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


-- ewp_plms 데이터베이스 구조 내보내기
CREATE DATABASE IF NOT EXISTS `ewp_plms` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;
USE `ewp_plms`;

-- 테이블 ewp_plms.admin_domain_info 구조 내보내기
CREATE TABLE IF NOT EXISTS `admin_domain_info` (
  `userId` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '사번',
  `domainRole` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '관리자 구분',
  `regDateTime` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`userId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='프로그램 계층 관리자 명단';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 ewp_plms.admin_manager_info 구조 내보내기
CREATE TABLE IF NOT EXISTS `admin_manager_info` (
  `userId` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `officeCode` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `deptId` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `managerRole` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`userId`,`managerRole`,`officeCode`,`deptId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='기능 관리자 명단';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 ewp_plms.alarm_send 구조 내보내기
CREATE TABLE IF NOT EXISTS `alarm_send` (
  `userKey` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '유저 고유키',
  `alarmDiv` varchar(1) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '알람 구분(E:e-mail, S:sms, A:알림톡, P:push)',
  `alarmNo` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  `alarmDtlDiv` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '회의시작 안내발송 발송구분: MONTH: 한달전, DAY: 하루전, HOUR: 한시간전 발송 ',
  `meetingKey` int(11) NOT NULL COMMENT '회의 고유키',
  `alarmEmail` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '이메일주소',
  `alarmTel` varchar(13) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '수신 전화번호',
  `alarmSubject` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '알람 제목',
  `alarmBody` mediumtext COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '알람 BODY',
  `mailPurpose` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '메일 용도',
  `mailType` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '메일 종류',
  `mailLinkUrl` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'mail link url',
  `mailRcvName` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '수신자명',
  `mailRole` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT 'role',
  `templateCode` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '알림톡 템플릿 코드',
  `alarmDate` datetime NOT NULL DEFAULT current_timestamp() COMMENT '알림발송일자',
  `alarmRlt` varchar(1) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '발송처리결과(NULL:처리되지 않음, S:발송성공, F:발송실패, N:발송안함)',
  `cancelYn` varchar(1) COLLATE utf8mb4_unicode_ci DEFAULT 'N' COMMENT '발송 취소여부(Y:취소, N:발송)',
  `readYn` varchar(1) COLLATE utf8mb4_unicode_ci DEFAULT 'N' COMMENT '일림리스트, 읽음 여부(Y:읽음, N:미읽음)',
  `regDateTime` datetime NOT NULL DEFAULT current_timestamp() COMMENT '등록일시',
  `modDateTime` datetime NOT NULL DEFAULT current_timestamp() COMMENT '수정일시',
  PRIMARY KEY (`userKey`,`alarmDiv`,`alarmNo`),
  KEY `IDX1_alarm_send` (`meetingKey`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 ewp_plms.assign_approval 구조 내보내기
CREATE TABLE IF NOT EXISTS `assign_approval` (
  `appId` int(11) NOT NULL AUTO_INCREMENT COMMENT '결재 요청키',
  `userKey` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '결재 담당자키(자동처리인 경우에도 사업소 결재담당자 키 지정)',
  `roomType` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '결재 대상 배정의 장소유형',
  `reqKey` int(11) DEFAULT NULL COMMENT '결재 대상 배정키',
  `skdKey` int(11) NOT NULL COMMENT '결재 대상 스케줄키',
  `appStatus` int(11) NOT NULL COMMENT '결재',
  `appComment` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '결재 코멘트',
  `appYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '결재 처리 여부',
  `successYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '결재 성공 여부',
  `appDateTime` datetime DEFAULT NULL COMMENT '결재 처리 일시',
  `regDateTime` datetime NOT NULL DEFAULT current_timestamp() COMMENT '등록일시',
  `modDateTime` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '수정일시',
  PRIMARY KEY (`appId`) USING BTREE,
  KEY `IDX1_assign_approval` (`roomType`,`reqKey`),
  KEY `IDX2_assign_approval` (`skdKey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 ewp_plms.auth_code 구조 내보내기
CREATE TABLE IF NOT EXISTS `auth_code` (
  `authId` int(11) NOT NULL AUTO_INCREMENT,
  `authCode` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '기능코드',
  `authDiv` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'COM' COMMENT '코드분류\r\n-COM: 공통\r\n-MT: 회의\r\n-FILE: 파일',
  `authName` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '기능명',
  `authDesc` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '기능상세',
  `authVal` int(11) NOT NULL COMMENT '기능값(2진수)',
  PRIMARY KEY (`authId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 ewp_plms.board_notice 구조 내보내기
CREATE TABLE IF NOT EXISTS `board_notice` (
  `noticeId` int(11) NOT NULL AUTO_INCREMENT COMMENT '공지사항 번호',
  `writerId` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '작성자 키값',
  `officeCode` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '사업소 키값',
  `fixYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '상단고정여부 Y/N',
  `title` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '공지 제목',
  `contents` mediumtext COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '공지 내용',
  `regDateTime` datetime NOT NULL DEFAULT current_timestamp() COMMENT '등록일',
  `modDateTime` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '수정일',
  `delYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '삭제여부',
  PRIMARY KEY (`noticeId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 ewp_plms.common_code 구조 내보내기
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

-- 테이블 ewp_plms.drive_file_cvt 구조 내보내기
CREATE TABLE IF NOT EXISTS `drive_file_cvt` (
  `cvtId` int(11) NOT NULL AUTO_INCREMENT COMMENT '변환키',
  `fileId` int(11) NOT NULL COMMENT '파일키',
  `cvtPriority` int(11) NOT NULL DEFAULT 0 COMMENT '변환 시도 우선순위(0,1,2,3)',
  `cvtCount` int(11) NOT NULL DEFAULT 0 COMMENT '변환 시도 횟수',
  `cvtStep` int(11) NOT NULL DEFAULT 0 COMMENT '변환 시도 단계(-1:실패,0:대기,1:변환중,2:성공)',
  `conversionType` enum('DOC_TO_PDF','PDF_TO_IMAGES','IMAGES_TO_PDF','IMAGE_TO_WEBP') COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '변환 유형',
  `startDateTime` datetime DEFAULT NULL COMMENT '변환 시작(YYYYMMDDHHmiss)',
  `endDateTime` datetime DEFAULT NULL COMMENT '변환 종료(YYYYMMDDHHmiss)',
  `errMsg` varchar(5000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '에러 메시지',
  `regDateTime` datetime DEFAULT current_timestamp(),
  `modDateTime` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`cvtId`) USING BTREE,
  KEY `IDX1_file_cvt` (`fileId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 ewp_plms.drive_file_info 구조 내보내기
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
  `conversionStatus` int(11) NOT NULL DEFAULT 100 COMMENT '변환상태\r\n-100: 변환전\r\n-200: 문서를 PDF파일로 변환\r\n-210: PDF파일 페이지별 이미지 생성\r\n-220: 이미지 파일 WEBP로 변환\r\n-230: 페이지별 이미지 PDF파일로 변환\r\n-300: 변환 완료\r\n-400: 변환 실패',
  `pdfGeneratedYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT 'PDF 생성 여부',
  `pageImagesGeneratedYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '페이지별 이미지 생성 여부',
  `webpGeneratedYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT 'WebP 생성 여부',
  `regDateTime` datetime NOT NULL DEFAULT current_timestamp() COMMENT '등록시간',
  `modDateTime` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '수정시간',
  `delYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '삭제 여부',
  PRIMARY KEY (`fileId`) USING BTREE,
  KEY `IDX1_file_info` (`sourceId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='*원본 파일 저장경로(년/월은 파일 생성일자를 따름)\r\n-User-uploaded files: ROOT_PATH\\uploadFile\\user\\userId\\year\\month\\uuid\\uuid.ext\r\n-View files: ROOT_PATH\\uploadFile\\user\\userId\\year\\month\\uuid\\uuid.pdf\r\n*판서 관련 파일 저장경로(년/월은 파일 생성일자를 따름)\r\n-Page-specific image files for writing: ROOT_PATH\\uploadFile\\meeting\\meetingKey\\SOURCE\\fileKey\\0001.webp~pages.web\r\n-User-specific annotation files: ROOT_PATH\\uploadFile\\meeting\\meetingKey\\EDITION\\fileKey\\0001.webp~pages.web\r\n-Converted PDF files after the meeting: ROOT_PATH\\uploadFile\\user\\userId\\year\\month\\fileKey (same as user-uploaded files)';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 ewp_plms.drive_file_relation 구조 내보내기
CREATE TABLE IF NOT EXISTS `drive_file_relation` (
  `relationId` int(11) NOT NULL AUTO_INCREMENT COMMENT '관계키',
  `registerId` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '등록자키',
  `fileId` int(11) NOT NULL COMMENT '파일키',
  `relatedEntityId` int(11) NOT NULL COMMENT '연관된 개체키',
  `relatedEntityType` int(11) NOT NULL COMMENT '연관된 개체 유형\r\n-10: 회의\r\n-20: 사용자게시판\r\n-21: 공지사항게시판',
  `relationType` int(11) NOT NULL COMMENT '관계유형\r\n-100: 회의 원본자료\r\n-101: 회의 메모\r\n-102: 회의 판서본\r\n-103: 회의 회의록\r\n-104: 회의 사진기록\r\n-105: 회의 녹음기록\r\n-200: 게시판 첨부파일',
  `regDateTime` datetime NOT NULL DEFAULT current_timestamp() COMMENT '등록일',
  PRIMARY KEY (`relationId`) USING BTREE,
  UNIQUE KEY `fileId_relatedEntityId_relatedEntityType_relationType` (`fileId`,`relatedEntityId`,`relatedEntityType`,`relationType`),
  KEY `IDX1_file_relation` (`fileId`) USING BTREE,
  KEY `IDX2_file_relation` (`relatedEntityId`,`relatedEntityType`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 ewp_plms.file_office_to_pdf 구조 내보내기
CREATE TABLE IF NOT EXISTS `file_office_to_pdf` (
  `otpId` int(11) NOT NULL AUTO_INCREMENT COMMENT '키',
  `fileKey` int(11) NOT NULL COMMENT '파일키',
  `officeType` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '*오피스 문서 분류\r\n-HWP: 한글\r\n-WORD: 워드\r\n-PPT: 파워포인트\r\n-EXCEL: 엑셀',
  `rootPath` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '파일 최상위 폴더 경로',
  `officePath` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '오피스 파일 경로',
  `pdfPath` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '변환된 PDF 파일이 위치할 경로',
  `cvtPriority` int(11) NOT NULL DEFAULT 0 COMMENT '변환 시도 우선순위(0,1,2,3)',
  `cvtCount` int(11) NOT NULL DEFAULT 0 COMMENT '변환 시도 횟수',
  `cvtStep` int(11) NOT NULL DEFAULT 0 COMMENT '변환 시도 단계(-1:실패,0:대기,1:변환중,2:성공)',
  PRIMARY KEY (`otpId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 ewp_plms.meeting_attendee 구조 내보내기
CREATE TABLE IF NOT EXISTS `meeting_attendee` (
  `attendKey` int(11) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `meetingKey` int(11) NOT NULL COMMENT '회의키',
  `userKey` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '유저키',
  `userName` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '유저명',
  `deptId` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '부서키',
  `attendRole` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '*참석유형\r\n-ATTENDEE: 참석자\r\n-OBSERVER: 참관자\r\n-GUEST: 외부참석자',
  `assistantYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '보조 진행자 지정여부',
  `attendYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '참석여부(회의에 들어옴)',
  `attendDateTime` datetime DEFAULT NULL COMMENT '참석시간',
  `exitYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '종료여부',
  `exitDateTime` datetime DEFAULT NULL COMMENT '종료시간',
  `signYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '사인여부(회의에 들어와서 사인)',
  `signSrc` mediumtext COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '사인내용',
  `tempPW` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '회의임시비밀번호',
  `expireDate` date DEFAULT NULL COMMENT '비밀번호만료기간',
  `delYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '삭제여부',
  PRIMARY KEY (`attendKey`),
  UNIQUE KEY `IDX1_meeting_attendee` (`meetingKey`,`userKey`,`deptId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 ewp_plms.meeting_file_cvt 구조 내보내기
CREATE TABLE IF NOT EXISTS `meeting_file_cvt` (
  `cvtKey` int(11) NOT NULL AUTO_INCREMENT COMMENT '변환키',
  `meetingKey` int(11) DEFAULT NULL COMMENT '회의키',
  `fileKey` int(11) NOT NULL COMMENT '파일키',
  `cvtPriority` int(11) NOT NULL DEFAULT 0 COMMENT '변환 시도 우선순위(0,1,2,3)',
  `cvtCount` int(11) NOT NULL DEFAULT 0 COMMENT '변환 시도 횟수',
  `cvtStep` int(11) NOT NULL DEFAULT 0 COMMENT '변환 시도 단계(-1:실패,0:대기,1:변환중,2:성공)',
  `startDT` datetime DEFAULT NULL COMMENT '변환 시작(YYYYMMDDHHmiss)',
  `endDT` datetime DEFAULT NULL COMMENT '변환 종료(YYYYMMDDHHmiss)',
  `errMsg` varchar(5000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '에러 메시지',
  `regDT` datetime DEFAULT current_timestamp(),
  `modDT` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`cvtKey`),
  KEY `IDX1_file_cvt` (`meetingKey`,`fileKey`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 ewp_plms.meeting_file_info 구조 내보내기
CREATE TABLE IF NOT EXISTS `meeting_file_info` (
  `fileKey` int(11) NOT NULL AUTO_INCREMENT COMMENT '파일키',
  `meetingKey` int(11) NOT NULL COMMENT '회의키',
  `empKey` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '소유자키',
  `originalKey` int(11) DEFAULT NULL COMMENT '원본키',
  `roleType` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '*파일역할\r\n-MATERIAL: 자료\r\n-COPY: 판서\r\n-REPORT: 회의록\r\n-PHOTO: 사진기록\r\n-VOICE: 음성기록',
  `mimeType` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '*파일유형\r\n-HWP: 한글\r\n-WORD: 워드\r\n-PPT: 파워포인트\r\n-EXCEL: 엑셀\r\n-PDF: PDF\r\n-IMG: 이미지',
  `originalName` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '파일 업로드시 이름(확장자포함)',
  `fileName` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '현재 파일 이름',
  `fileExt` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '현재 파일 확장자',
  `uuid` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '서버에 저장될 파일명',
  `size` int(11) DEFAULT NULL COMMENT '파일 크기(byte)',
  `page` int(11) NOT NULL DEFAULT 0 COMMENT '파일 페이지수(인쇄시)',
  `state` int(11) NOT NULL DEFAULT 0 COMMENT '파일 상태 -3:삭제,-2:기간만료,-1:실패, 0:대기, 1:진행, 2:생성됨',
  `stickyBit` int(11) NOT NULL DEFAULT 15 COMMENT '허용권한(사용X)',
  `rootPath` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '파일경로(사용X)',
  `regDateTime` datetime NOT NULL DEFAULT current_timestamp() COMMENT '등록시간',
  `modDateTime` datetime NOT NULL DEFAULT current_timestamp() COMMENT '수정시간',
  `expDateTime` datetime DEFAULT NULL COMMENT '만료시간',
  `delYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '삭제여부',
  PRIMARY KEY (`fileKey`),
  KEY `IDX2_file_info` (`empKey`) USING BTREE,
  KEY `IDX1_file_info` (`meetingKey`,`fileKey`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 ewp_plms.meeting_grp_permission 구조 내보내기
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

-- 테이블 ewp_plms.meeting_info 구조 내보내기
CREATE TABLE IF NOT EXISTS `meeting_info` (
  `meetingKey` int(11) NOT NULL AUTO_INCREMENT COMMENT '회의 고유키',
  `writerKey` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '등록 사원 고유키',
  `title` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '회의제목',
  `contents` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '회의내용',
  `meetingStatus` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'NEW' COMMENT '회의상태\r\n-NEW: 새로 등록됨\r\n-UNAPPROVAL: 미승인\r\n-APPROVED: 승인(예약)\r\n-OPENING: 개시\r\n-START: 회의시작\r\n-FINISH: 회의종료\r\n-CLOSING: 마감\r\n-END: 끝남\r\n-CANCEL: 취소\r\n-DROP: 드랍',
  `secuLevel` int(11) NOT NULL DEFAULT 3 COMMENT '회의 조회에 적용할 보안등급\r\n1: 기밀회의\r\n3: 일반회의',
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
  `delYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '삭제여부',
  PRIMARY KEY (`meetingKey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 ewp_plms.meeting_permission 구조 내보내기
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 ewp_plms.meeting_report 구조 내보내기
CREATE TABLE IF NOT EXISTS `meeting_report` (
  `meetingKey` int(11) NOT NULL COMMENT '회의키',
  `reporterKey` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '작성자키',
  `contents` mediumtext COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '회의록내용',
  `reportStatus` int(11) NOT NULL DEFAULT 0 COMMENT '*상태값\r\n0: 임시저장\r\n1: 의견요청\r\n2: 최종등록',
  `regDateTime` datetime NOT NULL DEFAULT current_timestamp() COMMENT '등록시간',
  `modDateTime` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '수정시간',
  PRIMARY KEY (`meetingKey`),
  KEY `IDX1_meeting_report` (`reporterKey`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 ewp_plms.meeting_report_opn 구조 내보내기
CREATE TABLE IF NOT EXISTS `meeting_report_opn` (
  `opnId` int(11) NOT NULL AUTO_INCREMENT COMMENT '검토의견 번호',
  `meetingKey` int(11) NOT NULL COMMENT '회의 번호',
  `writerKey` varchar(100) NOT NULL DEFAULT '' COMMENT '검토의견 작성자 유저키',
  `writerName` varchar(100) DEFAULT NULL COMMENT '검토의견 작성자 명',
  `writerTel` varchar(10) DEFAULT NULL COMMENT '내선번호',
  `comment` tinytext DEFAULT NULL COMMENT '검토의견 내용',
  `regDateTime` datetime NOT NULL DEFAULT current_timestamp() COMMENT '등록시간',
  `delYN` char(1) DEFAULT 'N' COMMENT '삭제여부 Y/N',
  PRIMARY KEY (`opnId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='검토의견 테이블';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 ewp_plms.meeting_schedule 구조 내보내기
CREATE TABLE IF NOT EXISTS `meeting_schedule` (
  `skdKey` int(11) NOT NULL AUTO_INCREMENT COMMENT '스케줄키',
  `writerKey` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '작성자키',
  `meetingKey` int(11) DEFAULT NULL COMMENT '회의키',
  `attendeeCnt` int(11) NOT NULL DEFAULT 0 COMMENT '참석자수',
  `officeCode` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '사업소/회사키',
  `deptId` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '부서키',
  `reqKey` int(11) DEFAULT NULL COMMENT '회의실 사용요청키',
  `roomKey` int(11) DEFAULT NULL COMMENT '회의실키',
  `roomType` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '*회의실 타입(강의실/회의실/강당 등)\r\n-강당(HALL)\r\n-강의실(EDU_ROOM)\r\n-회의실(MEETING_ROOM)',
  `customPlace` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '사용자 정의 장소',
  `skdHost` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '스케줄/배정 신청 주최자',
  `skdType` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'FORMAL' COMMENT '*회의 스케줄 유형\r\n-RENTAL: 장소대여\r\n-FORMAL: 정규 회의(장소,시작/종료 시간을 예약)\r\n-INSTANT: 즉시 회의',
  `skdStatus` int(11) NOT NULL DEFAULT 0 COMMENT '승인 상태\r\n0: 미신청\r\n1: 신청\r\n2: 자동 승인\r\n3: 승인 취소\r\n4: 승인 불가\r\n',
  `skdComment` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '상태 코멘트',
  `holdingDate` date DEFAULT NULL COMMENT '회의개최일',
  `beginDateTime` datetime NOT NULL COMMENT '시작시간',
  `finishDateTime` datetime DEFAULT NULL COMMENT '종료시간',
  `expDateTime` datetime DEFAULT NULL COMMENT '만료시간',
  `regDateTime` datetime NOT NULL DEFAULT current_timestamp() COMMENT '등록시간',
  `modDateTime` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '수정시간',
  `delYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '삭제여부',
  PRIMARY KEY (`skdKey`),
  UNIQUE KEY `IDX2_meeting_schedule` (`reqKey`,`roomType`),
  KEY `IDX1_meeting_schedule` (`meetingKey`) USING BTREE,
  KEY `IDX3_meeting_schedule` (`holdingDate`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 ewp_plms.meeting_security_agreement 구조 내보내기
CREATE TABLE IF NOT EXISTS `meeting_security_agreement` (
  `attendId` int(11) NOT NULL,
  `signSrc` mediumtext COLLATE utf8mb4_unicode_ci NOT NULL,
  `agreeDateTime` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`attendId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 ewp_plms.room_info 구조 내보내기
CREATE TABLE IF NOT EXISTS `room_info` (
  `roomKey` int(11) NOT NULL COMMENT '장소고유키',
  `roomType` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '장소분류(회의실,강의실,강당)',
  `officeCode` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '사업소코드',
  `roomCode` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '장소코드',
  `roomName` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '장소명',
  `roomLabel` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '장소표기명',
  `roomSize` int(11) DEFAULT NULL COMMENT '장소좌석규모',
  `roomFloor` int(11) DEFAULT NULL COMMENT '장소층수',
  `roomNote` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '장소기타정보',
  `rentYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Y' COMMENT '대여가능여부',
  `rentReason` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '대여불가사유',
  `regUser` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '등록자고유키',
  `regDateTime` datetime NOT NULL DEFAULT current_timestamp() COMMENT '등록일자',
  `modUser` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '수정자고유키',
  `modDateTime` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '수정일자',
  `syncYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '경영지원서비스와 동기화여부',
  `delYN` char(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '삭제여부',
  PRIMARY KEY (`roomKey`,`roomType`),
  UNIQUE KEY `roomCode` (`roomCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='동서발전 DB에 등록된 장소를 포함.\r\n경영지원서비스와 동기화하지 않는 장소에 사용신청을 한 경우 사용신청도 동서발전 DB와 동기화하지 않음.\r\n경영지원서비스에 등록된 장소키는 그대로 사용하고 자체적으로 등록한 장소의 키는 앞에 구분자 E를 붙임';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 ewp_plms.room_permission 구조 내보내기
CREATE TABLE IF NOT EXISTS `room_permission` (
  `roomPermKey` int(11) NOT NULL AUTO_INCREMENT COMMENT '회의장소 대여 권한 고유키',
  `roomCode` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '회의장소 품목코드',
  `officeCode` varchar(4) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '사업소 코드',
  `roomType` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `roomKey` int(11) NOT NULL,
  `deptId` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '부서 코드',
  `deptName` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '부서명',
  `regUser` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '등록자',
  `regDate` date DEFAULT NULL COMMENT '등록일',
  PRIMARY KEY (`roomPermKey`),
  KEY `IDX2_admin_room_permission` (`officeCode`,`roomType`,`roomKey`) USING BTREE,
  KEY `IDX1_admin_room_permission` (`roomCode`,`officeCode`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='부서별 대여 가능 회의장소';

-- 내보낼 데이터가 선택되어 있지 않습니다.

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
