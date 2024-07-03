package com.patientpal.backend.chat.domain;

import com.patientpal.backend.common.BaseTimeEntity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Message extends BaseTimeEntity {

    //컬럼추가
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;


    public Message(Long id, String content ) {
        this.id  = id;
        this.content = content;
    }

}
