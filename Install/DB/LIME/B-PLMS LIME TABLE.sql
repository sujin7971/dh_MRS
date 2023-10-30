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

-- 테이블 b_plms.admin_domain_info 구조 내보내기
CREATE TABLE IF NOT EXISTS `admin_domain_info` (
  `userId` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '사번',
  `domainRole` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '관리자 구분',
  `regDateTime` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`userId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='프로그램 계층 관리자 명단';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 b_plms.admin_manager_info 구조 내보내기
CREATE TABLE IF NOT EXISTS `admin_manager_info` (
  `userId` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `officeCode` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `deptId` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `managerRole` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`userId`,`managerRole`,`officeCode`,`deptId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='기능 관리자 명단';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 b_plms.auth_code 구조 내보내기
CREATE TABLE IF NOT EXISTS `auth_code` (
  `authId` int(11) NOT NULL AUTO_INCREMENT,
  `authCode` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '기능코드',
  `authDiv` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'COM' COMMENT '코드분류\r\n-COM: 공통\r\n-MT: 회의\r\n-FILE: 파일',
  `authName` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '기능명',
  `authDesc` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '기능상세',
  `authVal` int(11) NOT NULL COMMENT '기능값(2진수)',
  PRIMARY KEY (`authId`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 b_plms.board_notice 구조 내보내기
CREATE TABLE IF NOT EXISTS `board_notice` (
  `noticeId` int(11) NOT NULL AUTO_INCREMENT COMMENT '공지사항 번호',
  `writerId` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '작성자 키값',
  `officeCode` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '사업소 키값',
  `fixYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '상단고정여부 Y/N',
  `title` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '공지 제목',
  `contents` mediumtext COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '공지 내용',
  `regDateTime` datetime NOT NULL DEFAULT current_timestamp() COMMENT '등록일',
  `modDateTime` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '수정일',
  `delYN` enum('Y','N') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '삭제여부',
  PRIMARY KEY (`noticeId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 b_plms.common_code 구조 내보내기
CREATE TABLE IF NOT EXISTS `common_code` (
  `CLASS_CD` varchar(6) NOT NULL COMMENT '분류코드',
  `CLASS_NM` varchar(100) DEFAULT NULL COMMENT '분류코드명',
  `DTL_CD` varchar(20) NOT NULL COMMENT '상세코드',
  `DTL_NM` varchar(500) DEFAULT NULL COMMENT '상세코드명',
  `DISP_COL_NM` varchar(100) DEFAULT NULL COMMENT '코드디스플레이명',
  `CLASS_TYPE` varchar(20) DEFAULT NULL COMMENT '분류구분 ',
  `SORT_SEQ` int(3) DEFAULT NULL COMMENT '정렬순서 ',
  `RMK` varchar(300) DEFAULT NULL COMMENT '비고',
  `USE_YN` varchar(1) DEFAULT 'Y' COMMENT '사용여부 ',
  `ETC_COL1` varchar(100) DEFAULT NULL COMMENT '기타컬럼1',
  `ETC_COL2` varchar(100) DEFAULT NULL COMMENT '기타컬럼2',
  `ETC_COL3` varchar(100) DEFAULT NULL COMMENT '기타컬럼3',
  `ETC_COL4` varchar(100) DEFAULT NULL COMMENT '기타컬럼4',
  `UPD_CODE` varchar(1) NOT NULL DEFAULT 'C' COMMENT 'CUD구분(I: 신규생성, D: 삭제, U: 수정)'';',
  `INS_DATE` date NOT NULL DEFAULT current_timestamp() COMMENT '등록일자',
  `INS_EMP_ID` varchar(15) NOT NULL DEFAULT 'SYSTEM' COMMENT '등록사원',
  `LAST_UPD_DATE` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '수정일자',
  `LAST_UPD_EMP_ID` varchar(15) NOT NULL DEFAULT 'SYSTEM' COMMENT '수정사원',
  PRIMARY KEY (`CLASS_CD`,`DTL_CD`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 b_plms.drive_file_cvt 구조 내보내기
CREATE TABLE IF NOT EXISTS `drive_file_cvt` (
  `cvtId` int(11) NOT NULL AUTO_INCREMENT COMMENT '변환키',
  `fileId` int(11) NOT NULL COMMENT '파일키',
  `cvtPriority` int(11) NOT NULL DEFAULT 0 COMMENT '변환 시도 우선순위(0,1,2,3)',
  `cvtCount` int(11) NOT NULL DEFAULT 0 COMMENT '변환 시도 횟수',
  `cvtStep` int(11) NOT NULL DEFAULT 0 COMMENT '변환 시도 단계(-1:실패,0:대기,1:변환중,2:성공)',
  `conversionType` enum('DOC_TO_PDF','PDF_TO_IMAGES','IMAGES_TO_PDF','IMAGE_TO_WEBP') COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '변환 유형',
  `sourcePath` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `destinationPath` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `startDateTime` datetime DEFAULT NULL COMMENT '변환 시작(YYYYMMDDHHmiss)',
  `endDateTime` datetime DEFAULT NULL COMMENT '변환 종료(YYYYMMDDHHmiss)',
  `errMsg` varchar(5000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '에러 메시지',
  `regDateTime` datetime DEFAULT current_timestamp(),
  `modDateTime` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`cvtId`) USING BTREE,
  KEY `IDX1_file_cvt` (`fileId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 b_plms.drive_file_info 구조 내보내기
CREATE TABLE IF NOT EXISTS `drive_file_info` (
  `fileId` int(11) NOT NULL AUTO_INCREMENT COMMENT '파일키',
  `sourceId` int(11) DEFAULT NULL COMMENT '원본파일키',
  `uploaderId` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '업로더키',
  `fileCategory` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '*파일분류\r\n-HWP: 한글\r\n-WORD: 워드\r\n-PPT: 파워포인트\r\n-EXCEL: 엑셀\r\n-PDF: PDF\r\n-IMG: 이미지',
  `uploadedFileName` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '파일 업로드시 이름(확장자포함)',
  `fileLabel` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '파일명',
  `fileExt` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '파일확장자',
  `uuid` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '서버에 저장될 파일명',
  `fileSize` int(11) DEFAULT 0 COMMENT '파일 크기(byte)',
  `pageCount` int(11) DEFAULT NULL COMMENT '파일 페이지수(인쇄시)',
  `fileStatus` int(11) NOT NULL DEFAULT 100 COMMENT '파일 상태\r\n-100: 디스크에 저장됨\r\n-200: 디스크에서 누락됨\r\n-210: 디스크에 파일이 아직 할당되지 않음\r\n-300: 암호가 설정 되어 있음\r\n-310: DRM으로 암호화 되어 있음\r\n-400: 내부 로직으로 인해 삭제됨\r\n-410: 기간 만료로 삭제됨',
  `conversionStatus` int(11) NOT NULL DEFAULT 0 COMMENT '변환상태\r\n-0: 요청없음\r\n-100: 변환전\r\n-200: 문서를 PDF파일로 변환\r\n-210: PDF파일 페이지별 이미지 생성\r\n-220: 이미지 파일 WEBP로 변환\r\n-230: 페이지별 이미지 PDF파일로 변환\r\n-300: 변환 완료\r\n-400: 변환 실패',
  `pdfGeneratedYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT 'PDF 생성 여부',
  `pageImagesGeneratedYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '페이지별 이미지 생성 여부',
  `webpGeneratedYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT 'WebP 생성 여부',
  `uploadDateTime` datetime NOT NULL DEFAULT current_timestamp() COMMENT '업로드된 시간',
  `modDateTime` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '수정시간',
  `delYN` enum('Y','N') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '삭제 여부',
  PRIMARY KEY (`fileId`) USING BTREE,
  KEY `IDX1_file_info` (`sourceId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=547 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='*원본 파일 저장경로(년/월은 파일 생성일자를 따름)\r\n-User-uploaded files: ROOT_PATH\\uploadFile\\user\\userId\\year\\month\\uuid\\uuid.ext\r\n-View files: ROOT_PATH\\uploadFile\\user\\userId\\year\\month\\uuid\\uuid.pdf\r\n*판서 관련 파일 저장경로(년/월은 파일 생성일자를 따름)\r\n-Page-specific image files for writing: ROOT_PATH\\uploadFile\\meeting\\meetingKey\\SOURCE\\fileKey\\0001.webp~pages.web\r\n-User-specific annotation files: ROOT_PATH\\uploadFile\\meeting\\meetingKey\\EDITION\\fileKey\\0001.webp~pages.web\r\n-Converted PDF files after the meeting: ROOT_PATH\\uploadFile\\user\\userId\\year\\month\\fileKey (same as user-uploaded files)';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 b_plms.drive_file_relation 구조 내보내기
CREATE TABLE IF NOT EXISTS `drive_file_relation` (
  `relationId` int(11) NOT NULL AUTO_INCREMENT COMMENT '관계키',
  `registerId` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '등록자키',
  `fileId` int(11) NOT NULL COMMENT '파일키',
  `relatedEntityId` int(11) NOT NULL DEFAULT 0 COMMENT '연관된 개체키',
  `relatedEntityType` int(11) NOT NULL COMMENT '연관된 개체 유형\r\n-10: 회의\r\n-20: 사용자게시판\r\n-21: 공지사항게시판',
  `relationType` int(11) NOT NULL COMMENT '관계유형\r\n-100: 회의 원본자료\r\n-101: 회의 메모\r\n-102: 회의 판서본\r\n-103: 회의 회의록\r\n-104: 회의 사진기록\r\n-105: 회의 녹음기록\r\n-200: 게시판 첨부파일',
  `regDateTime` datetime NOT NULL DEFAULT current_timestamp() COMMENT '등록일',
  PRIMARY KEY (`relationId`) USING BTREE,
  UNIQUE KEY `fileId` (`fileId`),
  KEY `IDX1_file_relation` (`fileId`) USING BTREE,
  KEY `IDX2_file_relation` (`relatedEntityId`,`relatedEntityType`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 b_plms.file_conversion_transactions 구조 내보내기
CREATE TABLE IF NOT EXISTS `file_conversion_transactions` (
  `trId` int(11) NOT NULL COMMENT '변환키',
  `fileCategory` enum('HWP','PPT','EXCEL','WORD','PDF','IMG') COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '*파일분류\r\n-HWP: 한글\r\n-WORD: 워드\r\n-PPT: 파워포인트\r\n-EXCEL: 엑셀\r\n-PDF: PDF\r\n-IMG: 이미지',
  `conversionType` enum('DOC_TO_PDF','PDF_TO_IMAGES','IMAGES_TO_PDF','IMAGE_TO_WEBP') COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '변환 유형',
  `cvtCount` int(11) NOT NULL DEFAULT 0 COMMENT '변환 시도 횟수',
  `cvtStep` int(11) NOT NULL DEFAULT 0 COMMENT '변환 시도 단계(-1:실패,0:대기,1:변환중,2:성공)',
  `sourcePath` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `destinationPath` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `startDateTime` datetime DEFAULT NULL COMMENT '변환 시작(YYYYMMDDHHmiss)',
  `endDateTime` datetime DEFAULT NULL COMMENT '변환 종료(YYYYMMDDHHmiss)',
  `errMsg` varchar(5000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '에러 메시지',
  `regDateTime` datetime DEFAULT current_timestamp(),
  `modDateTime` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`trId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='*클라이언트서버->변환서버\r\n-파일 변환요청 등록(Post): /file-conversion\r\n-파일 상태 조회(Get): /file-conversion/{trId}\r\n\r\n*변환서버->클라이언트 서버\r\n-파일 변환 결과 업데이트(Put): /file-conversion/{trId}/cvtStep/{cvtStep}';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 함수 b_plms.FUNC_GET_AUTH_VAL 구조 내보내기
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

-- 함수 b_plms.FUNC_GET_CVT_PRIORITY 구조 내보내기
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

-- 함수 b_plms.FUNC_GET_GRP_MEETING_AUTH_VAL 구조 내보내기
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

-- 함수 b_plms.FUNC_IS_DEPT_HAS_MEETING_AUTH 구조 내보내기
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

-- 함수 b_plms.FUNC_IS_USER_HAS_MEETING_AUTH 구조 내보내기
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
	WHERE meetingId = MEETING_ID AND userId = USER_ID AND delYN = 'N';
	
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

-- 테이블 b_plms.login_history 구조 내보내기
CREATE TABLE IF NOT EXISTS `login_history` (
  `loginId` int(11) NOT NULL AUTO_INCREMENT,
  `userId` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `ipAddress` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `serverType` enum('USER','ADMIN','EMBEDED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `userAgent` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `loginDateTime` datetime NOT NULL DEFAULT current_timestamp(),
  `loginResult` enum('SUCCESS','BAD_PRINCIPAL','BAD_CREDENTIALS','EXPIRED_ACCOUNT','ACCOUNT_LOCKED','ACCESS_DENIED','DEACTIVATED','UNAUTHORIZED_SERVER','UNKNOWN_ERROR') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`loginId`)
) ENGINE=InnoDB AUTO_INCREMENT=223 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='로그인 이력 테이블';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 b_plms.meeting_approval 구조 내보내기
CREATE TABLE IF NOT EXISTS `meeting_approval` (
  `approvalId` int(11) NOT NULL AUTO_INCREMENT COMMENT '결재 요청키',
  `requesterId` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '결재 요청자키',
  `decisionMakerId` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '결재 담당자키(자동처리인 경우에도 사업소 결재담당자 키 지정)',
  `scheduleId` int(11) NOT NULL COMMENT '결재 대상 스케줄키',
  `approvalStatus` int(11) NOT NULL COMMENT '결재',
  `approvalComment` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '결재 코멘트',
  `approvedYN` char(1) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '결재 성공 여부',
  `requestDateTime` datetime NOT NULL DEFAULT current_timestamp() COMMENT '등록일시',
  `processDateTime` datetime DEFAULT NULL COMMENT '결재 처리 일시',
  PRIMARY KEY (`approvalId`) USING BTREE,
  KEY `IDX2_assign_approval` (`scheduleId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=91 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 b_plms.meeting_attendee 구조 내보내기
CREATE TABLE IF NOT EXISTS `meeting_attendee` (
  `attendId` int(11) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `meetingId` int(11) NOT NULL COMMENT '회의키',
  `userId` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '참석자키',
  `userName` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '참석자명',
  `deptId` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '부서키',
  `attendRole` enum('FACILITATOR','ATTENDEE','OBSERVER','GUEST') COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '*참석유형\r\n-FACILITATOR: 진행자\r\n-ATTENDEE: 참석자\r\n-OBSERVER: 참관자\r\n-GUEST: 외부참석자',
  `assistantYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '보조 진행자 지정여부',
  `attendYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '참석여부(회의에 들어옴)',
  `attendDateTime` datetime DEFAULT NULL COMMENT '참석시간',
  `exitYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '종료여부',
  `exitDateTime` datetime DEFAULT NULL COMMENT '종료시간',
  `signYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '사인여부(회의에 들어와서 사인)',
  `signSrc` mediumtext COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '사인내용',
  `tempPassword` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '회의임시비밀번호',
  `passwordExpiryDate` date DEFAULT NULL COMMENT '비밀번호만료기간',
  `delYN` enum('Y','N') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '삭제여부',
  PRIMARY KEY (`attendId`) USING BTREE,
  UNIQUE KEY `IDX1_meeting_attendee` (`meetingId`,`userId`,`deptId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=342 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 b_plms.meeting_grp_permission 구조 내보내기
CREATE TABLE IF NOT EXISTS `meeting_grp_permission` (
  `grpId` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT concat(`grpDiv`,'_',`grpCode`,'_',`meetingId`) COMMENT '그룹의 아이디. grpDiv_grpRole_grpCode',
  `grpDiv` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '그룹 구분.\r\n-DOMAIN: 계정 계층\r\n-ROLE: 회의 역할\r\n-DEPT: 부서\r\n-USER: 유저',
  `grpCode` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '구분에 따른 그룹 코드\r\n-DOMAIN: 일반, 관리자, 최고 관리자 등\r\n-ROLE: 주관자, 보조진행자, 작성자 등\r\n-DEPT: 부서코드\r\n-USER: 사용자 식별키',
  `meetingId` int(11) NOT NULL DEFAULT 0 COMMENT '대상 회의 아이디',
  `permId` int(11) NOT NULL DEFAULT 0 COMMENT '그룹이 가질 권한',
  PRIMARY KEY (`grpId`),
  KEY `IDX1_meeting_grp_permission` (`permId`,`meetingId`) USING BTREE,
  KEY `IDX2_meeting_grp_permission` (`grpCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 b_plms.meeting_info 구조 내보내기
CREATE TABLE IF NOT EXISTS `meeting_info` (
  `meetingId` int(11) NOT NULL AUTO_INCREMENT COMMENT '회의 고유키',
  `writerId` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '등록 사원 고유키',
  `title` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '회의제목',
  `contents` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '회의내용',
  `meetingStatus` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'NEW' COMMENT '회의상태\r\n-NEW: 새로 등록됨\r\n-UNAPPROVAL: 미승인\r\n-APPROVED: 승인(예약)\r\n-OPENING: 개시\r\n-START: 회의시작\r\n-FINISH: 회의종료\r\n-CLOSING: 마감\r\n-END: 끝남\r\n-CANCEL: 취소\r\n-DROP: 드랍',
  `secretYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '기밀회의 여부',
  `hostSecuLvl` int(11) NOT NULL DEFAULT 2 COMMENT '주최자/보조진행자 공개 범위',
  `attendeeSecuLvl` int(11) NOT NULL DEFAULT 2 COMMENT '참석자 공개 범위',
  `observerSecuLvl` int(11) NOT NULL DEFAULT 2 COMMENT '참관자 공개 범위',
  `elecYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '전자회의여부',
  `mailYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '메일알림여부',
  `smsYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Y' COMMENT 'SMS알림여부',
  `messengerYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Y' COMMENT '메신저알림여부',
  `stickyBit` int(11) NOT NULL DEFAULT 0 COMMENT '읽기,조회,수정,삭제,취소,업로드,초대',
  `regDateTime` datetime NOT NULL DEFAULT current_timestamp() COMMENT '등록날짜',
  `modDateTime` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '수정날짜',
  `delYN` enum('Y','N') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '삭제여부',
  PRIMARY KEY (`meetingId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=604 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 b_plms.meeting_permission 구조 내보내기
CREATE TABLE IF NOT EXISTS `meeting_permission` (
  `permId` int(11) NOT NULL AUTO_INCREMENT COMMENT '권한 아이디',
  `permLvl` int(11) NOT NULL DEFAULT 0 COMMENT '권한 레벨',
  `permName` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '권한 이름',
  `permDesc` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '권한 상세',
  `regDateTime` datetime NOT NULL DEFAULT current_timestamp() COMMENT '등록시간',
  `modDateTime` datetime NOT NULL DEFAULT current_timestamp() COMMENT '수정시간',
  `expDateTime` datetime DEFAULT NULL COMMENT '만료시간',
  `delYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '삭제 여부',
  PRIMARY KEY (`permId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 b_plms.meeting_report 구조 내보내기
CREATE TABLE IF NOT EXISTS `meeting_report` (
  `meetingId` int(11) NOT NULL COMMENT '회의키',
  `reporterId` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '작성자키',
  `reportContents` mediumtext COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '회의록내용',
  `reportStatus` int(11) NOT NULL DEFAULT 0 COMMENT '*상태값\r\n0: 임시저장\r\n1: 의견요청\r\n2: 최종등록',
  `regDateTime` datetime NOT NULL DEFAULT current_timestamp() COMMENT '등록시간',
  `modDateTime` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '수정시간',
  PRIMARY KEY (`meetingId`) USING BTREE,
  KEY `IDX1_meeting_report` (`reporterId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 b_plms.meeting_schedule 구조 내보내기
CREATE TABLE IF NOT EXISTS `meeting_schedule` (
  `scheduleId` int(11) NOT NULL AUTO_INCREMENT COMMENT '스케줄키',
  `meetingId` int(11) DEFAULT NULL COMMENT '회의키',
  `externalReservationId` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '회의실 사용요청키',
  `roomId` int(11) DEFAULT NULL COMMENT '회의실키',
  `roomType` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '*회의실 타입(강의실/회의실/강당 등)\r\n-강당(HALL)\r\n-강의실(EDU_ROOM)\r\n-회의실(MEETING_ROOM)',
  `writerId` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '작성자키',
  `officeCode` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '사업소/회사키',
  `deptId` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '부서키',
  `userDefinedLocation` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '사용자 정의 장소',
  `attendeeCnt` int(11) NOT NULL DEFAULT 0 COMMENT '참석자수',
  `scheduleHost` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '스케줄/배정 신청 주최자',
  `scheduleType` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'FORMAL' COMMENT '*회의 스케줄 유형\r\n-RENTAL: 장소대여\r\n-FORMAL: 정규 회의(장소,시작/종료 시간을 예약)\r\n-INSTANT: 즉시 회의',
  `approvalStatus` int(11) NOT NULL DEFAULT 0 COMMENT '승인 상태\r\n0: 미신청\r\n1: 신청\r\n2: 자동 승인\r\n3: 승인 취소\r\n4: 승인 불가\r\n',
  `approvalComment` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '상태 코멘트',
  `holdingDate` date DEFAULT NULL COMMENT '회의개최일',
  `beginDateTime` datetime NOT NULL COMMENT '시작시간',
  `finishDateTime` datetime DEFAULT NULL COMMENT '종료시간',
  `expDateTime` datetime DEFAULT NULL COMMENT '만료시간',
  `regDateTime` datetime NOT NULL DEFAULT current_timestamp() COMMENT '등록시간',
  `modDateTime` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '수정시간',
  `delYN` enum('Y','N') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '삭제여부',
  PRIMARY KEY (`scheduleId`) USING BTREE,
  UNIQUE KEY `IDX2_meeting_schedule` (`externalReservationId`,`roomType`) USING BTREE,
  KEY `IDX3_meeting_schedule` (`holdingDate`) USING BTREE,
  KEY `IDX1_meeting_schedule` (`meetingId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=602 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 b_plms.org_dept_info 구조 내보내기
CREATE TABLE IF NOT EXISTS `org_dept_info` (
  `deptId` int(11) NOT NULL AUTO_INCREMENT,
  `officeCode` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '사업소 코드',
  `deptCode` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '부서 번호',
  `parentId` int(11) NOT NULL DEFAULT 1,
  `parentCode` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '상위부서(deptno)',
  `deptName` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '부서명',
  `deptManager` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '부서장(emp)',
  `delYN` enum('Y','N') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '삭제 여부',
  PRIMARY KEY (`deptId`),
  UNIQUE KEY `deptCode` (`deptCode`),
  KEY `상위부서` (`parentCode`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 b_plms.org_user_account 구조 내보내기
CREATE TABLE IF NOT EXISTS `org_user_account` (
  `userId` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '유저 ID. 로그인에 사용',
  `userPw` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '유저 비밀번호. 로그인에 사용',
  `salt` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '비밀번호 해싱에 사용되는 난수',
  `status` enum('NORMAL','LOCKED','TEMPORARILY_LOCKED','DEACTIVATED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'NORMAL' COMMENT '계정 상태\r\n1.NORMAL (정상적으로 로그인 가능한 계정)\r\n2.LOCKED (영구적으로 잠긴 계정)\r\n3.TEMPORARILY_LOCKED (일시적으로 잠긴 계정, 로그인 실패 횟수 초과 등)\r\n4.DEACTIVATED (비활성화된 계정)',
  `failedAttempts` int(11) NOT NULL DEFAULT 0 COMMENT '로그인 실패 횟수',
  `regDateTime` datetime NOT NULL DEFAULT current_timestamp() COMMENT '계정 생성 일시',
  `modDateTime` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '계정 정보 수정 일시',
  `lockoutDateTime` datetime DEFAULT NULL COMMENT '계정 일시 잠금 일시',
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 계정 정보를 저장하는 테이블';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 b_plms.org_user_info 구조 내보내기
CREATE TABLE IF NOT EXISTS `org_user_info` (
  `userId` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '유저 ID. 로그인에 사용',
  `userName` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `deptId` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '1',
  `titleName` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `positionName` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rankName` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `personalCellPhone` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `officeDeskPhone` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `regDateTime` datetime NOT NULL DEFAULT current_timestamp(),
  `modDateTime` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `delYN` enum('Y','N') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N',
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 프로시저 b_plms.PROC_CERT_GROUP_MEETING_PERMISSION 구조 내보내기
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

-- 프로시저 b_plms.PROC_CERT_MEETING_DEFAULT_AUTH 구조 내보내기
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
	CALL PROC_CERT_GROUP_MEETING_PERMISSION("ROLE", "FACILITATOR", _MEETING_ID, _ATTEND_AUTH);
	CALL PROC_CERT_GROUP_MEETING_PERMISSION("ROLE", "ASSISTANT", _MEETING_ID, _ATTEND_AUTH);
	CALL PROC_CERT_GROUP_MEETING_PERMISSION("ROLE", "ATTENDEE", _MEETING_ID, _ATTEND_AUTH);
	CALL PROC_CERT_GROUP_MEETING_PERMISSION("ROLE", "OBSERVER", _MEETING_ID, _ATTEND_AUTH);
END//
DELIMITER ;

-- 프로시저 b_plms.PROC_UPDATE_MEETING_INFO_STATUS 구조 내보내기
DELIMITER //
CREATE PROCEDURE `PROC_UPDATE_MEETING_INFO_STATUS`(
	IN `_MEETING_ID` INT,
	IN `_APP_STATUS` INT
)
BEGIN
	IF _APP_STATUS = 3
	THEN 
		UPDATE meeting_info SET meetingStatus = "CANCEL", stickyBit = 0 WHERE meetingId = _MEETING_ID;
	ELSEIF _APP_STATUS = 4
	THEN
		UPDATE meeting_info SET meetingStatus = "DROP", stickyBit = 0 WHERE meetingId = _MEETING_ID;
	ELSEIF _APP_STATUS = 2
	THEN
		UPDATE meeting_info SET meetingStatus = "APPROVED", stickyBit = 0 WHERE meetingId = _MEETING_ID;
	ELSEIF _APP_STATUS = 1
	THEN
		UPDATE meeting_info SET meetingStatus = "UNAPPROVAL", stickyBit = 0 WHERE meetingId = _MEETING_ID;
	END IF;
END//
DELIMITER ;

-- 테이블 b_plms.room_info 구조 내보내기
CREATE TABLE IF NOT EXISTS `room_info` (
  `roomId` int(11) NOT NULL AUTO_INCREMENT COMMENT '장소고유키',
  `roomType` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '장소분류(회의실,강의실,강당)',
  `officeCode` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '사업소코드',
  `roomCode` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '장소코드',
  `roomName` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '장소명',
  `roomLabel` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '장소표기명',
  `roomSize` int(11) DEFAULT NULL COMMENT '장소좌석규모',
  `roomFloor` int(11) DEFAULT NULL COMMENT '장소층수',
  `roomNote` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '장소기타정보',
  `disableYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Y' COMMENT '대여가능여부',
  `disableComment` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '대여불가사유',
  `writerId` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '등록자고유키',
  `regDateTime` datetime NOT NULL DEFAULT current_timestamp() COMMENT '등록일자',
  `modDateTime` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '수정일자',
  `delYN` enum('Y','N') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '삭제여부',
  PRIMARY KEY (`roomId`) USING BTREE,
  UNIQUE KEY `roomCode` (`roomCode`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='동서발전 DB에 등록된 장소를 포함.\r\n경영지원서비스와 동기화하지 않는 장소에 사용신청을 한 경우 사용신청도 동서발전 DB와 동기화하지 않음.\r\n경영지원서비스에 등록된 장소키는 그대로 사용하고 자체적으로 등록한 장소의 키는 앞에 구분자 E를 붙임';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 트리거 b_plms.TR_ASSIGN_APPROVAL_APPYN_AF_UPDATE 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `TR_ASSIGN_APPROVAL_APPYN_AF_UPDATE` BEFORE INSERT ON `meeting_approval` FOR EACH ROW BEGIN
	/* 결재 요청 처리가 완료된 경우 해당 사용신청에 대한 스케줄이 DB 에 등록되 있으면 해당 칼럼의 결재 상태를 처리 내용과 동기화 */
	/* 결재 요청이 삭제 요청(-1) 인 경우 해당 사용신청에 대해 삭제 처리 */
	DECLARE _MEETING_ID INTEGER;
	DECLARE _APP_STATUS INTEGER;
	/*결재 처리에 성공한 경우*/
	IF NEW.approvedYN = 'Y'
	 	THEN 
	 		IF NEW.approvalStatus = -1 
				THEN UPDATE meeting_schedule SET delYN = 'Y', approvalStatus = NEW.approvalStatus, approvalComment = NEW.approvalComment WHERE scheduleId = NEW.scheduleId;
			ELSE 
				SELECT meetingId, approvalStatus INTO _MEETING_ID, _APP_STATUS
				FROM meeting_schedule
				WHERE scheduleId = NEW.scheduleId;
				
				IF _APP_STATUS = NEW.approvalStatus
					THEN CALL PROC_UPDATE_MEETING_INFO_STATUS(_MEETING_ID, _APP_STATUS);
				ELSE
					UPDATE meeting_schedule 
		 			SET approvalStatus = NEW.approvalStatus, approvalComment = NEW.approvalComment
		 			WHERE scheduleId = NEW.scheduleId;
		 		END IF;
		 	END IF;
	/*결재 처리에 실패한 경우 코멘트만 업데이트*/	 	
	ELSE 
		UPDATE meeting_schedule 
		 		SET approvalComment = NEW.approvalComment
		 		WHERE scheduleId = NEW.scheduleId;
 	END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 트리거 b_plms.TR_DRIVE_FILE_INFO_AF_DELETE 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `TR_DRIVE_FILE_INFO_AF_DELETE` AFTER DELETE ON `drive_file_info` FOR EACH ROW BEGIN
	DELETE FROM drive_file_cvt WHERE fileId = OLD.fileId;
	DELETE FROM drive_file_relation WHERE fileId = OLD.fileId;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 트리거 b_plms.TR_DRIVE_FILE_INFO_AF_UPDATE 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `TR_DRIVE_FILE_INFO_AF_UPDATE` AFTER UPDATE ON `drive_file_info` FOR EACH ROW BEGIN
	IF NEW.delYN = 'Y'
	THEN 
		DELETE FROM drive_file_cvt WHERE fileId = OLD.fileId;
		DELETE FROM drive_file_relation WHERE fileId = OLD.fileId;
	END IF;
	IF NEW.fileId != OLD.fileId
	THEN
		UPDATE drive_file_cvt SET fileId = NEW.fileId WHERE fileId = OLD.fileId;
		UPDATE drive_file_relation SET fileId = NEW.fileId WHERE fileId = OLD.fileId;
	END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 트리거 b_plms.TR_MEETING_APPROVAL_AF_UPDATE 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `TR_MEETING_APPROVAL_AF_UPDATE` AFTER UPDATE ON `meeting_approval` FOR EACH ROW BEGIN
	/* 결재 요청 처리가 완료된 경우 해당 사용신청에 대한 스케줄이 DB 에 등록되 있으면 해당 칼럼의 결재 상태를 처리 내용과 동기화 */
	/* 결재 요청이 삭제 요청(-1) 인 경우 해당 사용신청에 대해 삭제 처리 */
	DECLARE _MEETING_ID INTEGER;
	DECLARE _APP_STATUS INTEGER;
	/*결재 처리에 성공한 경우*/
	IF NEW.approvedYN = 'Y'
	 	THEN 
	 		IF NEW.approvalStatus = -1 
				THEN UPDATE meeting_schedule SET delYN = 'Y', approvalStatus = NEW.approvalStatus, approvalComment = NEW.approvalComment WHERE scheduleId = NEW.scheduleId;
			ELSE 
				SELECT meetingId, approvalStatus INTO _MEETING_ID, _APP_STATUS
				FROM meeting_schedule
				WHERE scheduleId = NEW.scheduleId;
				
				IF _APP_STATUS = NEW.approvalStatus
					THEN CALL PROC_UPDATE_MEETING_INFO_STATUS(_MEETING_ID, _APP_STATUS);
				ELSE
					UPDATE meeting_schedule 
		 			SET approvalStatus = NEW.approvalStatus, approvalComment = NEW.approvalComment
		 			WHERE scheduleId = NEW.scheduleId;
		 		END IF;
		 	END IF;
	/*결재 처리에 실패한 경우 코멘트만 업데이트*/	 	
	ELSE 
		UPDATE meeting_schedule 
		 		SET approvalComment = NEW.approvalComment
		 		WHERE scheduleId = NEW.scheduleId;
 	END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 트리거 b_plms.TR_MEETING_INFO_AF_UPDATE 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `TR_MEETING_INFO_AF_UPDATE` AFTER UPDATE ON `meeting_info` FOR EACH ROW BEGIN
	/*
	* 진행이 종료된 회의가 기밀회의인경우, 등록된 모든 자료를 삭제처리
	*/
	IF NEW.meetingStatus = 'END' AND NEW.secretYN = 'Y'
	THEN
		UPDATE drive_file_info SET delYN = 'Y' WHERE fileId IN (
			SELECT fildId
			FROM drive_file_relation
			WHERE relatedEntityType = 10
		);
	END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 트리거 b_plms.TR_MEETING_SCHEDULE_AF_DELETE 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `TR_MEETING_SCHEDULE_AF_DELETE` AFTER DELETE ON `meeting_schedule` FOR EACH ROW BEGIN
	DELETE FROM meeting_info WHERE meetingId = OLD.meetingId;
	DELETE FROM meeting_attendee WHERE meetingId = OLD.meetingId;
	DELETE FROM meeting_report WHERE meetingId = OLD.meetingId;
	DELETE FROM drive_file_relation WHERE relatedEntityId = OLD.meetingId AND relatedEntityType = 10;
	DELETE perm, grp FROM meeting_permission perm JOIN meeting_grp_permission grp ON perm.permId = grp.permId WHERE grp.meetingId = OLD.meetingId;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 트리거 b_plms.TR_MEETING_SCHEDULE_AF_INSERT 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `TR_MEETING_SCHEDULE_AF_INSERT` AFTER INSERT ON `meeting_schedule` FOR EACH ROW BEGIN
/* 새로 등록된 스케줄에 대한 기본적인 권한 발급 처리 */
	CALL PROC_CERT_MEETING_DEFAULT_AUTH(NEW.writerId, NEW.meetingId);
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 트리거 b_plms.TR_MEETING_SCHEDULE_AF_UPDATE 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `TR_MEETING_SCHEDULE_AF_UPDATE` AFTER UPDATE ON `meeting_schedule` FOR EACH ROW BEGIN
	IF NEW.delYN != OLD.delYN
	THEN
		UPDATE meeting_info SET delYN = NEW.delYN, stickyBit = 0 WHERE meetingId = NEW.meetingId;
		UPDATE meeting_attendee SET delYN = NEW.delYN WHERE meetingId = NEW.meetingId;
		DELETE FROM meeting_report WHERE meetingId = NEW.meetingId;
		DELETE FROM drive_file_relation WHERE relatedEntityId = OLD.meetingId AND relatedEntityType = 10;
		DELETE perm, grp FROM meeting_permission perm JOIN meeting_grp_permission grp ON perm.permId = grp.permId WHERE grp.meetingId = OLD.meetingId;
	END IF;
	
	IF NEW.approvalStatus != OLD.approvalStatus
	THEN 
		CALL PROC_UPDATE_MEETING_INFO_STATUS(NEW.meetingId, NEW.approvalStatus);
	END IF;
	IF NEW.approvalStatus = 3 OR NEW.approvalStatus = 4
	THEN
		DELETE FROM drive_file_relation WHERE relatedEntityId = OLD.meetingId AND relatedEntityType = 10;
	END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 트리거 b_plms.TR_ORG_DEPT_INFO_AF_DELETE 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `TR_ORG_DEPT_INFO_AF_DELETE` AFTER DELETE ON `org_dept_info` FOR EACH ROW BEGIN
	UPDATE org_user_info SET deptId = 0 WHERE deptId = OLD.deptId;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 트리거 b_plms.TR_ORG_DEPT_INFO_AF_UPDATE 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `TR_ORG_DEPT_INFO_AF_UPDATE` AFTER UPDATE ON `org_dept_info` FOR EACH ROW BEGIN
	IF NEW.delYN = 'Y'
	THEN
		UPDATE org_user_info SET deptId = 0 WHERE deptId = NEW.deptId;
	END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 트리거 b_plms.TR_USER_INFO_AF_DELETE 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `TR_USER_INFO_AF_DELETE` AFTER DELETE ON `org_user_info` FOR EACH ROW BEGIN
	DELETE FROM user_account WHERE userId = OLD.userId;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- 트리거 b_plms.TR_USER_INFO_AF_UPDATE 구조 내보내기
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `TR_USER_INFO_AF_UPDATE` AFTER UPDATE ON `org_user_info` FOR EACH ROW BEGIN
	IF NEW.delYN != OLD.delYN AND NEW.delYN = 'Y'
	THEN 
		UPDATE user_account SET STATUS = 'DEACTIVATED' WHERE userId = NEW.userId;
	END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
