-- --------------------------------------------------------
-- 호스트:                          127.0.0.1
-- 서버 버전:                        10.6.4-MariaDB - mariadb.org binary distribution
-- 서버 OS:                        Win64
-- HeidiSQL 버전:                  11.3.0.6295
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- 테이블 데이터 b-plms_comm_dev.auth_code:~17 rows (대략적) 내보내기
DELETE FROM `auth_code`;
/*!40000 ALTER TABLE `auth_code` DISABLE KEYS */;
INSERT INTO `auth_code` (`authId`, `authCode`, `authDiv`, `authName`, `authDesc`, `authVal`) VALUES
	(1, 'FUNC_READ', 'COM', 'READ', NULL, 1),
	(2, 'FUNC_VIEW', 'COM', 'VIEW', NULL, 2),
	(3, 'FUNC_UPDATE', 'COM', 'UPDATE', NULL, 4),
	(4, 'FUNC_DELETE', 'COM', 'DELETE', NULL, 8),
	(5, 'FUNC_CANCEL', 'MT', 'CANCEL', NULL, 16),
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
	(17, 'FUNC_DOWN', 'FILE', 'DOWN', NULL, 16);
/*!40000 ALTER TABLE `auth_code` ENABLE KEYS */;

-- 테이블 데이터 b-plms_comm_dev.meeting_grp_permission:~5 rows (대략적) 내보내기
DELETE FROM `meeting_grp_permission`;
/*!40000 ALTER TABLE `meeting_grp_permission` DISABLE KEYS */;
INSERT INTO `meeting_grp_permission` (`grpId`, `grpDiv`, `grpCode`, `meetingId`, `permId`) VALUES
	('DOMAIN_ROLE_SYSTEM_ADMIN_0', 'DOMAIN', 'ROLE_SYSTEM_ADMIN', 0, 2),
	('DOMAIN_ROLE_DEV_0', 'DOMAIN', 'ROLE_DEV', 0, 6),
	('DOMAIN_ROLE_GENERAL_0', 'DOMAIN', 'ROLE_GENERAL', 0, 1),
	('DOMAIN_ROLE_GUEST_0', 'DOMAIN', 'ROLE_GUEST', 0, 1),
	('DOMAIN_ROLE_MASTER_ADMIN_0', 'DOMAIN', 'ROLE_MASTER_ADMIN', 0, 2);
/*!40000 ALTER TABLE `meeting_grp_permission` ENABLE KEYS */;

-- 테이블 데이터 b-plms_comm_dev.meeting_permission:~6 rows (대략적) 내보내기
DELETE FROM `meeting_permission`;
/*!40000 ALTER TABLE `meeting_permission` DISABLE KEYS */;
INSERT INTO `meeting_permission` (`permId`, `permLvl`, `permName`, `permDesc`, `regDateTime`, `modDateTime`, `expDateTime`, `delYN`) VALUES
	(1, 1, 'MHA001', NULL, '2022-06-19 22:30:00', '2022-06-19 22:30:00', NULL, 'N'),
	(2, 3, 'MHA002', NULL, '2022-06-19 22:30:00', '2022-06-19 22:30:00', NULL, 'N'),
	(3, 3, 'MHA003', NULL, '2022-06-19 22:30:00', '2022-06-19 22:30:00', NULL, 'N'),
	(4, 3, 'MHA004', NULL, '2022-06-19 22:30:00', '2022-06-19 22:30:00', NULL, 'N'),
	(5, 3, 'MHA005', NULL, '2022-06-19 22:30:00', '2022-06-19 22:30:00', NULL, 'N'),
	(6, 11, 'MHA006', NULL, '2022-06-19 22:30:00', '2022-06-19 22:30:00', NULL, 'N');
/*!40000 ALTER TABLE `meeting_permission` ENABLE KEYS */;

-- 테이블 데이터 b_plms.org_dept_info:~12 rows (대략적) 내보내기
DELETE FROM `org_dept_info`;
/*!40000 ALTER TABLE `org_dept_info` DISABLE KEYS */;
INSERT INTO `org_dept_info` (`deptId`, `officeCode`, `deptCode`, `parentId`, `parentCode`, `deptName`, `deptManager`, `delYN`) VALUES
	(1, NULL, 'HEAD', 0, NULL, '라임에스엔씨', NULL, 'N'),
	(2, NULL, 'DEV1', 1, NULL, '개발1팀', NULL, 'N'),
	(3, NULL, 'DEV2', 1, NULL, '개발2팀', NULL, 'N'),
	(4, NULL, 'DEV2PART1', 3, NULL, '개발파트', NULL, 'N'),
	(5, NULL, 'DEV2PART2', 3, NULL, '기술파트', NULL, 'N'),
	(6, NULL, 'DEV2PART2SUB1', 5, NULL, '기술보조파트', NULL, 'Y'),
	(9, NULL, NULL, 1, NULL, '개발지원팀', NULL, 'N'),
	(10, NULL, NULL, 1, NULL, '재경팀', NULL, 'N'),
	(11, NULL, NULL, 1, NULL, '대표이사', NULL, 'N'),
	(12, NULL, NULL, 1, NULL, '영업본부', NULL, 'N'),
	(13, NULL, NULL, 12, NULL, '솔루션사업팀', NULL, 'N'),
	(14, NULL, NULL, 12, NULL, '발전사업팀', NULL, 'N');
/*!40000 ALTER TABLE `org_dept_info` ENABLE KEYS */;