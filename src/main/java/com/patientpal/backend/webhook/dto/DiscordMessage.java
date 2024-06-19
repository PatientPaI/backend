package com.patientpal.backend.webhook.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DiscordMessage {
    private String content;
    private List<Embed> embeds;

    @Builder
    public DiscordMessage(String content, List<Embed> embeds) {
        this.content = content;
        this.embeds = embeds;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Embed {
        private String title;
        private String description;
        private int color;

        @Builder
        public Embed(String title, String description, int color) {
            this.title = title;
            this.description = description;
            this.color = color;
        }
    }
}
