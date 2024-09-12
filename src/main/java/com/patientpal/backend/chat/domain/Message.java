package com.patientpal.backend.chat.domain;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.patientpal.backend.chat.dto.MessageType;
import com.patientpal.backend.common.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(name = "messages")
public class Message extends BaseTimeEntity {

    @Id
    private UUID id;

    private MessageType messageType;

    private String content;

    private Long senderId;

    private Long chatId;

    private UUID generateId() {
        return Generators.timeBasedGenerator(EthernetAddress.fromInterface()).generate();
    }
}
