package com.patientpal.backend.matching.service;

import com.patientpal.backend.notification.domain.NotificationInfo;
import com.patientpal.backend.matching.dto.response.MatchResponse;
import com.patientpal.backend.notification.domain.NotificationType;

public class MatchNotificationProxy extends MatchResponse implements NotificationInfo {

    private final MatchResponse matchResponse;
    private final MatchNotificationMemberResponse memberResponseNotification;

    public MatchNotificationProxy(MatchResponse matchResponse, MatchNotificationMemberResponse memberResponseNotification) {
        super(matchResponse.getId(), matchResponse.getPatientId(), matchResponse.getCaregiverId(),
                matchResponse.getCreatedDate(),
                matchResponse.getMatchStatus(), matchResponse.getReadStatus(), matchResponse.getFirstRequest(),
                matchResponse.getPatientProfileSnapshot(), matchResponse.getCaregiverProfileSnapshot());
        this.memberResponseNotification = memberResponseNotification;
        this.matchResponse = matchResponse;
    }

    @Override
    public String getReceiver() {
        return memberResponseNotification.getUsername();
    }

    @Override
    public Long getGoUrlId() {
        return matchResponse.getId();
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.MATCH;
    }
}
