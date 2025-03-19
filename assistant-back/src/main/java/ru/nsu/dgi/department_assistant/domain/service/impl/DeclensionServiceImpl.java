package ru.nsu.dgi.department_assistant.domain.service.impl;

import com.github.petrovich4j.Case;
import com.github.petrovich4j.Gender;
import com.github.petrovich4j.NameType;
import com.github.petrovich4j.Petrovich;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.service.DeclensionService;

@Service
public class DeclensionServiceImpl implements DeclensionService {

    @Override
    public String declineName(String key, String value, String caseType) {
        // Определяем падеж
        Case pCase;
        switch (caseType) {
            case "i" -> pCase = Case.Prepositional; // Предложный
            case "r" -> pCase = Case.Genitive;      // Родительный
            case "d" -> pCase = Case.Dative;        // Дательный
            case "v" -> pCase = Case.Accusative;    // Винительный
            case "t" -> pCase = Case.Instrumental;  // Творительный
            default -> {
                // Если падеж не указан или не поддерживается, возвращаем исходное значение
                return value;
            }
        }

        // Определяем тип (фамилия, имя, отчество, полное имя, короткое имя)
        NameType type;
        switch (key) {
            case "lastName" -> type = NameType.LastName;
            case "firstName" -> type = NameType.FirstName;
            case "middleName" -> type = NameType.PatronymicName;
            case "fullname" -> {
                // Разделяем полное имя на части
                String[] parts = value.split(" ");
                if (parts.length < 2) {
                    return value; // Если ФИО неполное, возвращаем исходное значение
                }

                // Склоняем фамилию и имя
                String lastName = declinePart(parts[0], NameType.LastName, pCase);
                String firstName = declinePart(parts[1], NameType.FirstName, pCase);

                // Склоняем отчество, если оно есть
                String middleName = parts.length > 2 ? declinePart(parts[2], NameType.PatronymicName, pCase) : null;

                // Собираем результат
                return middleName != null ? lastName + " " + firstName + " " + middleName : lastName + " " + firstName;
            }
            case "shortname" -> {
                // Разделяем короткое имя на части
                String[] parts = value.split(" ");
                if (parts.length < 2) {
                    return value; // Если ФИО неполное, возвращаем исходное значение
                }

                // Склоняем фамилию
                String lastName = declinePart(parts[0], NameType.LastName, pCase);
                String firstNameInitial = parts[1].substring(0, 1) + ".";
                String middleNameInitial = parts.length > 2 ? parts[2].substring(0, 1) + "." : null;

                // Собираем результат
                return middleNameInitial != null ? lastName + " " + firstNameInitial + " " + middleNameInitial : lastName + " " + firstNameInitial;
            }
            default -> {
                // Если ключ не связан с ФИО, возвращаем исходное значение
                return value;
            }
        }

        // Определяем пол (гендер)
        Petrovich petrovich = new Petrovich();
        Gender gender = petrovich.gender(value, Gender.Both);

        // Склоняем значение
        return petrovich.say(value, type, gender, pCase);
    }

    private String declinePart(String value, NameType type, Case pCase) {
        Petrovich petrovich = new Petrovich();
        Gender gender = petrovich.gender(value, Gender.Both);
        return petrovich.say(value, type, gender, pCase);
    }
}
