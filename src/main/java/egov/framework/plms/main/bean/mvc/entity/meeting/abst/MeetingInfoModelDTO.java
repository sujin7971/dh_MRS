package egov.framework.plms.main.bean.mvc.entity.meeting.abst;

import egov.framework.plms.main.core.model.able.Convertable;
import egov.framework.plms.main.core.model.enums.meeting.MeetingStatus;

public abstract class MeetingInfoModelDTO extends MeetingInfoEntity implements Convertable<MeetingInfoModelVO> {
	public abstract void setMeetingId(Integer value);
	public abstract void setWriterId(String value);
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
}
