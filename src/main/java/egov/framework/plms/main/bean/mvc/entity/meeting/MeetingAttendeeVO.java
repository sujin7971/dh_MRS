package egov.framework.plms.main.bean.mvc.entity.meeting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingAttendeeModelVO;
import egov.framework.plms.main.bean.mvc.entity.organization.UserInfoDTO;
import egov.framework.plms.main.bean.mvc.entity.organization.UserInfoVO;
import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoModelVO;
import egov.framework.plms.main.core.model.enums.meeting.AttendRole;
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
public class MeetingAttendeeVO extends MeetingAttendeeModelVO {
	private Integer attendId;
	private Integer meetingId;
	private String userId;
	private UserInfoVO user;
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
	private String tempPassword;
	private LocalDate passwordExpiryDate;
	private Character delYN;
	
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public MeetingAttendeeVO(MeetingAttendeeDTO dto) {
		this.attendId = dto.getAttendId();
		this.meetingId = dto.getMeetingId();
		this.userId = dto.getUserId();
		this.userName = dto.getUserName();
		this.deptId = dto.getDeptId();
		this.user = Optional.ofNullable(dto.getUser()).map(UserInfoDTO::convert).orElseGet(()->null);
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
	public MeetingAttendeeDTO convert() {
		return MeetingAttendeeDTO.initDTO().vo(this).build();
	}

	@Override
	public String getOfficeCode() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelVO::getOfficeCode).orElse(null);
	}

	@Override
	public String getOfficeName() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelVO::getOfficeName).orElse(null);
	}

	@Override
	public String getDeptCode() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelVO::getDeptCode).orElse(null);
	}

	@Override
	public String getDeptName() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelVO::getDeptName).orElse(null);
	}

	@Override
	public String getTitleName() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelVO::getTitleName).orElse(null);
	}

	@Override
	public String getTitleCode() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelVO::getTitleCode).orElse(null);
	}

	@Override
	public String getPositionName() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelVO::getPositionName).orElse(null);
	}

	@Override
	public String getPositionCode() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelVO::getPositionCode).orElse(null);
	}

	@Override
	public String getRankName() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelVO::getRankName).orElse(null);
	}

	@Override
	public String getRankCode() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelVO::getRankCode).orElse(null);
	}

	@Override
	public String getEmail() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelVO::getEmail).orElse(null);
	}

	@Override
	public String getPersonalCellPhone() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelVO::getPersonalCellPhone).orElse(null);
	}

	@Override
	public String getOfficeDeskPhone() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelVO::getOfficeDeskPhone).orElse(null);
	}

	@Override
	public String getNameplate() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelVO::getNameplate).orElse(null);
	}
}
