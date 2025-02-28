package ru.nsu.dgi.department_assistant.integration;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureEmbeddedDatabase
class ProcessCreationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String BASE_URL = "/api/v1/templates";

    private MockHttpServletRequestBuilder withJsonBody(String json) {
        return post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON);
    }

    @Test
    void test200IfValidProcess() throws Exception {
        String json = """
                {
                  "name": "Definitely valid process",
                  "steps": [
                    {
                      "id": 1,
                      "duration": 2,
                      "metaInfo": {},
                      "type": 1,
                      "description": "Step #1",
                      "data": {
                        "next": 2
                      }
                    },
                    {
                      "id": 2,
                      "duration": 30,
                      "metaInfo": {},
                      "type": 3,
                      "description": "Some conditional step",
                      "data": {
                        "ifTrue": 3,
                        "ifFalse": 4
                      }
                    },
                    {
                      "id": 3,
                      "duration": 4,
                      "metaInfo": {},
                      "type": 2,
                      "description": "Some subtasks step",
                      "data": {
                        "subtasks": [
                          {
                            "description": "Subtask #1",
                            "duration": 2
                          },
                          {
                            "description": "Subtask #2"
                          }
                        ],
                        "next": 5
                      }
                    },
                    {
                      "id": 4,
                      "duration": 1,
                      "metaInfo": {},
                      "type": 1,
                      "description": "Just a step",
                      "data": {
                        "next": 5
                      }
                    },
                    {
                      "id": 5,
                      "metaInfo": {},
                      "type": 4,
                      "description": "Stupid fucking final",
                      "data": {
                        "isSuccessful": true
                      }
                    }
                  ]
                }
                """;

        mockMvc.perform(withJsonBody(json))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void test400IfEmptySteps() throws Exception {
        String json = """
                {
                  "name": "Some name",
                  "steps": [
                  ]
                }
                """;

        mockMvc.perform(withJsonBody(json))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void test400IfContainsLoops() throws Exception {
        String json = """
                {
                  "name": "Some name",
                  "steps": [
                    {
                      "id": 0,
                      "duration": 1,
                      "metaInfo": {},
                      "type": 1,
                      "description": "Test common step",
                      "data": {
                        "next": 1
                      }
                    },
                    {
                      "id": 1,
                      "duration": 1,
                      "metaInfo": {},
                      "type": 3,
                      "description": "Test conditional step",
                      "data": {
                        "ifTrue": 2,
                        "ifFalse": 3
                      }
                    },
                    {
                      "id": 2,
                      "metaInfo": {},
                      "type": 4,
                      "description": "Test final",
                      "data": {
                        "isSuccessful": true
                      }
                    },
                    {
                      "id": 3,
                      "metaInfo": {},
                      "type": 1,
                      "description": "Bad next step which makes a loop",
                      "data": {
                        "next": 1
                      }
                    }
                  ]
                }
                """;

        mockMvc.perform(withJsonBody(json))
                .andExpect(status().isBadRequest());
    }
}
