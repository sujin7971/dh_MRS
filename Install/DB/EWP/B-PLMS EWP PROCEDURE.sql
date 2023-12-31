-- --------------------------------------------------------
-- 호스트:                          127.0.0.1
-- 서버 버전:                        10.3.32-MariaDB - mariadb.org binary distribution
-- 서버 OS:                        Win64
-- HeidiSQL 버전:                  11.2.0.6213
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- 프로시저 ewp_plms.PROC_CERT_GROUP_MEETING_PERMISSION 구조 내보내기
DELIMITER //
CREATE PROCEDURE `PROC_CERT_GROUP_MEETING_PERMISSION`(
	IN `_GROUP_TYPE` VARCHAR(50),
	IN `_GROUP_CODE` VARCHAR(50),
	IN `_MEETING_ID` INT,
	IN `_AUTH` INT
)
BEGIN
	/* 특정 그룹의 회의에 대한 사용권한 등록 
	   _GROUP_TYPE: 권한을 휙득할 그룹분류("USER", "ROLE", "DEPT")
		_GROUP_CODE: 그룹의 코드
			-"USER" : 사용자 고유키
			-"ROLE" : "HOST", "ASSISTANT", "ATTENDEE", "OBSERVER"
			-"DEPT" : 부서 고유키 
		-MEETING_ID: 부여할 권한의 대상 회의 고유키
		-AUTH: 부여할 권한값	
	*/
	DECLARE _PERMID INTEGER;

	INSERT INTO meeting_permission(permLvl, permName, permDesc)
	VALUE(_AUTH, CONCAT(_GROUP_CODE, "-", _MEETING_ID), "AUTO_PERM");
	
	SET _PERMID = LAST_INSERT_ID();
	INSERT INTO meeting_grp_permission(grpDiv, grpCode, meetingId, permId)
	VALUE(_GROUP_TYPE, _GROUP_CODE, _MEETING_ID, _PERMID);
END//
DELIMITER ;

-- 프로시저 ewp_plms.PROC_CERT_MEETING_DEFAULT_AUTH 구조 내보내기
DELIMITER //
CREATE PROCEDURE `PROC_CERT_MEETING_DEFAULT_AUTH`(
	IN `_WRITER_ID` VARCHAR(50),
	IN `_MEETING_ID` INT
)
BEGIN
	/* 회의에 대한 기본 권한 발급 처리
	_WRITER_ID: 회의 등록자 고유키
	-MEETING_ID: 회의 고유키
	*/
	DECLARE _WRITER_AUTH INTEGER;
	DECLARE _ATTEND_AUTH INTEGER;
	SET _WRITER_AUTH = (SELECT FUNC_GET_AUTH_VAL("FUNC_READ", "COM")) 
	+ (SELECT FUNC_GET_AUTH_VAL("FUNC_VIEW", "COM"))
	+ (SELECT FUNC_GET_AUTH_VAL("FUNC_UPDATE", "COM"))
	+ (SELECT FUNC_GET_AUTH_VAL("FUNC_CANCEL", "MT"))
	+ (SELECT FUNC_GET_AUTH_VAL("FUNC_UPLOAD", "MT"))
	+ (SELECT FUNC_GET_AUTH_VAL("FUNC_INVITE", "MT"))
	;
	SET _ATTEND_AUTH = (SELECT FUNC_GET_AUTH_VAL("FUNC_NONE", "MT"))
	;
	CALL PROC_CERT_GROUP_MEETING_PERMISSION("USER", _WRITER_ID, _MEETING_ID, _WRITER_AUTH);
	CALL PROC_CERT_GROUP_MEETING_PERMISSION("ROLE", "HOST", _MEETING_ID, _ATTEND_AUTH);
	CALL PROC_CERT_GROUP_MEETING_PERMISSION("ROLE", "ASSISTANT", _MEETING_ID, _ATTEND_AUTH);
	CALL PROC_CERT_GROUP_MEETING_PERMISSION("ROLE", "ATTENDEE", _MEETING_ID, _ATTEND_AUTH);
	CALL PROC_CERT_GROUP_MEETING_PERMISSION("ROLE", "OBSERVER", _MEETING_ID, _ATTEND_AUTH);
END//
DELIMITER ;

-- 프로시저 ewp_plms.PROC_UPDATE_MEETING_INFO_STATUS 구조 내보내기
DELIMITER //
CREATE PROCEDURE `PROC_UPDATE_MEETING_INFO_STATUS`(
	IN `_MEETING_ID` INT,
	IN `_APP_STATUS` INT
)
BEGIN
	IF _APP_STATUS = 3
	THEN 
		UPDATE meeting_info SET meetingStatus = "CANCEL", stickyBit = 0 WHERE meetingKey = _MEETING_ID;
	ELSEIF _APP_STATUS = 4
	THEN
		UPDATE meeting_info SET meetingStatus = "DROP", stickyBit = 0 WHERE meetingKey = _MEETING_ID;
	ELSEIF _APP_STATUS = 2
	THEN
		UPDATE meeting_info SET meetingStatus = "APPROVED", stickyBit = 0 WHERE meetingKey = _MEETING_ID;
	ELSEIF _APP_STATUS = 1
	THEN
		UPDATE meeting_info SET meetingStatus = "UNAPPROVAL", stickyBit = 0 WHERE meetingKey = _MEETING_ID;
	END IF;
END//
DELIMITER ;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
