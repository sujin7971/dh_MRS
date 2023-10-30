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

-- 테이블 데이터 ewp_plms.auth_code:~17 rows (대략적) 내보내기
DELETE FROM `auth_code`;
/*!40000 ALTER TABLE `auth_code` DISABLE KEYS */;
INSERT INTO `auth_code` (`authId`, `authCode`, `authDiv`, `authName`, `authDesc`, `authVal`) VALUES
	(1, 'FUNC_READ', 'COM', 'READ', NULL, 1),
	(2, 'FUNC_VIEW', 'COM', 'VIEW', NULL, 2),
	(3, 'FUNC_UPDATE', 'COM', 'UPDATE', NULL, 4),
	(4, 'FUNC_DELETE', 'COM', 'DELETE', NULL, 8),
	(5, 'FUNC_DOWN', 'FILE', 'DOWN', NULL, 16),
	(6, 'FUNC_UPLOAD', 'MT', 'UPLOAD', NULL, 32),
	(7, 'FUNC_INVITE', 'MT', 'INVITE', NULL, 64),
	(8, 'FUNC_ATTEND', 'MT', 'ATTEND', NULL, 128),
	(9, 'FUNC_COPY', 'MT', 'COPY', NULL, 256),
	(10, 'FUNC_REPORT', 'MT', 'REPORT', NULL, 512),
	(11, 'FUNC_EXTEND', 'MT', 'EXTEND', NULL, 1024),
	(12, 'FUNC_CHECK', 'MT', 'CHECK', NULL, 2048),
	(13, 'FUNC_SIGN', 'MT', 'SIGN', NULL, 4096),
	(14, 'FUNC_PHOTO', 'MT', 'PHOTO', NULL, 8192),
	(15, 'FUNC_VOICE', 'MT', 'VOICE', NULL, 16384),
	(16, 'FUNC_FINISH', 'MT', 'FINISH', NULL, 32768),
	(17, 'FUNC_DELETE', 'MT', 'DELETE', NULL, 8),
	(18, 'FUNC_UPDATE', 'MT', 'UPDATE', NULL, 4),
	(19, 'FUNC_VIEW', 'MT', 'VIEW', NULL, 2),
	(20, 'FUNC_CANCEL', 'MT', 'CANCEL', NULL, 16),
	(21, 'FUNC_READ', 'MT', 'READ', NULL, 1),
	(22, 'FUNC_NONE', 'MT', 'NONE', NULL, 0);
/*!40000 ALTER TABLE `auth_code` ENABLE KEYS */;

-- 테이블 데이터 ewp_plms.common_code:~84 rows (대략적) 내보내기
DELETE FROM `common_code`;
/*!40000 ALTER TABLE `common_code` DISABLE KEYS */;
INSERT INTO `common_code` (`CLASS_CD`, `CLASS_NM`, `DTL_CD`, `DTL_NM`, `DISP_COL_NM`, `CLASS_TYPE`, `SORT_SEQ`, `RMK`, `USE_YN`, `ETC_COL1`, `ETC_COL2`, `ETC_COL3`, `ETC_COL4`, `UPD_CODE`, `INS_DATE`, `INS_EMP_ID`, `LAST_UPD_DATE`, `LAST_UPD_EMP_ID`) VALUES
	('CD001', '사업소', '1000', '본사', '본사', 'OFFICE', 1, '주석', 'Y', 'AUTH_AUTO', NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-12-02 00:00:00', '1022'),
	('CD001', '사업소', '2000', '당진발전본부', '당진발전본부', 'OFFICE', 2, NULL, 'Y', 'AUTH_MANUAL', NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD001', '사업소', '3000', '울산발전본부', '울산발전본부', 'OFFICE', 3, NULL, 'Y', 'AUTH_MANUAL', NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD001', '사업소', '4000', '호남발전본부', '호남발전본부', 'OFFICE', 4, NULL, 'Y', 'AUTH_MANUAL', NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD001', '사업소', '5000', '동해발전본부', '동해발전본부', 'OFFICE', 5, NULL, 'Y', 'AUTH_MANUAL', NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD001', '사업소', '6000', '일산발전본부', '일산발전본부', 'OFFICE', 6, NULL, 'Y', 'AUTH_MANUAL', NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD001', '사업소', 'B000', '미래기술융합원', '미래기술융합원', 'OFFICE', 7, NULL, 'Y', 'AUTH_MANUAL', NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD002', '관리자', 'ADMIN', '시스템관리자', '시스템관리자', 'ADMIN', 2, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD002', '관리자', 'MASTER', '통합관리자', '통합관리자', 'ADMIN', 1, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD003', '회의실', 'EDU_ROOM', '강의실', '강의실', 'ROOM', 2, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD003', '회의실', 'HALL', '강당', '강당', 'ROOM', 1, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD003', '회의실', 'MEETING_ROOM', '회의실', '회의실', 'ROOM', 3, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD004', '알람', 'A', '알림톡', '알림톡', 'ALARM', 3, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD004', '알람', 'B', '알림', '알림', 'ALARM', 5, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-12-28 16:18:01', 'SYSTEM'),
	('CD004', '알람', 'E', '이메일', '이메일', 'ALARM', 1, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD004', '알람', 'M', '메신저', '메신저', 'ALARM', 4, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-12-28 16:16:23', 'SYSTEM'),
	('CD004', '알람', 'S', '문자메시지', '문자메시지', 'ALARM', 2, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-12-28 16:14:24', 'SYSTEM'),
	('CD005', '결재', '1', '등록', '등록', 'APP_STATUS', 1, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD005', '결재', '2', '승인', '승인', 'APP_STATUS', 2, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD005', '결재', '3', '취소', '취소', 'APP_STATUS', 3, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD005', '결재', '4', '불가', '불가', 'APP_STATUS', 4, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD006', '변환', '-1', '실패', '실패', 'CVT_STEP', 1, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD006', '변환', '0', '대기', '대기', 'CVT_STEP', 2, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD006', '변환', '1', '변환중', '변환중', 'CVT_STEP', 3, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD006', '변환', '2', '성공', '성공', 'CVT_STEP', 4, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD007', '파일역할', 'COPY', '판서', '판서', 'FILE_ROLE', 2, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD007', '파일역할', 'MATERIAL', '자료', '자료', 'FILE_ROLE', 1, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD007', '파일역할', 'PHOTO', '사진기록', '사진기록', 'FILE_ROLE', 4, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD007', '파일역할', 'REPORT', '회의록', '회의록', 'FILE_ROLE', 3, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD007', '파일역할', 'VOICE', '음성기록', '음성기록', 'FILE_ROLE', 5, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD008', '파일유형', 'EXCEL', '엑셀', '엑셀', 'FILE_TYPE', 4, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD008', '파일유형', 'HWP', '한글', '한글', 'FILE_TYPE', 1, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD008', '파일유형', 'IMG', '이미지', '이미지', 'FILE_TYPE', 6, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD008', '파일유형', 'PDF', 'PDF', 'PDF', 'FILE_TYPE', 5, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD008', '파일유형', 'PPT', '파워포인트', '파워포인트', 'FILE_TYPE', 3, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD008', '파일유형', 'WORD', '워드', '워드', 'FILE_TYPE', 2, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD009', '파일상태', '-1', '실패', '실패', 'FILE_STATE', 3, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD009', '파일상태', '-2', '기간만료', '기간만료', 'FILE_STATE', 2, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD009', '파일상태', '-3', '삭제예정', '삭제예정', 'FILE_STATE', 1, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD009', '파일상태', '0', '대기', '대기', 'FILE_STATE', 4, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD009', '파일상태', '1', '진행', '진행', 'FILE_STATE', 5, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD009', '파일상태', '2', '생성', '생성', 'FILE_STATE', 6, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD010', '참석유형', 'ATTENDEE', '참석자', '참석자', 'ATTEND_ROLE', 1, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD010', '참석유형', 'GUEST', '외부참석자', '외부참석자', 'ATTEND_ROLE', 3, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD010', '참석유형', 'OBSERVER', '참관자', '참관자', 'ATTEND_ROLE', 2, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD011', '회의구분', 'DEPT', '부서코드', '부서코드', 'GRP_DIV', 3, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD011', '회의구분', 'DOMAIN', '계정계층', '계정계층', 'GRP_DIV', 1, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD011', '회의구분', 'ROLE', '회의역할', '회의역할', 'GRP_DIV', 2, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD011', '회의구분', 'USER', '사용자', '사용자', 'GRP_DIV', 4, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD012', '회의코드', 'DEPT', '부서코드', '부서코드', 'GRP_CODE', 2, 'DEPT: 부서코드,', 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD012', '회의코드', 'DOMAIN', '일반', '일반', 'GRP_CODE', 1, 'DOMAIN: 일반, 관리자, 최고 관리자 등', 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD012', '회의코드', 'ROLE', '주관자', '주관자', 'GRP_CODE', 1, 'ROLE: 주관자, 보조진행자, 작성자 등,', 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD012', '회의코드', 'USER', '사용자', '사용자', 'GRP_CODE', 3, 'USER: 사용자 식별키(사번)', 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD013', '회의상태', 'APPROVED', '예약', '예약', 'MEETING_STATUS', 2, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD013', '회의상태', 'CLOSING', '마감', '마감', 'MEETING_STATUS', 6, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD013', '회의상태', 'CANCEL', '취소', '취소', 'MEETING_STATUS', 8, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD013', '회의상태', 'DROP', '드랍', '드랍', 'MEETING_STATUS', 9, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD013', '회의상태', 'END', '끝남', '끝남', 'MEETING_STATUS', 7, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD013', '회의상태', 'FINISH', '회의종료', '회의종료', 'MEETING_STATUS', 5, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD013', '회의상태', 'OPENING', '개시', '개시', 'MEETING_STATUS', 3, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD013', '회의상태', 'START', '회의시작', '회의시작', 'MEETING_STATUS', 4, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD013', '회의상태', 'UNAPPROVAL', '미승인', '미승인', 'MEETING_STATUS', 1, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD014', '보안등급', '1', '기밀회의', '기밀회의', 'SECU_LEVEL', 1, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD014', '보안등급', '3', '일반회의', '일반회의', 'SECU_LEVEL', 2, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD015', '주최자공개범위', '1', '공개안함', '공개안함', 'HOST_SECU_LVL', 1, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD015', '주최자공개범위', '2', '주최자/보조진행자 공개', '주최자/보조진행자 공개', 'HOST_SECU_LVL', 2, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD015', '주최자공개범위', '3', '주최자/보조진행자 동부서 공개', '주최자/보조진행자 동부서 공개', 'HOST_SECU_LVL', 3, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD016', '참석자공개범위', '1', '공개안함', '공개안함', 'ATTEND_SECU_LVL', 1, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD016', '참석자공개범위', '2', '참석자공개', '참석자공개', 'ATTEND_SECU_LVL', 2, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD016', '참석자공개범위', '3', '참석자 동부서 공개', '참석자동부서공개', 'ATTEND_SECU_LVL', 3, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD017', '참관자공개범위', '1', '공개안함', '공개안함', 'OBSERVER_SECU_LVL', 1, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD017', '참관자공개', '2', '참관자공개', '참관자공개', 'OBSERVER_SECU_LVL', 2, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD017', '참관자공개', '3', '참관자 동부서 공개', '참관자 동부서 공개', 'OBSERVER_SECU_LVL', 3, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD018', '회의상태값', '0', '임시저장', '임시저장', 'REPORT_STATUS', 1, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD018', '회의상태값', '1', '의견요청', '의견요청', 'REPORT_STATUS', 2, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD018', '회의상태값', '2', '최종등록', '최종등록', 'REPORT_STATUS', 3, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD019', '스케쥴우형', 'FORMAL', '정규회의', '정규회의', 'SKD_TYPE', 2, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD019', '스케쥴유형', 'INSTANT', '즉시회의', '즉시회의', 'SKD_TYPE', 3, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD019', '스케쥴유형', 'RENTAL', '장소대여', '장소대여', 'SKD_TYPE', 1, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD020', '스케쥴상태', '0', '미신청', '미신청', 'SKD_STATUS', 1, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD020', '스케쥴상태', '1', '신청', '신청', 'SKD_STATUS', 2, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD020', '스케쥴상태', '2', '자동승인', '자동승인', 'SKD_STATUS', 3, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD020', '스케쥴상태', '3', '사용취소', '사용취소', 'SKD_STATUS', 4, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD020', '스케쥴상태', '4', '승인불가', '승인불가', 'SKD_STATUS', 5, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD021', '인사권한', '3', '일반사용자', '일반사용자', 'INSA_LEVEL', 1, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD021', '인사권한', '8', '시스템관리자', '시스템관리자', 'INSA_LEVEL', 2, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM'),
	('CD021', '인사권한', '9', '마스터관리자', '마스터관리자', 'INSA_LEVEL', 3, NULL, 'Y', NULL, NULL, NULL, NULL, 'C', '2022-11-29', 'SYSTEM', '2022-11-29 00:00:00', 'SYSTEM');
/*!40000 ALTER TABLE `common_code` ENABLE KEYS */;

-- 테이블 데이터 ewp_plms.meeting_grp_permission:~97 rows (대략적) 내보내기
DELETE FROM `meeting_grp_permission`;
/*!40000 ALTER TABLE `meeting_grp_permission` DISABLE KEYS */;
INSERT INTO `meeting_grp_permission` (`grpId`, `grpDiv`, `grpCode`, `meetingId`, `permId`) VALUES
	('DOMAIN_ROLE_SYSTEM_ADMIN_0', 'DOMAIN', 'ROLE_SYSTEM_ADMIN', 0, 2),
	('DOMAIN_ROLE_DEV_0', 'DOMAIN', 'ROLE_DEV', 0, 6),
	('DOMAIN_ROLE_GENERAL_0', 'DOMAIN', 'ROLE_GENERAL', 0, 1),
	('DOMAIN_ROLE_GUEST_0', 'DOMAIN', 'ROLE_GUEST', 0, 1),
	('DOMAIN_ROLE_MANAGER_0', 'DOMAIN', 'ROLE_MANAGER', 0, 3),
	('DOMAIN_ROLE_MASTER_ADMIN_0', 'DOMAIN', 'ROLE_MASTER_ADMIN', 0, 2);
/*!40000 ALTER TABLE `meeting_grp_permission` ENABLE KEYS */;

-- 테이블 데이터 ewp_plms.meeting_permission:~121 rows (대략적) 내보내기
DELETE FROM `meeting_permission`;
/*!40000 ALTER TABLE `meeting_permission` DISABLE KEYS */;
INSERT INTO `meeting_permission` (`permId`, `permLvl`, `permName`, `permDesc`, `regDateTime`, `modDateTime`, `expDateTime`, `delYN`) VALUES
	(1, 1, 'MHA001', NULL, '2022-06-19 22:30:00', '2022-06-19 22:30:00', NULL, 'N'),
	(2, 3, 'MHA002', NULL, '2022-06-19 22:30:00', '2022-06-19 22:30:00', NULL, 'N'),
	(3, 19, 'MHA003', NULL, '2022-06-19 22:30:00', '2022-06-19 22:30:00', NULL, 'N'),
	(4, 3, 'MHA004', NULL, '2022-06-19 22:30:00', '2022-06-19 22:30:00', NULL, 'N'),
	(5, 3, 'MHA005', NULL, '2022-06-19 22:30:00', '2022-06-19 22:30:00', NULL, 'N'),
	(6, 11, 'MHA006', NULL, '2022-06-19 22:30:00', '2022-06-19 22:30:00', NULL, 'N');
/*!40000 ALTER TABLE `meeting_permission` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
