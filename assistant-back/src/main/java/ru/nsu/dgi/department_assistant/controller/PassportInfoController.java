package ru.nsu.dgi.department_assistant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.dgi.department_assistant.domain.dto.employee.PassportInfoRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.PassportInfoResponseDto;
import ru.nsu.dgi.department_assistant.domain.service.PassportInfoService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/passport-info")
public class PassportInfoController {
    private final PassportInfoService passportInfoService;

    @GetMapping()
    public ResponseEntity<List<PassportInfoResponseDto>> getAll() {
        return ResponseEntity.ok(passportInfoService.getAll());
    }

    @GetMapping("/employee")
    public ResponseEntity<PassportInfoResponseDto> getPassportInfoByEmployeeId(
            @RequestParam("employeeId") UUID employeeId
    ) {
        return ResponseEntity.ok(passportInfoService.getByEmployeeId(employeeId));
    }

    @PostMapping("/create")
    public ResponseEntity<PassportInfoResponseDto> createPassportInfo(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody PassportInfoRequestDto passportInfoRequestDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(passportInfoService.create(employeeId, passportInfoRequestDto));
    }

    @PutMapping("/update")
    public ResponseEntity<PassportInfoResponseDto> updatePassportInfo(
            @RequestParam("employeeId") UUID employeeId,
            @RequestBody PassportInfoRequestDto passportInfoRequestDto
    ) {
        return ResponseEntity.ok(passportInfoService.update(employeeId, passportInfoRequestDto));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deletePassportInfo(
            @RequestParam("employeeId") UUID employeeId
    ) {
        passportInfoService.delete(employeeId);
        return ResponseEntity.noContent().build();
    }
}
