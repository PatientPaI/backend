package com.patientpal.backend.matching.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.patientpal.backend.matching.dto.response.MatchResponse;
import com.patientpal.backend.member.domain.Role;
import io.micrometer.core.annotation.Timed;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import java.io.IOException;
import org.thymeleaf.context.Context;

@Service
@Timed("pdf")
@RequiredArgsConstructor
@Slf4j
public class PdfService {

    private final TemplateEngine templateEngine;

    public ByteArrayInputStream  generateMatchPdf(MatchResponse matchResponse, String role) {

        try (
                ByteArrayOutputStream os = new ByteArrayOutputStream()
        ) {
            Context context = new Context();
            context.setVariable("match", matchResponse);

            String htmlTemplate = null;
            if (role.equals(Role.USER.name())) {
                htmlTemplate = "match-details-patient";
            } else if (role.equals(Role.CAREGIVER.name())) {
                htmlTemplate = "match-details-caregiver";
            }

            String htmlContent = templateEngine.process(htmlTemplate, context);
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(htmlContent, "");

            File fontFile = new ClassPathResource("fonts/NotoSansKR-Regular.ttf").getFile();
            builder.useFont(fontFile, "Noto Sans KR");

            builder.toStream(os);
            builder.run();
            return new ByteArrayInputStream(os.toByteArray());
        } catch (RuntimeException e) {
            log.error(e.getMessage(),e);
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
