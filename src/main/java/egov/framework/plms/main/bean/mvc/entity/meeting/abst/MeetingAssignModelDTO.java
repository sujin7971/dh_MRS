package egov.framework.plms.main.bean.mvc.entity.meeting.abst;

import java.time.LocalDate;
import java.time.LocalDateTime;

import egov.framework.plms.main.core.model.able.Convertable;
import egov.framework.plms.main.core.model.able.Filterable;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import egov.framework.plms.main.core.model.enums.meeting.MeetingStatus;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.model.enums.meeting.ScheduleType;

public abstract class MeetingAssignModelDTO extends MeetingAssignEntity implements Convertable<MeetingAssignModelVO>, Filterable<MeetingAssignModelDTO>{
	public abstract void setScheduleId(Integer value);
	public abstract void setMeetingId(Integer value);
	public abstract void setExternalReservationId(String value);
	public abstract void setWriterId(String value);
	public abstract void setOfficeCode(String value);
	public abstract void setDeptId(String value);
	public abstract void setRoomId(Integer value);
	public abstract void setAttendeeCnt(Integer value);
	public abstract void setUserDefinedLocation(String value);
	public abstract void setScheduleHost(String value);
	public abstract void setScheduleType(ScheduleType value);
	public abstract void setApprovalStatus(ApprovalStatus value);
	public abstract void setRoomType(RoomType value);
	public abstract void setHoldingDate(LocalDate value);
	public abstract void setBeginDateTime(LocalDateTime value);
	public abstract void setFinishDateTime(LocalDateTime value);
	public abstract void setExpDateTime(LocalDateTime value);
	
	public abstract void setTitle(String value);
	public abstract void setContents(String value);
	public abstract void setMeetingStatus(MeetingStatus value);
	public abstract void setSecretYN(Character value);
	public abstract void setHostSecuLvl(Integer value);
	public abstract void setAttendeeSecuLvl(Integer value);
	public abstract void setObserverSecuLvl(Integer value);
	public abstract void setElecYN(Character value);
	public abstract void setMessengerYN(Character value);
	public abstract void setMailYN(Character value);
	public abstract void setSmsYN(Character value);
	public abstract void setDelYN(Character value);
	
	/**
	 * 다음 값들을 필터링합니다<br>
	 * {@link #meetingId}, {@link #externalReservationId}, {@link #writerId}, 
	 * {@link #officeCode}, {@link #deptId}, {@link #roomType}, {@link #roomId},
	 * <br>{@link #attendeeCnt}, {@link #elecYN}, {@link #secretYN}
	 * <br>{@link #contents}, {@link #expDateTime}
	 * {@link #hostSecuLvl}, {@link #attendeeSecuLvl}, {@link #observerSecuLvl}, 
	 * {@link #mailYN}, {@link #smsYN}, {@link #messengerYN}, {@link #delYN}
	 * <br>{@link #stickyBit}
	 */
	@Override
	public MeetingAssignModelDTO filterForEssential() {
		MeetingAssignModelDTO filteredDTO = filterForBasic();
		filteredDTO.setMeetingId(null);
		filteredDTO.setExternalReservationId(null);
		filteredDTO.setWriterId(null);;
		filteredDTO.setOfficeCode(null);
		filteredDTO.setDeptId(null);
		filteredDTO.setRoomType(null);
		filteredDTO.setRoomId(null);
		return filteredDTO;
	}

	/**
	 * 다음 값들을 필터링합니다<br>
	 * {@link #attendeeCnt}, {@link #elecYN}, {@link #secretYN}
	 * <br>{@link #contents}, {@link #expDateTime}
	 * {@link #hostSecuLvl}, {@link #attendeeSecuLvl}, {@link #observerSecuLvl}, 
	 * {@link #mailYN}, {@link #smsYN}, {@link #messengerYN}, {@link #delYN}
	 * <br>{@link #stickyBit}
	 */
	@Override
	public MeetingAssignModelDTO filterForBasic() {
		MeetingAssignModelDTO filteredDTO = filterForDetailed();
		filteredDTO.setAttendeeCnt(null);
		filteredDTO.setElecYN(null);
		filteredDTO.setSecretYN(null);
		return filteredDTO;
	}

	/**
	 * 다음 값들을 필터링합니다<br>
	 * {@link #contents}, {@link #expDateTime},
	 * {@link #hostSecuLvl}, {@link #attendeeSecuLvl}, {@link #observerSecuLvl}, 
	 * {@link #mailYN}, {@link #smsYN}, {@link #messengerYN}, {@link #delYN}
	 */
	@Override
	public MeetingAssignModelDTO filterForDetailed() {
		MeetingAssignModelDTO filteredDTO = filterForSensitive();
		filteredDTO.setContents(null);
		filteredDTO.setExpDateTime(null);
		filteredDTO.setHostSecuLvl(null);
		filteredDTO.setAttendeeSecuLvl(null);
		filteredDTO.setObserverSecuLvl(null);
		filteredDTO.setMessengerYN(null);
		filteredDTO.setMailYN(null);
		filteredDTO.setSmsYN(null);
		filteredDTO.setDelYN(null);
		return filteredDTO;
	}
	/**
	 * 다음 값들을 필터링합니다<br>
	 * 
	 */
	@Override
	public MeetingAssignModelDTO filterForSensitive() {
		MeetingAssignModelDTO filteredDTO = filterForHighest();
		return filteredDTO;
	}
	/**
	 * 다음 값들을 필터링합니다<br>
	 * 
	 */
	@Override
	public MeetingAssignModelDTO filterForHighest() {
		MeetingAssignModelDTO filteredDTO = this;
		return filteredDTO;
	}
}
