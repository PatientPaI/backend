package com.patientpal.backend.webhook.service;

import com.patientpal.backend.webhook.dto.DiscordMessage;
import com.patientpal.backend.webhook.dto.DiscordMessage.Embed;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@Service
@Profile("prod")
public class DiscordWebhookService {

    @Value("${discord.webhook.url}")
    private String webhookUrl;

    public void sendDiscordAlarm(Exception ex, WebRequest request) {
        RestTemplate restTemplate = new RestTemplate();
        DiscordMessage discordMessage = createMessage(ex, request);
        restTemplate.postForEntity(webhookUrl, discordMessage, String.class);
    }

    private String createDescription(Exception ex, WebRequest request) {
        Throwable rootException = getRootCauseOrDefault(ex);
        String truncatedStackTrace = getTruncatedStackTrace(rootException, 10);

        return String.format("""
        ### :link: 요청 URL
        %s
        ### :page_facing_up: 스택 트레이스
        ```bash
        %s
        ```
        """, createRequestFullPath(request), truncatedStackTrace);
    }

    private Throwable getRootCauseOrDefault(Exception ex) {
        Throwable rootCause = ExceptionUtils.getRootCause(ex);
        return (rootCause != null) ? rootCause : ex;
    }

    private String getTruncatedStackTrace(Throwable ex, int maxLines) {
        String fullStackTrace = ExceptionUtils.getStackTrace(ex);
        return Arrays.stream(fullStackTrace.split("\n"))
                .limit(maxLines)
                .collect(Collectors.joining("\n"));
    }

    private DiscordMessage createMessage(Exception ex, WebRequest request) {
        return DiscordMessage.builder()
                .content("# :fire: 에러 발생 :rotating_light:")
                .embeds(
                        List.of(
                                Embed.builder()
                                        .title(":information_source: 에러 정보")
                                        .description(createDescription(ex, request))
                                        .color(0xFF0000)
                                        .build()
                        )
                )
                .build();
    }

    private String createRequestFullPath(WebRequest webRequest) {
        HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
        String fullPath = request.getMethod() + " " + request.getRequestURI();
        String queryString = request.getQueryString();

        if (StringUtils.isNotEmpty(queryString)) {
            fullPath += "?" + queryString;
        }
        return fullPath;
    }
}
