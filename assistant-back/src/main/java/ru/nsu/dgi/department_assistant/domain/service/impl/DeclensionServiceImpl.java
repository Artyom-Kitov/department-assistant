package ru.nsu.dgi.department_assistant.domain.service.impl;

import com.github.petrovich4j.*;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.service.DeclensionService;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class DeclensionServiceImpl implements DeclensionService {

    private final Petrovich petrovich = new Petrovich();
    private final Map<String, Gender> genderCache = new ConcurrentHashMap<>();

    @Override
    public String declineName(String key, String value, String caseType) {
        Case pCase = parseCase(caseType);
        if (pCase == null) return value;

        return switch (key) {
            case "fullname" -> declineFullName(value, pCase);
            case "shortname" -> declineShortName(value, pCase);
            case "lastName" -> declineLastName(value, pCase);
            case "firstName" -> declineFirstName(value, pCase);
            case "middleName" -> declineMiddleName(value, pCase);
            default -> value;
        };
    }

    private String declineFullName(String fullName, Case pCase) {
        String[] parts = normalizeName(fullName).split(" ");
        if (parts.length < 2) return fullName;

        Gender gender = determineGender(parts);

        return switch (parts.length) {
            case 4 -> handleCompoundName(parts, gender, pCase);
            case 3 -> String.join(" ",
                    declinePart(parts[0], NameType.LastName, pCase, gender),
                    declinePart(parts[1], NameType.FirstName, pCase, gender),
                    declinePart(parts[2], NameType.PatronymicName, pCase, gender)
            );
            case 2 -> String.join(" ",
                    declinePart(parts[0], NameType.LastName, pCase, gender),
                    declinePart(parts[1], NameType.FirstName, pCase, gender)
            );
            default -> fullName;
        };
    }

    private Gender determineGender(String[] nameParts) {
        String cacheKey = String.join("|", nameParts);
        return genderCache.computeIfAbsent(cacheKey, k -> {
            if (nameParts.length >= 3) {
                return petrovich.gender(nameParts[2], Gender.Both);
            }
            return detectGenderByLastName(nameParts[0]);
        });
    }

    private String handleCompoundName(String[] parts, Gender gender, Case pCase) {
        String[] surnameParts = parts[0].split("-");
        String declinedSurname = Arrays.stream(surnameParts)
                .map(part -> declinePart(part, NameType.LastName, pCase, gender))
                .collect(Collectors.joining("-"));

        return String.join(" ",
                declinedSurname,
                declinePart(parts[1], NameType.FirstName, pCase, gender),
                parts.length > 2 ? declinePart(parts[2], NameType.PatronymicName, pCase, gender) : "",
                parts.length > 3 ? parts[3] : ""
        ).trim();
    }

    private String declineShortName(String value, Case pCase) {
        String[] parts = normalizeName(value).split(" ");
        if (parts.length < 2) return value;

        Gender gender = determineGender(parts);
        String lastName = declinePart(parts[0], NameType.LastName, pCase, gender);
        String initials = Arrays.stream(parts, 1, parts.length)
                .map(s -> s.charAt(0) + ".")
                .collect(Collectors.joining(" "));

        return lastName + " " + initials;
    }

    private String declineLastName(String value, Case pCase) {
        Gender gender = detectGenderByLastName(value);
        return petrovich.say(value, NameType.LastName, gender, pCase);
    }

    private String declineMiddleName(String value, Case pCase) {
        Gender gender = petrovich.gender(value, Gender.Both);
        return petrovich.say(value, NameType.PatronymicName, gender, pCase);
    }

    private String declineFirstName(String value, Case pCase) {
        Gender gender = detectGenderByFirstName(value);
        return petrovich.say(value, NameType.FirstName, gender, pCase);
    }

    private String declinePart(String value, NameType type, Case pCase, Gender gender) {
        return petrovich.say(value, type, gender, pCase);
    }

    private String normalizeName(String name) {
        return name.trim().replaceAll("\\s+", " ");
    }

    private Case parseCase(String caseType) {
        return switch (caseType.toLowerCase()) {
            case "i", "предложный" -> Case.Prepositional;
            case "r", "родительный" -> Case.Genitive;
            case "d", "дательный" -> Case.Dative;
            case "v", "винительный" -> Case.Accusative;
            case "t", "творительный" -> Case.Instrumental;
            default -> null;
        };
    }
    public Gender detectGenderByLastName(String lastName) {
        String lowerLastName = lastName.toLowerCase();

        for (Rule rule : Library.LAST_NAME_RULES.exceptions) {
            if (matchesAnyTest(lowerLastName, rule.test)) {
                return rule.gender;
            }
        }

        for (Rule rule : Library.LAST_NAME_RULES.suffixes) {
            if (matchesAnyTest(lowerLastName, rule.test)) {
                return rule.gender;
            }
        }

        return defaultGenderDetection(lowerLastName);
    }

    private boolean matchesAnyTest(String name, String[] tests) {
        for (String test : tests) {
            if (name.endsWith(test)) {
                return true;
            }
        }
        return false;
    }

    private Gender defaultGenderDetection(String lowerLastName) {
        if (lowerLastName.endsWith("ова") ||
                lowerLastName.endsWith("ева") ||
                lowerLastName.endsWith("ина")) {
            return Gender.Female;
        }
        return Gender.Male;
    }


    private Gender detectGenderByFirstName(String firstName) {
        String lowerName = firstName.toLowerCase();

        for (Rule rule : Library.FIRST_NAME_RULES.exceptions) {
            if (matchesAnyTest(lowerName, rule.test)) {
                return rule.gender;
            }
        }

        for (Rule rule : Library.FIRST_NAME_RULES.suffixes) {
            if (matchesAnyTest(lowerName, rule.test)) {
                return rule.gender;
            }
        }

        return defaultGenderDetectionForName(lowerName);
    }



    private Gender defaultGenderDetectionForName(String lowerName) {
        if (lowerName.endsWith("а") ||
                lowerName.endsWith("я") ||
                lowerName.endsWith("ья")) {
            return Gender.Female;
        }
        return Gender.Male;
    }
}