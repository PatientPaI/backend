package com.patientpal.backend.chat.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patientpal.backend.common.BaseTimeEntity;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.io.IOException;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
public class Chat extends BaseTimeEntity {

    private static final int DIRECT_CHAT_MEMBER_SIZE = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ChatType chatType;

    @Convert(converter = MemberIdsConverter.class)
    private List<Long> memberIds;

    public boolean alone() {
        return memberIds.size() != DIRECT_CHAT_MEMBER_SIZE;
    }

    public void leave(Long memberId) {
        memberIds.remove(memberId);
    }

    @Converter
    static
    class MemberIdsConverter implements AttributeConverter<List<Long>, String> {
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(List<Long> attribute) {
            try {
                return objectMapper.writeValueAsString(attribute);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Error converting list to JSON", e);
            }
        }

        @Override
        public List<Long> convertToEntityAttribute(String s) {
            try {
                return objectMapper.readValue(s, new TypeReference<>() {});
            } catch (IOException e) {
                throw new IllegalArgumentException("Error converting JSON to list", e);
            }
        }
    }
}
