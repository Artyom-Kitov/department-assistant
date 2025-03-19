package ru.nsu.dgi.department_assistant.domain.service.impl;

import com.github.petrovich4j.Case;
import com.github.petrovich4j.Gender;
import com.github.petrovich4j.NameType;
import com.github.petrovich4j.Petrovich;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.nsu.dgi.department_assistant.domain.dto.document.DocumentTemplateDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeResponseDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeWithAllInfoResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.document.DocumentTemplate;
import ru.nsu.dgi.department_assistant.domain.mapper.document.DocumentTemplateMapper;
import ru.nsu.dgi.department_assistant.domain.mapper.employee.AcademicDegreeMapper;
import ru.nsu.dgi.department_assistant.domain.repository.document.DocumentTemplateRepository;
import ru.nsu.dgi.department_assistant.domain.service.DocumentService;
import ru.nsu.dgi.department_assistant.domain.service.factory.TemplateHandlerFactory;
import ru.nsu.dgi.department_assistant.domain.service.handler.DocxTemplateHandler;
import ru.nsu.dgi.department_assistant.domain.service.handler.TemplateHandler;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor

public class DocumentServiceImpl implements DocumentService {
    private final TemplateHandlerFactory templateHandlerFactory;
    private final DocumentTemplateRepository documentTemplateRepository;
    private final DocumentTemplateMapper documentTemplateMapper;
    private final DeclensionServiceImpl declensionService;
    private final MapBuildeerServiceImpl mapBuildeerService;
    private final EmployeeServiceImpl employeeService;
    private final DocumentTemplateServiceImpl documentTemplateService;
    private final TemplateHandlerDispatcherServiceImpl templateProcessingService;
    private final FileServiceImpl fileService;


//
//    @Override
//    public XWPFDocument convertToDocument(byte[] data) {
//        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
//            return new XWPFDocument(inputStream);
//        } catch (IOException e) {
//            throw new RuntimeException("Ошибка при загрузке шаблона документа", e);
//        }
//    }

//    @Override
//    public DocumentTemplateDto getTemplateById(Integer id) {
//        DocumentTemplate template = documentTemplateRepository.findById(id).orElseThrow();
//        return new DocumentTemplateDto(template.getId(), template.getTitle(), template.getTemplatePath());
//    }
//
//    @Override
//    public DocumentTemplateDto updateTemplate(Integer id, DocumentTemplateDto documentTemplateDto) {
//        return null;
//    }
//
//    @Override
//    public DocumentTemplateDto saveTemplate(String title, MultipartFile file) {
//        try {
//            // Указываем путь к папке с шаблонами
//            String uploadDir = "путь/к/папке/с/шаблонами"; // Укажи путь к папке
//            File dir = new File(uploadDir);
//
//            // Создаем папку, если её нет
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
//
//            // Сохраняем файл на сервер
//            String fileName = file.getOriginalFilename();
//            String filePath = uploadDir + File.separator + fileName;
//
//            File dest = new File(filePath);
//            file.transferTo(dest); // Сохраняем файл
//
//            // Создаем DTO с путем к файлу
//            DocumentTemplateDto templateDTO = new DocumentTemplateDto(null, title, filePath);
//
//            // Сохраняем в базу данных
//            DocumentTemplate template = documentTemplateMapper.toEntity(templateDTO);
//            template = documentTemplateRepository.save(template);
//
//            return documentTemplateMapper.toDto(template);
//        } catch (IOException e) {
//            throw new RuntimeException("Ошибка при обработке файла", e);
//        }
//    }

//    public byte[] fillAndConvertTemplate(UUID templateId, UUID employeeId) throws IOException {
//        // Получаем шаблон и данные сотрудника
//        DocumentTemplateDto template = documentTemplateService.getTemplateById(templateId);
//        EmployeeWithAllInfoResponseDto employee = employeeService.getEmployeeWithAllInfos(employeeId);
//        Map<String, String> data = mapBuildeerService.buildMapForPerson(employee);
//        TemplateHandler handler = templateHandlerFactory.getHandler(template.templateType());
//
//        // Обрабатываем шаблон
//        if (handler instanceof DocxTemplateHandler) {
//            XWPFDocument document = ((DocxTemplateHandler) handler).handleTemplate(template.getFile(), data);
//            return FileUtils.convertToBytes(document);
//        } else {
//            throw new UnsupportedOperationException("Конвертация поддерживается только для DOCX");
//        }
//    }

//    @Override
//    public XWPFDocument fillTemplate(UUID templateId, UUID employeeId) {
//        // Получаем шаблон по ID
//        DocumentTemplateDto template = documentTemplateService.getTemplateById(templateId);
//
//        // Получаем данные сотрудника
//        EmployeeWithAllInfoResponseDto employee = employeeService.getEmployeeWithAllInfos(employeeId);
//
//        // Строим мапу данных
//        Map<String, String> data = mapBuildeerService.buildMapForPerson(employee);
//
//        // Путь к файлу шаблона
//        String templatePath = template.templatePath();
//
//        // Чтение шаблона из файла
//        XWPFDocument document;
//        try (FileInputStream fis = new FileInputStream(templatePath)) {
//            document = new XWPFDocument(fis);
//        } catch (IOException e) {
//            throw new RuntimeException("Ошибка при чтении шаблона", e);
//        }
//
//        // Замена данных в шаблоне с учетом падежей
//        for (XWPFParagraph paragraph : document.getParagraphs()) {
//            for (XWPFRun run : paragraph.getRuns()) {
//                String text = run.getText(0);
//                if (text != null) {
//                    text = replaceWithCases(text, data);
//                    run.setText(text, 0);
//                }
//            }
//        }
//        return document;
//    }








//    @Override
//    @Transactional
//    public void deleteTemplate(Integer id) {
//        if (!documentTemplateRepository.existsById(id)) {
//            throw new RuntimeException("Шаблон не найден");
//        }
//        documentTemplateRepository.deleteById(id);
//    }
public byte[] fillAndConvertTemplate(UUID templateId, UUID employeeId){
    // Обрабатываем шаблон
    XWPFDocument document = templateProcessingService.processTemplate(templateId, employeeId);

    // Конвертируем документ в массив байтов
    return fileService.convertToBytes(document);
}
}



