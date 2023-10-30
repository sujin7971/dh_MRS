package egov.framework.plms.sub.ewp.bean.mvc.entity.meeting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingAttendeeModelVO;
import egov.framework.plms.main.core.model.enums.meeting.AttendRole;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpUserInfoDTO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpUserInfoVO;
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
public class EwpMeetingAttendeeVO extends MeetingAttendeeModelVO {
	private Integer attendKey;
	private Integer meetingKey;
	private String userKey;
	private EwpUserInfoVO user;
	private String userName;
	private String deptId;
	private AttendRole attendRole;
	private Character assistantYN;
	private Character attendYN;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime attendDateTime;
	private Character exitYN;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime exitDateTime;
	private Character signYN;
	private String signSrc;
	private String tempPW;
	private LocalDate expireDate;
	private Character delYN;
	
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public EwpMeetingAttendeeVO(EwpMeetingAttendeeDTO dto) {
		this.attendKey = dto.getAttendKey();
		this.meetingKey = dto.getMeetingKey();
		this.userKey = dto.getUserKey();
		this.userName = dto.getUserName();
		this.deptId = dto.getDeptId();
		this.user = Optional.ofNullable(dto.getUser()).map(EwpUserInfoDTO::convert).orElseGet(()->null);
		this.attendRole = dto.getAttendRole();
		this.assistantYN = dto.getAssistantYN();
		this.attendYN = dto.getAttendYN();
		this.attendDateTime = dto.getAttendDateTime();
		this.exitYN = dto.getExitYN();
		this.exitDateTime = dto.getExitDateTime();
		this.signYN = dto.getSignYN();
		this.signSrc = dto.getSignSrc();
		this.delYN = dto.getDelYN();
	}

	@Override
	public EwpMeetingAttendeeDTO convert() {
		return EwpMeetingAttendeeDTO.initDTO().vo(this).build();
	}

	@Override
	public String getOfficeCode() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoVO::getOfficeCode).orElse(null);
	}

	@Override
	public String getOfficeName() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoVO::getOfficeName).orElse(null);
	}

	@Override
	public String getDeptCode() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoVO::getDeptCode).orElse(null);
	}

	@Override
	public String getDeptName() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoVO::getDeptName).orElse(null);
	}

	@Override
	public String getUserId() {
		// TODO Auto-generated method stub
		return this.userKey;
	}

	@Override
	public String getTitleName() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoVO::getTitleName).orElse(null);
	}

	@Override
	public String getTitleCode() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoVO::getTitleCode).orElse(null);
	}

	@Override
	public String getPositionName() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoVO::getPositionName).orElse(null);
	}

	@Override
	public String getPositionCode() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoVO::getPositionCode).orElse(null);
	}

	@Override
	public String getRankName() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoVO::getRankName).orElse(null);
	}

	@Override
	public String getRankCode() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoVO::getRankCode).orElse(null);
	}

	@Override
	public String getEmail() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoVO::getEmail).orElse(null);
	}

	@Override
	public String getPersonalCellPhone() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoVO::getPersonalCellPhone).orElse(null);
	}

	@Override
	public String getOfficeDeskPhone() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoVO::getOfficeDeskPhone).orElse(null);
	}

	@Override
	public String getNameplate() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoVO::getNameplate).orElse(null);
	}

	@Override
	public String getTempPassword() {
		// TODO Auto-generated method stub
		return this.tempPW;
	}

	@Override
	public LocalDate getPasswordExpiryDate() {
		// TODO Auto-generated method stub
		return this.expireDate;
	}

	@Override
	public Integer getAttendId() {
		// TODO Auto-generated method stub
		return this.attendKey;
	}

	@Override
	public Integer getMeetingId() {
		// TODO Auto-generated method stub
		return this.meetingKey;
	}
}
