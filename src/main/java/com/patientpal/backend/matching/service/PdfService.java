package com.patientpal.backend.matching.service;

import com.lowagie.text.DocumentException;
import com.patientpal.backend.matching.dto.response.MatchResponse;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

    private final TemplateEngine templateEngine;

    public PdfService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public ByteArrayInputStream generateMatchPdf(MatchResponse matchResponse) throws DocumentException {
        Context context = new Context();
        context.setVariable("match", matchResponse);

        String htmlContent = templateEngine.process("match-details", context);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(out);

        return new ByteArrayInputStream(out.toByteArray());
    }
}
