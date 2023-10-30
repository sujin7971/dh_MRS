package egov.framework.plms.sub.ewp.bean.mvc.entity.assign;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Id;

import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoModelVO;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.model.enums.meeting.ScheduleType;
import egov.framework.plms.main.core.util.DateTimeUtil;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAssignVO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class EwpRoomReqVO {
	@Id 
	@Column(name = "SEQ_REQ", nullable = false)
	private Integer seqReq;// 승인 아이디
	private Integer seqRoom;
	private RoomType roomType;
	private ScheduleType scheduleType;
	@Column(name = "USE_DATE")
	private String useDate;// 사용일
	@Column(name = "S_TIME", length = 4)
	private String sTime;// 사용 시작 시간
	@Column(name = "E_TIME", length = 4)
	private String eTime;// 사용 종료 시간
	@Column(name = "PURPOSE")
	private String purPose;// 사용 목적
	@Column(name = "ETC")
	private String etc;// 기타사항
	@Column(name = "MAN_CNT")
	private Integer manCnt;// 사용 인원
	@Column(name = "HOST")
	private String host;// 주관자
	@Column(name = "SAUPSO_ID")
	private String saupsoCd;// 사업소 아이디
	@Column(name = "ORG_ID")
	private String orgId;// 작성자 조직 아이디(사원 뷰테이블 deptno)
	@Column(name = "REG_USER")
	private String regUser;// 작성자(사원 뷰테이블 empno)
	@Column(name = "TEL_NO")
	private String telNo;// 작성자 전화번호 (사원 뷰테이블 telno)
	@Column(name = "REG_DATE")
	private String regDate;// 작성일
	@Column(name = "MODIFY_USER")
	private String modifyUser;// 수정자
	@Column(name = "MODIFY_DATE")
	private String modifyDate;// 수정일
	@Column(name = "APP_STATUS")
	private Character appStatus;// 승인 상태(1: 신청, 2: 자동 승인, 3: 승인 취소, 4: 승인 불가)
	@Column(name = "APP_REASON")
	private String appReason;// 승인 특이사항(1: 신청, 2: 자동 승인, 3: 승인 취소, 4: 승인 불가)
	@Column(name = "APP_USER")
	private String appUser;// 승인 담당자
	@Column(name = "APP_DATE")
	private String appDate;// 승인 처리일
	@Column(name = "DEL_YN")
	private Character delYN;// 삭제여부
	
	public static Character getAppStatus(ApprovalStatus approvalStatus) {
		if(approvalStatus == null) {
			return null;
		}
		Character status = '0';
		switch(approvalStatus) {
			case REQUEST:
				status = '1';
				break;
			case APPROVED:
				status = '2';
				break;
			case CANCELED:
				status = '3';
				break;
			case REJECTED:
				status = '4';
				break;
			default:
				status = null;
		}
		return status;
	}
	
	public LocalDate getHoldingDate() {
		if(this.useDate == null) {
			return null;
		}
		return LocalDate.parse(this.useDate, DateTimeUtil.format("yyyy-MM-dd"));
	}
	
	public LocalDateTime getBeginDateTime() {
		LocalDate dueDate = getHoldingDate();
		if(dueDate == null || sTime == null) {
			return null;
		}
		return LocalDateTime.of(dueDate, LocalTime.parse(sTime, DateTimeUtil.format("HHmm")));
	}
	
	public LocalDateTime getFinishDateTime() {
		LocalDate dueDate = getHoldingDate();
		if(dueDate == null || eTime == null) {
			return null;
		}
		return LocalDateTime.of(dueDate, LocalTime.parse(eTime, DateTimeUtil.format("HHmm")));
	}
	
	public ApprovalStatus getSkdStatus() {
		ApprovalStatus status = null;
		switch(appStatus) {
		case '1':
			status = ApprovalStatus.REQUEST;
			break;
		case '2':
			status = ApprovalStatus.APPROVED;
			break;
		case '3':
			status = ApprovalStatus.CANCELED;
			break;
		case '4':
			status = ApprovalStatus.REJECTED;
			break;
		}
		return status;
	}
	
	public static EwpRoomReqVO generate(EwpMeetingAssignVO assignVO) {
		return EwpRoomReqVO.builder()
				.seqReq(Optional.ofNullable(assignVO.getExternalReservationId()).map(id -> Integer.parseInt(id)).orElse(null))
				.seqRoom(assignVO.getRoomId())
				.roomType(assignVO.getRoomType())
				.useDate(Optional.ofNullable(assignVO.getHoldingDate()).map(LocalDate::toString).orElse(null))
				.sTime(Optional.ofNullable(assignVO.getBeginDateTime()).map(dt -> dt.format(DateTimeUtil.format("HHmm"))).orElse(null))
				.eTime(Optional.ofNullable(assignVO.getFinishDateTime()).map(dt -> dt.format(DateTimeUtil.format("HHmm"))).orElse(null))
				.purPose(assignVO.getTitle())
				.etc(assignVO.getContents())
				.manCnt(assignVO.getAttendeeCnt())
				.host(assignVO.getScheduleHost())
				.saupsoCd(assignVO.getOfficeCode())
				.orgId(assignVO.getDeptId())
				.regUser(assignVO.getWriterId())
				.telNo(Optional.ofNullable(assignVO.getWriter()).map(UserInfoModelVO::getOfficeDeskPhone).orElse(null))
				.appStatus(Optional.ofNullable(assignVO.getApprovalStatus()).map(EwpRoomReqVO::getAppStatus).orElse(null))
				.appReason(assignVO.getAppComment())
				.delYN(assignVO.getDelYN())
				.build();
	}
	
	public EwpMeetingAssignVO convert() {
		return EwpMeetingAssignVO.builder()
				.reqKey(this.seqReq)
				.roomType(this.roomType)
				.roomKey(this.seqRoom)
				.holdingDate(getHoldingDate())
				.beginDateTime(getBeginDateTime())
				.finishDateTime(getFinishDateTime())
				.title(this.purPose)
				.contents(this.etc)
				.attendeeCnt(this.manCnt)
				.skdHost(this.host)
				.officeCode(this.saupsoCd)
				.deptId(this.orgId)
				.writerKey(this.regUser)
				.appStatus(getSkdStatus())
				.appComment(this.appReason)
				.meetingStatus(getSkdStatus().getMeetingStatus())
				.delYN(this.delYN)
				.build();
	}
}
