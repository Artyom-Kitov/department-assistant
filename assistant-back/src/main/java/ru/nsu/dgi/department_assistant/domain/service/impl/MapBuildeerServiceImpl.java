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
        addCommonFields(dataMap);
        processFields(employee, dataMap, "");
        addNameFields(employee, dataMap);
        return dataMap;
    }

    @Override
    public Map<String, String> buildEmptyMap() {
        Map<String, String> dataMap = new HashMap<>();
        addCommonFields(dataMap);
        collectAllFields(EmployeeWithAllInfoResponseDto.class, dataMap, "");
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
        if (obj == null) {
            return;
        }

        if (obj.getClass().getPackage() != null &&
                (obj.getClass().getPackage().getName().startsWith("java.") ||
                        obj.getClass().getPackage().getName().startsWith("javax."))) {
            dataMap.put(prefix + "value", obj.toString());
            return;
        }

        for (Field field : obj.getClass().getDeclaredFields()) {
            try {
                if (!field.trySetAccessible()) {
                    continue; // Пропускаем поля, к которым нет доступа
                }

                Object value = field.get(obj);
                String fieldName = prefix + field.getName();

                if (value == null) {
                    dataMap.put(fieldName, "");
                } else if (isSimpleType(value)) {
                    dataMap.put(fieldName, value.toString());
                } else if (value instanceof Collection) {
                    // Обработка коллекций
                    int index = 0;
                    for (Object item : (Collection<?>) value) {
                        processFields(item, dataMap, fieldName + "[" + index + "].");
                        index++;
                    }
                } else {
                    processFields(value, dataMap, fieldName + ".");
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Ошибка при обработке поля: " + field.getName(), e);
            }
        }
    }

    private boolean isSimpleType(Object value) {
        Class<?> clazz = value.getClass();
        return clazz.isPrimitive() ||
                clazz.isEnum() ||
                Number.class.isAssignableFrom(clazz) ||
                CharSequence.class.isAssignableFrom(clazz) ||
                Date.class.isAssignableFrom(clazz) ||
                Boolean.class == clazz;
    }

    private void collectAllFields(Class<?> clazz, Map<String, String> dataMap, String prefix) {
        if (clazz.getPackage() != null &&
                (clazz.getPackage().getName().startsWith("java.") ||
                        clazz.getPackage().getName().startsWith("javax."))) {
            return;
        }

        for (Field field : clazz.getDeclaredFields()) {
            try {
                if (!field.trySetAccessible()) {
                    continue;
                }

                String fieldName = prefix + field.getName();
                if (isSimpleType(field.getType())) {
                    dataMap.put(fieldName, "");
                } else if (!field.getType().isArray() && !Collection.class.isAssignableFrom(field.getType())) {
                    collectAllFields(field.getType(), dataMap, fieldName + ".");
                }
            } catch (Exception e) {
                throw new RuntimeException("Ошибка при сборе полей: " + field.getName(), e);
            }
        }
    }


    private boolean isSimpleType(Class<?> clazz) {
        return clazz.equals(String.class) || clazz.equals(Boolean.class) || clazz.equals(Integer.class) ||
                clazz.equals(Long.class) || clazz.equals(Double.class) || clazz.equals(UUID.class);
    }
}