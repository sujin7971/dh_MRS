package egov.framework.plms.main.core.model.enums.common;

public enum LoginResult {
	SUCCESS, // 성공
    BAD_PRINCIPAL, // 잘못된 아이디
    BAD_CREDENTIALS, // 잘못된 비밀번호
    EXPIRED_ACCOUNT, // 만료된 계정
    ACCOUNT_LOCKED, // 잠긴 계정
    ACCESS_DENIED, // 접근 거부 (동시 접속자 인원 제한 등)
    DEACTIVATED, //비활성화된 계정
    UNAUTHORIZED_SERVER, // 권한이 없는 서버 접속 시도
    UNKNOWN_ERROR // 알 수 없는 에러
}
