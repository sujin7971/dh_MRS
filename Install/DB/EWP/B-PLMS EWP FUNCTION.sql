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

-- 함수 ewp_plms.FUNC_GET_AUTH_VAL 구조 내보내기
DELIMITER //
CREATE FUNCTION `FUNC_GET_AUTH_VAL`(
	`_AUTH_CODE` VARCHAR(20),
	`_AUTH_DIV` VARCHAR(50)
) RETURNS int(11)
BEGIN
	/* 특정 권한의 값 조회하여 반환
	_AUTH_CODE: 권한 코드("FUNC_~")
	_AUTH_DIV: 권한 분류("COM", "MT", "FILE")
	
	*/
	DECLARE _AUTH_VAL INTEGER;
	
	SELECT authVal INTO _AUTH_VAL
	FROM auth_code
	WHERE authCode = _AUTH_CODE AND authDiv = _AUTH_DIV
	;
	RETURN _AUTH_VAL;
END//
DELIMITER ;

-- 함수 ewp_plms.FUNC_GET_CVT_PRIORITY 구조 내보내기
DELIMITER //
CREATE FUNCTION `FUNC_GET_CVT_PRIORITY`(
	`_MEETING_STATUS` VARCHAR(50)
) RETURNS int(11)
BEGIN
	DECLARE _PRIORITY INTEGER;
	SET _PRIORITY = 3;
	
	IF _MEETING_STATUS = "APPROVAL" THEN SET _PRIORITY = 1;
	ELSEIF _MEETING_STATUS = "OPENING" THEN SET _PRIORITY = 2;
	ELSEIF _MEETING_STATUS = "START" THEN SET _PRIORITY = 3;
	ELSE SET _PRIORITY = 0;
	END IF;
	
	RETURN _PRIORITY;
END//
DELIMITER ;

-- 함수 ewp_plms.FUNC_GET_GRP_MEETING_AUTH_VAL 구조 내보내기
DELIMITER //
CREATE FUNCTION `FUNC_GET_GRP_MEETING_AUTH_VAL`(
	`GRP_DIV` VARCHAR(50),
	`GRP_CODE` VARCHAR(50),
	`MEETING_ID` INT
) RETURNS int(11)
BEGIN
	/* 그룹 유형 GRP_DIV에 속한 GRP_CODE그룹이 회의 MEETING_ID에 대해 가진 권한 조회 */
	DECLARE AUTH_VAL INTEGER;
	SET AUTH_VAL = 0;
	
	SELECT permLvl INTO AUTH_VAL
	FROM meeting_grp_permission mgp
	JOIN meeting_permission mp
	ON mgp.permId = mp.permId
	WHERE mgp.grpDiv = GRP_DIV AND mgp.grpCode = GRP_CODE AND mgp.meetingId = MEETING_ID;
	
	RETURN AUTH_VAL;
END//
DELIMITER ;

-- 함수 ewp_plms.FUNC_IS_DEPT_HAS_MEETING_AUTH 구조 내보내기
DELIMITER //
CREATE FUNCTION `FUNC_IS_DEPT_HAS_MEETING_AUTH`(
	`DEPT_ID` VARCHAR(50),
	`MEETING_ID` INT,
	`AUTH_CODE` VARCHAR(50)
) RETURNS int(11)
BEGIN
	DECLARE IS_HAS INTEGER;
	DECLARE CHECK_AUTH_VAL INTEGER;
	DECLARE DEPT_AUTH_VAL INTEGER;
	SET IS_HAS = -1;
	SET CHECK_AUTH_VAL = (SELECT FUNC_GET_AUTH_VAL(auth_code, 'MT'));

	SET DEPT_AUTH_VAL = (SELECT FUNC_GET_GRP_MEETING_AUTH_VAL('DEPT', DEPT_ID, MEETING_ID));
	IF DEPT_AUTH_VAL IS NOT NULL THEN	
		IF DEPT_AUTH_VAL & CHECK_AUTH_VAL = CHECK_AUTH_VAL THEN SET IS_HAS = 1;
		END IF;
	END IF;
	
	RETURN IS_HAS;
END//
DELIMITER ;

-- 함수 ewp_plms.FUNC_IS_USER_HAS_MEETING_AUTH 구조 내보내기
DELIMITER //
CREATE FUNCTION `FUNC_IS_USER_HAS_MEETING_AUTH`(
	`USER_ID` VARCHAR(50),
	`MEETING_ID` INT,
	`AUTH_CODE` VARCHAR(50)
) RETURNS int(11)
BEGIN
	DECLARE IS_HAS INTEGER;
	DECLARE ATTEND_ROLE VARCHAR(50);
	DECLARE ASSISTANT_YN CHAR(1);
	DECLARE CHECK_AUTH_VAL INTEGER;/* 판별할 권한의 기능값 */
	DECLARE ATTENDEE_AUTH_VAL INTEGER;/* 참석자 권한값 */
	DECLARE USER_AUTH_VAL INTEGER;/* 개인 권한값 */
	
	SET IS_HAS = -1;
	SET CHECK_AUTH_VAL = (SELECT FUNC_GET_AUTH_VAL(auth_code, 'MT'));
	SET USER_AUTH_VAL = (SELECT FUNC_GET_GRP_MEETING_AUTH_VAL('USER', USER_ID, MEETING_ID));
	
	SELECT attendRole, assistantYN INTO ATTEND_ROLE, ASSISTANT_YN
	FROM meeting_attendee
	WHERE meetingKey = MEETING_ID AND userKey = USER_ID AND delYN = 'N';
	
	IF ATTEND_ROLE IS NOT NULL THEN/* 참석자 인 경우 참석자 권한도 판별에 사용 */
		IF ASSISTANT_YN = 'Y' THEN SET ATTEND_ROLE = 'ASSISTANT';
		END IF;
		SET ATTENDEE_AUTH_VAL = (SELECT FUNC_GET_GRP_MEETING_AUTH_VAL('ROLE', ATTEND_ROLE, MEETING_ID));	
	ELSE 
		SET ATTENDEE_AUTH_VAL = 0;
	END IF;
	
	IF (USER_AUTH_VAL | ATTENDEE_AUTH_VAL) & CHECK_AUTH_VAL = CHECK_AUTH_VAL THEN SET IS_HAS = 1;
	END IF;
	
	RETURN IS_HAS;
END//
DELIMITER ;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
