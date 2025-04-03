package ru.nsu.dgi.department_assistant.domain.service.impl;

import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeWithAllInfoResponseDto;
import ru.nsu.dgi.department_assistant.domain.service.MapBuilderService;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class MapBuildeerServiceImpl implements MapBuilderService {

    @Override
    public Map<String, String> buildMapForPerson(EmployeeWithAllInfoResponseDto employee) {
        Map<String, String> dataMap = new HashMap<>();
        addCommonFields(dataMap); // Добавляем общие поля (например, дату)
        processFields(employee, dataMap, ""); // Обрабатываем поля сотрудника
        addNameFields(employee, dataMap); // Добавляем fullname и shortname
        return dataMap;
    }

    @Override
    public Map<String, String> buildEmptyMap() {
        Map<String, String> dataMap = new HashMap<>();
        addCommonFields(dataMap); // Добавляем общие поля
        collectAllFields(EmployeeWithAllInfoResponseDto.class, dataMap, ""); // Собираем все возможные поля
        return dataMap;
    }

    private void addCommonFields(Map<String, String> dataMap) {
        SimpleDateFormat fullDateFormatter = new SimpleDateFormat("dd MMMM yyyy", new Locale("ru"));
        SimpleDateFormat dayFormatter = new SimpleDateFormat("dd", new Locale("ru"));
        SimpleDateFormat monthFormatter = new SimpleDateFormat("MMMM", new Locale("ru"));
        SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy", new Locale("ru"));

        Date currentDate = new Date();
        dataMap.put("date", fullDateFormatter.format(currentDate));
        dataMap.put("day", dayFormatter.format(currentDate));
        dataMap.put("month", monthFormatter.format(currentDate));
        dataMap.put("year", yearFormatter.format(currentDate));

        dataMap.put("fullname", "");
        dataMap.put("shortname", "");
    }

    private void addNameFields(EmployeeWithAllInfoResponseDto employee, Map<String, String> dataMap) {
        String fullName = employee.lastName() + " " + employee.firstName() + " " + employee.middleName();
        String shortName = employee.lastName() + " " + employee.firstName().charAt(0) + ". " + employee.middleName().charAt(0) + ".";
        dataMap.put("fullname", fullName);
        dataMap.put("shortname", shortName);
    }

    private void processFields(Object obj, Map<String, String> dataMap, String prefix) {
        if (obj == null) return;

        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value == null) {
                    dataMap.put(prefix + field.getName(), "");
                } else if (isSimpleType(value)) {
                    dataMap.put(prefix + field.getName(), value.toString());
                } else {
                    processFields(value, dataMap, prefix + field.getName() + ".");
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Ошибка при обработке поля: " + field.getName(), e);
            }
        }
    }

    private void collectAllFields(Class<?> clazz, Map<String, String> dataMap, String prefix) {
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (isSimpleType(field.getType())) {
                dataMap.put(prefix + field.getName(), ""); // Добавляем поле с пустым значением
            } else {
                collectAllFields(field.getType(), dataMap, prefix + field.getName() + "."); // Рекурсия для вложенных объектов
            }
        }
    }

    private boolean isSimpleType(Object value) {
        return value instanceof String || value instanceof Boolean || value instanceof Integer ||
                value instanceof Long || value instanceof Double || value instanceof UUID;
    }

    private boolean isSimpleType(Class<?> clazz) {
        return clazz.equals(String.class) || clazz.equals(Boolean.class) || clazz.equals(Integer.class) ||
                clazz.equals(Long.class) || clazz.equals(Double.class) || clazz.equals(UUID.class);
    }
}