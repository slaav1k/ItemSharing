package rsreu.itemsharing.controllers;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import rsreu.itemsharing.entities.BlackList;
import rsreu.itemsharing.entities.Request;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;
import java.util.List;

@Service
public class ReportService {

    public void generateRequestsReport(HttpServletResponse response,
                                       List<Request> incoming,
                                       List<Request> outgoing) throws IOException {
        // Устанавливаем заголовки ответа
        response.setContentType("application/pdf");
        response.setCharacterEncoding("UTF-8");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        try {
            // Создаем шрифт с правильной кодировкой
            PdfFont font = PdfFontFactory.createFont(
                    "src/main/resources/fonts/FreeSerif.ttf",
                    PdfEncodings.IDENTITY_H,  // Используем правильную кодировку
                    PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED
            );
            document.setFont(font);

            // Добавляем контент
            document.add(new Paragraph("Отчет по заявкам").setFontSize(16));

            document.add(new Paragraph("Входящие заявки:").setFontSize(14));
            for (Request req : incoming) {
                document.add(new Paragraph(req.toString()));
                document.add(new Paragraph(" ")); // Пустая строка между записями
            }

            document.add(new Paragraph("\nИсходящие заявки:").setFontSize(14));
            for (Request req : outgoing) {
                document.add(new Paragraph(req.toString()));
                document.add(new Paragraph(" "));
            }
        } finally {
            document.close();
        }
    }

    public void generateBlacklistReport(HttpServletResponse response,
                                        List<BlackList> blocked,
                                        List<BlackList> blockedBy) throws IOException {
        // Устанавливаем заголовки ответа
        response.setContentType("application/pdf");
        response.setCharacterEncoding("UTF-8");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        try {
            // Создаем шрифт с поддержкой кириллицы
            PdfFont font = PdfFontFactory.createFont(
                    "src/main/resources/fonts/FreeSerif.ttf",
                    PdfEncodings.IDENTITY_H,
                    PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED
            );
            document.setFont(font);

            // Добавляем заголовок отчета
            document.add(new Paragraph("Отчет по блокировкам").setFontSize(16));

            // Раздел "Пользователи, которых заблокировал"
            document.add(new Paragraph("Пользователи, которых заблокировал:").setFontSize(14));
            if (blockedBy.isEmpty()) {
                document.add(new Paragraph("Нет пользователей").setItalic());
            } else {
                for (BlackList entry : blockedBy) {
                    if (entry.getBlockedUserEntity() != null) {
                        document.add(new Paragraph(entry.getBlockedUserEntity().getFullName()));
                    }
                }
            }

            // Раздел "Пользователи, которые заблокировали меня"
            document.add(new Paragraph("\nПользователи, которые заблокировали меня:").setFontSize(14));
            if (blocked.isEmpty()) {
                document.add(new Paragraph("Нет пользователей").setItalic());
            } else {
                for (BlackList entry : blocked) {
                    if (entry.getBlockedByUserEntity() != null) {
                        document.add(new Paragraph(entry.getBlockedByUserEntity().getFullName()));
                    }
                }
            }

            // Добавляем пустые строки для разделения
            document.add(new Paragraph(" "));
        } finally {
            document.close();
        }
    }
}