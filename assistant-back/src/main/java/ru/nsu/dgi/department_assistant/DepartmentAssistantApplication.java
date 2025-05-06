package ru.nsu.dgi.department_assistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class DepartmentAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(DepartmentAssistantApplication.class, args);
    }
}
