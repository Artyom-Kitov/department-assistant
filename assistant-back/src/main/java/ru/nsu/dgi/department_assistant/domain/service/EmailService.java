package ru.nsu.dgi.department_assistant.domain.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.web.multipart.MultipartFile;
import ru.nsu.dgi.department_assistant.domain.dto.document.EmailTemplateDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.ContactsResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.document.DocumentTemplate;
import ru.nsu.dgi.department_assistant.domain.entity.document.EmailTemplate;

import java.util.List;
import java.util.Map;

public interface EmailService {

    EmailTemplateDto getTemplateById(Integer id);
    //как-то добавлять новые шаблоны
    EmailTemplateDto updateTemplate(EmailTemplateDto emailTemplateDto);
    List<MimeMessage> buildEmails(EmailTemplateDto emailTemplateDto, List<ContactsResponseDto> contactsResponseDtoList, List<MultipartFile> files); // пока думаем
    // там внутри уже будут мапы из документ сервайса надеюсь это не убого
    void sendEmails(List<MimeMessage> emails);
    void deleteTemplate(Integer id);

}
