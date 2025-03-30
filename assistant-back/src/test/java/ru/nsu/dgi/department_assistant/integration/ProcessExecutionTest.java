package ru.nsu.dgi.department_assistant.integration;

import com.google.gson.Gson;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.nsu.dgi.department_assistant.config.StepType;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeResponseDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.template.ProcessTemplateCreationResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.process.ExecutionHistory;
import ru.nsu.dgi.department_assistant.domain.entity.process.Process;
import ru.nsu.dgi.department_assistant.domain.entity.process.Step;
import ru.nsu.dgi.department_assistant.domain.entity.process.StepStatus;
import ru.nsu.dgi.department_assistant.domain.entity.process.SubstepStatus;
import ru.nsu.dgi.department_assistant.domain.entity.process.id.StepStatusId;
import ru.nsu.dgi.department_assistant.domain.repository.process.ExecutionHistoryRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.ProcessRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.StepRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.StepStatusRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.SubstepRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.SubstepStatusRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureEmbeddedDatabase
class ProcessExecutionTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String BASE_URL = "/api/v1/execute";
    private static final Gson GSON = new Gson();

    @Autowired
    private ProcessRepository processRepository;
    @Autowired
    private StepStatusRepository stepStatusRepository;
    @Autowired
    private StepRepository stepRepository;
    @Autowired
    private ExecutionHistoryRepository historyRepository;
    @Autowired
    private SubstepRepository substepRepository;
    @Autowired
    private SubstepStatusRepository substepStatusRepository;

    private static final String START_EXECUTION_JSON_FORMAT = """
                {
                  "employeeId": "%s",
                  "processId": "%s"
                }
                """;

    private static final String START_EXECUTION_WITH_DEADLINE_JSON_FORMAT = """
                {
                  "employeeId": "%s",
                  "processId": "%s",
                  "deadline": "%d-%02d-%02d"
                }
                """;

    private static final String COMMON_STEP_EXECUTION_JSON_FORMAT = """
            {
              "employeeId": "%s",
              "startProcessId": "%s",
              "processId": "%s",
              "stepId": %d
            }
            """;

    private static final String SUBSTEP_EXECUTION_JSON_FORMAT = """
            {
              "employeeId": "%s",
              "startProcessId": "%s",
              "substepId": "%s"
            }
            """;

    @Test
    void executeSimpleProcess() throws Exception {
        UUID id = addSimpleProcess().id();
        UUID employeeId = addTestEmployee().id();
        String json = START_EXECUTION_JSON_FORMAT.formatted(employeeId.toString(), id.toString());
        mockMvc.perform(withJsonBody(json, BASE_URL + "/start"))
                .andExpect(status().isOk());
        List<Step> steps = stepRepository.findAllByProcessId(id);
        List<StepStatus> statuses = stepStatusRepository.findAll();
        assertEquals(steps.size(), statuses.size());
        statuses.forEach(status -> {
            if (status.getStep().getType() != StepType.START.getValue()) {
                assertNull(status.getCompletedAt());
            }
        });

        json = COMMON_STEP_EXECUTION_JSON_FORMAT.formatted(employeeId, id, id, 1);
        mockMvc.perform(withJsonBody(json, BASE_URL + "/common"))
                .andExpect(status().isOk());
        statuses = stepStatusRepository.findAll();
        assertTrue(statuses.isEmpty());
        List<ExecutionHistory> histories = historyRepository.findAll();
        assertEquals(1, histories.size());
    }

    // NEVER LET THIS TEST START IN ONE DAY AND COMPLETE IN ANOTHER!!!
    // If you do, never ever tell me I did not warn you... >:(
    @Test
    void executeMultiStepProcess() throws Exception {
        UUID id = addMultiStepSimpleProcess().id();
        UUID employeeId = addTestEmployee().id();
        LocalDate today = LocalDate.now();
        LocalDate deadline = today.plusDays(30);
        String json = START_EXECUTION_WITH_DEADLINE_JSON_FORMAT.formatted(employeeId.toString(), id.toString(),
                deadline.getYear(), deadline.getMonthValue(), deadline.getDayOfMonth());
        mockMvc.perform(withJsonBody(json, BASE_URL + "/start"))
                .andExpect(status().isOk());
        Process process = processRepository.findById(id).orElseThrow();
        assertEquals(13, process.getTotalDuration());

        json = COMMON_STEP_EXECUTION_JSON_FORMAT.formatted(employeeId, id, id, 1);
        mockMvc.perform(withJsonBody(json, BASE_URL + "/common"))
                .andExpect(status().isOk());
        assertEquals(0, historyRepository.count());
        List<StepStatus> statuses = stepStatusRepository.findAll();
        statuses.sort(Comparator.comparingInt(StepStatus::getStepId));
        assertEquals(2, statuses.stream().filter(s -> s.getCompletedAt() != null).count());

        StepStatus commonStatus1 = statuses.get(1);
        StepStatus commonStatus2 = statuses.get(2);
        StepStatus commonStatus3 = statuses.get(3);
        assertEquals(deadline, commonStatus3.getDeadline());
        assertEquals(deadline.minusDays(commonStatus3.getStep().getDuration()), commonStatus2.getDeadline());
        assertEquals(deadline.minusDays(commonStatus3.getStep().getDuration() +
                commonStatus2.getStep().getDuration()), commonStatus1.getDeadline());
    }

    @Test
    void executeSubsteps() throws Exception {
        UUID id = addSubstepsProcess().id();
        UUID employeeId = addTestEmployee().id();

        String json = START_EXECUTION_JSON_FORMAT.formatted(employeeId.toString(), id.toString());
        mockMvc.perform(withJsonBody(json, BASE_URL + "/start"))
                .andExpect(status().isOk());
        List<SubstepStatus> statuses = substepStatusRepository.findAll();
        assertEquals(2, statuses.size());
        assertTrue(statuses.stream().noneMatch(SubstepStatus::isCompleted));
        SubstepStatus substepStatus1 = statuses.stream().filter(s -> s.getSubstep().getDescription().endsWith("1"))
                .findAny().orElseThrow();

        json = COMMON_STEP_EXECUTION_JSON_FORMAT.formatted(employeeId, id, id, 1);
        mockMvc.perform(withJsonBody(json, BASE_URL + "/common"))
                .andExpect(status().isOk());

        json = SUBSTEP_EXECUTION_JSON_FORMAT.formatted(employeeId, id, substepStatus1.getSubstep().getId());
        mockMvc.perform(withJsonBody(json, BASE_URL + "/substep"))
                .andExpect(status().isOk());
        statuses = substepStatusRepository.findAll();
        substepStatus1 = statuses.stream().filter(s -> s.getSubstep().getDescription().endsWith("1"))
                .findAny().orElseThrow();
        SubstepStatus substepStatus2 = statuses.stream().filter(s -> s.getSubstep().getDescription().endsWith("2"))
                .findAny().orElseThrow();
        assertTrue(substepStatus1.isCompleted());
        assertFalse(substepStatus2.isCompleted());
        assertNull(stepStatusRepository.findById(new StepStatusId(employeeId, id, id, 2)).orElseThrow()
                .getCompletedAt());

        json = SUBSTEP_EXECUTION_JSON_FORMAT.formatted(employeeId, id, substepStatus2.getSubstep().getId());
        mockMvc.perform(withJsonBody(json, BASE_URL + "/substep"))
                .andExpect(status().isOk());
        statuses = substepStatusRepository.findAll();
        substepStatus1 = statuses.stream().filter(s -> s.getSubstep().getDescription().endsWith("1"))
                .findAny().orElseThrow();
        substepStatus2 = statuses.stream().filter(s -> s.getSubstep().getDescription().endsWith("2"))
                .findAny().orElseThrow();
        assertTrue(substepStatus1.isCompleted());
        assertTrue(substepStatus2.isCompleted());
        assertEquals(LocalDate.now(), stepStatusRepository.findById(new StepStatusId(employeeId, id, id, 2))
                .orElseThrow().getCompletedAt());
    }

    private ProcessTemplateCreationResponseDto addSimpleProcess() throws Exception {
        String json = """
                {
                  "name": "Simple",
                  "steps": [
                    {
                      "id": 0,
                      "metaInfo": {},
                      "type": 0,
                      "description": "start",
                      "data": {
                        "next": 1
                      }
                    },
                    {
                      "id": 1,
                      "metaInfo": {},
                      "type": 1,
                      "duration": 2,
                      "description": "First step",
                      "data": {
                        "next": 2
                      }
                    },
                    {
                      "id": 2,
                      "metaInfo": {},
                      "type": 4,
                      "description": "Success",
                      "data": {
                        "isSuccessful": true
                      }
                    }
                  ]
                }
                """;
        return GSON.fromJson(mockMvc.perform(withJsonBody(json, "/api/v1/templates"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(),
                ProcessTemplateCreationResponseDto.class);
    }

    private ProcessTemplateCreationResponseDto addMultiStepSimpleProcess() throws Exception {
        String json = """
                {
                  "name": "Simple",
                  "steps": [
                    {
                      "id": 0,
                      "metaInfo": {},
                      "type": 0,
                      "description": "start",
                      "data": {
                        "next": 1
                      }
                    },
                    {
                      "id": 1,
                      "metaInfo": {},
                      "type": 1,
                      "duration": 2,
                      "description": "First step",
                      "data": {
                        "next": 2
                      }
                    },
                    {
                      "id": 2,
                      "metaInfo": {},
                      "type": 1,
                      "duration": 10,
                      "description": "Second step",
                      "data": {
                        "next": 3
                      }
                    },
                    {
                      "id": 3,
                      "metaInfo": {},
                      "type": 1,
                      "description": "Third step",
                      "data": {
                        "next": 4
                      }
                    },
                    {
                      "id": 4,
                      "metaInfo": {},
                      "type": 4,
                      "description": "Success",
                      "data": {
                        "isSuccessful": true
                      }
                    }
                  ]
                }
                """;
        return GSON.fromJson(mockMvc.perform(withJsonBody(json, "/api/v1/templates"))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                ProcessTemplateCreationResponseDto.class);
    }

    private ProcessTemplateCreationResponseDto addSubstepsProcess() throws Exception {
        String json = """
                {
                  "name": "Subtasks",
                  "steps": [
                    {
                      "id": 0,
                      "metaInfo": {},
                      "type": 0,
                      "description": "start",
                      "data": {
                        "next": 1
                      }
                    },
                    {
                      "id": 1,
                      "metaInfo": {},
                      "type": 1,
                      "duration": 2,
                      "description": "First step",
                      "data": {
                        "next": 2
                      }
                    },
                    {
                      "id": 2,
                      "metaInfo": {},
                      "type": 2,
                      "description": "Substeps",
                      "data": {
                        "subtasks": [
                          {
                            "description": "substep 1",
                            "duration": 3
                          },
                          {
                            "description": "substep 2",
                            "duration": 4
                          }
                        ],
                        "next": 3
                      }
                    },
                    {
                      "id": 3,
                      "metaInfo": {},
                      "type": 1,
                      "duration": 2,
                      "description": "Some step",
                      "data": {
                        "next": 4
                      }
                    },
                    {
                      "id": 4,
                      "metaInfo": {},
                      "type": 4,
                      "description": "Success",
                      "data": {
                        "isSuccessful": true
                      }
                    }
                  ]
                }
                """;
        return GSON.fromJson(mockMvc.perform(withJsonBody(json, "/api/v1/templates"))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                ProcessTemplateCreationResponseDto.class);
    }

    private EmployeeResponseDto addTestEmployee() throws Exception {
        EmployeeRequestDto employeeRequestDto = new EmployeeRequestDto("Name", "Surname",
                "Yet another name", true, true, true,
                true, null, null, false);
        String json = GSON.toJson(employeeRequestDto);
        return GSON.fromJson(mockMvc.perform(withJsonBody(json, "/api/v1/employees"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
                EmployeeResponseDto.class);
    }

    private MockHttpServletRequestBuilder withJsonBody(String json, String url) {
        return post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON);
    }
}
