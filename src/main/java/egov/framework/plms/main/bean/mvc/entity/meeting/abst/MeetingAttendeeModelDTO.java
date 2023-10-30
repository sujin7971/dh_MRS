package egov.framework.plms.main.bean.mvc.entity.meeting.abst;

import java.time.LocalDate;
import java.time.LocalDateTime;

import egov.framework.plms.main.core.model.able.Convertable;
import egov.framework.plms.main.core.model.able.Filterable;
import egov.framework.plms.main.core.model.enums.meeting.AttendRole;

public abstract class MeetingAttendeeModelDTO extends MeetingAttendeeEntity implements Convertable<MeetingAttendeeModelVO>, Filterable<MeetingAttendeeModelDTO> {
	public abstract void setAttendId(Integer value);
	public abstract void setMeetingId(Integer value);
	public abstract void setUserId(String value);
	public abstract void setAttendRole(AttendRole value);
	public abstract void setAssistantYN(Character value);
	public abstract void setAttendYN(Character value);
	public abstract void setAttendDateTime(LocalDateTime value);
	public abstract void setExitYN(Character value);
	public abstract void setExitDateTime(LocalDateTime value);
	public abstract void setSignYN(Character value);
	public abstract void setSignSrc(String value);
	public abstract void setTempPassword(String value);
	public abstract void setPasswordExpiryDate(LocalDate value);
	public abstract void setDelYN(Character value);
	
	/**
	 * 다음 값들을 필터링합니다<br>
	 * {@link #attendRole}, {@link #assistantYN}
	 * <br>{@link #attendYN}, {@link #attendDateTime}, {@link #exitYN}, {@link #exitDateTime}
	 * <br>{@link #signYN}, {@link #delYN}
	 */
	@Override
	public MeetingAttendeeModelDTO filterForEssential() {
		MeetingAttendeeModelDTO filteredDTO = this.filterForBasic();
		filteredDTO.setAttendRole(null);
		filteredDTO.setAssistantYN(null);
		return this;
	}
	
	/**
	 * 다음 값들을 필터링합니다<br>
	 * {@link #attendYN}, {@link #attendDateTime}, {@link #exitYN}, {@link #exitDateTime}
	 * <br>{@link #signYN}, {@link #delYN}
	 */
	@Override
	public MeetingAttendeeModelDTO filterForBasic() {
		MeetingAttendeeModelDTO filteredDTO = this.filterForDetailed();
		filteredDTO.setSignYN(null);
		filteredDTO.setAttendYN(null);
		filteredDTO.setAttendDateTime(null);
		filteredDTO.setExitYN(null);
		filteredDTO.setExitDateTime(null);
		return filteredDTO;
	}

	/**
	 * 다음 값들을 필터링합니다<br>
	 * {@link #signSrc}, {@link #delYN}
	 */
	@Override
	public MeetingAttendeeModelDTO filterForDetailed() {
		MeetingAttendeeModelDTO filteredDTO = this.filterForSensitive();
		filteredDTO.setSignSrc(null);
		filteredDTO.setDelYN(null);
		return filteredDTO;
	}

	@Override
	public MeetingAttendeeModelDTO filterForSensitive() {
		MeetingAttendeeModelDTO filteredDTO = this.filterForHighest();
		return filteredDTO;
	}

	@Override
	public MeetingAttendeeModelDTO filterForHighest() {
		MeetingAttendeeModelDTO filteredDTO = this;
		return filteredDTO;
	}
}
