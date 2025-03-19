package ru.nsu.dgi.department_assistant.domain.service.impl;

import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeWithAllInfoResponseDto;
import ru.nsu.dgi.department_assistant.domain.service.MapBuilderService;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MapBuildeerServiceImpl implements MapBuilderService {

    @Override
    public Map<String, String> buildMapForPerson(EmployeeWithAllInfoResponseDto employee) {
        Map<String, String> dataMap = new HashMap<>();

        // Добавляем текущую дату
        Date current = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String date = formatter.format(current);
        dataMap.put("date", date);

        // Обрабатываем поля EmployeeWithAllInfoResponseDto
        processFields(employee, dataMap, "");

        // Добавляем fullname и shortname
        String fullName = employee.lastName() + " " + employee.firstName() + " " + employee.middleName();
        String shortName = employee.lastName() + " " + employee.firstName().charAt(0) + ". " + employee.middleName().charAt(0) + ".";
        dataMap.put("fullname", fullName);
        dataMap.put("shortname", shortName);

        return dataMap;
    }

    private void processFields(Object obj, Map<String, String> dataMap, String prefix) {
        if (obj == null) {
            return;
        }

        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value == null) {
                    dataMap.put(prefix + field.getName(), "");
                } else if (isSimpleType(value)) {
                    // Простые типы (String, Boolean, Integer и т.д.)
                    dataMap.put(prefix + field.getName(), value.toString());
                } else {
                    // Вложенные объекты
                    processFields(value, dataMap, prefix + field.getName() + ".");
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Ошибка при обработке поля: " + field.getName(), e);
            }
        }
    }

    private boolean isSimpleType(Object value) {
        return value instanceof String || value instanceof Boolean || value instanceof Integer ||
                value instanceof Long || value instanceof Double || value instanceof UUID;
    }
}
