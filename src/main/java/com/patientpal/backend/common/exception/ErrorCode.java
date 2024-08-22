package com.patientpal.backend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S_001", "서버에 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "S_002", "잘못된 요청 값입니다."),
    TOKEN_HASHING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S_003", "서버에 오류가 발생했습니다."),

    AUTHORIZATION_FAILED(HttpStatus.FORBIDDEN, "AR_001", "권한이 없습니다."),

    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "AU_001", "아이디 또는 비밀번호가 일치하지 않습니다."),
    UNSUPPORTED_OAUTH2_PROVIDER(HttpStatus.BAD_REQUEST, "AU_002", "지원하지 않는 OAuth2 프로바이더입니다."),
    UNSELECTED_ROLE(HttpStatus.BAD_REQUEST, "AU_003", "역할이 선택되지 않았습니다."),
    INVALID_USERNAME(HttpStatus.BAD_REQUEST, "AU_004", "유효하지 않은 사용자 이름입니다."),

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "V_001", "유효하지 않은 토큰입니다."),
    INVALID_RESIDENT_REGISTRATION_NUMBER(HttpStatus.NON_AUTHORITATIVE_INFORMATION, "V_002", "유효하지 않은 주민등록번호입니다."),

    MEMBER_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "M_001", "이미 가입된 계정이 존재합니다."),
    MEMBER_NOT_EXIST(HttpStatus.BAD_REQUEST, "M_002", "해당 계정은 존재하지 않습니다."),
    NOT_COMPLETE_PROFILE(HttpStatus.NOT_FOUND, "M_003", "프로필이 등록되어 있지 않습니다."),
    CAN_NOT_DELETE_PROFILE(HttpStatus.BAD_REQUEST, "M_004", "진행 중인 매칭이 있어 프로필을 삭제할 수 없습니다."),
    NOT_ACCEPTED_MATCH(HttpStatus.UNAUTHORIZED, "M_005", "PDF 다운로드는 매칭 수락 후 진행 가능합니다."),

    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "P_001", "해당 게시글을 찾을 수 없습니다."),

    DUPLICATED_REQUEST(HttpStatus.CONFLICT, "MT_001", "이미 매칭 요청이 진행 중입니다."),
    MATCH_ALREADY_ACCEPTED(HttpStatus.CONFLICT, "MT_002", "이미 매칭 수락이 완료되었습니다."),
    MATCH_ALREADY_CANCELED(HttpStatus.CONFLICT, "MT_003", "이미 매칭 취소가 처리되었습니다."),
    MATCH_NOT_EXIST(HttpStatus.NOT_FOUND, "MT_004", "매칭을 찾을 수 없습니다."),
    CAN_NOT_REQUEST_TO(HttpStatus.NOT_FOUND, "MT_005", "매칭 요청을 보낼 수 없는 상대입니다."),
    CAN_NOT_READ(HttpStatus.NOT_FOUND, "MT_006", "취소된 매칭 요청은 조회가 불가능합니다."),
    CAN_NOT_CANCEL_ALREADY_ACCEPTED_MATCH(HttpStatus.CONFLICT, "MT_007", "이미 진행중인 매칭은 취소가 불가능합니다."),
    CAN_NOT_ACCEPT_ALREADY_DELETE_PROFILE(HttpStatus.CONFLICT, "MT_008", "상대 프로필이 사라져 매칭 수락을 할 수 없습니다."),
    CAN_NOT_CREATE_PDF(HttpStatus.CONFLICT, "MT_009", "PDF 생성 실패"),

    PATIENT_NOT_EXIST(HttpStatus.NOT_FOUND, "PA_001", "해당 환자는 존재하지 않습니다."),
    PATIENT_ALREADY_EXIST(HttpStatus.CONFLICT, "PA_002", "이미 가입된 환자 프로필이 존재합니다."),
    CAREGIVER_NOT_EXIST(HttpStatus.NOT_FOUND, "CA_001", "해당 간병인은 존재하지 않습니다."),
    CAREGIVER_ALREADY_EXIST(HttpStatus.CONFLICT, "CA_002", "이미 가입된 간병인 프로필이 존재합니다."),

    PROFILE_NOT_COMPLETED(HttpStatus.NOT_FOUND, "PR_001", "프로필 작성이 선행되어야 합니다."),
    PROFILE_PRIVATE(HttpStatus.UNAUTHORIZED, "PR_002", "프로필이 비공개입니다."),

    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "RV_001", "리뷰를 찾을 수 없습니다."),

    CHAT_NOT_FOUND(HttpStatus.NOT_FOUND, "CH_001", "채팅이 존재하지 않습니다."),

    USER_DATA_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "U_001", "사용자 데이터를 가져오는 데 실패했습니다.");



    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(final HttpStatus status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
