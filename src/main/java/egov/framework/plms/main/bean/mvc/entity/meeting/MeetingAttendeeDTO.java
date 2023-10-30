package egov.framework.plms.main.bean.mvc.entity.meeting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingAttendeeModelDTO;
import egov.framework.plms.main.bean.mvc.entity.organization.UserInfoDTO;
import egov.framework.plms.main.bean.mvc.entity.organization.UserInfoVO;
import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoModelDTO;
import egov.framework.plms.main.core.model.enums.meeting.AttendRole;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL) 
public class MeetingAttendeeDTO extends MeetingAttendeeModelDTO {
	private Integer attendId;
	private Integer meetingId;
	private String userId;
	private UserInfoDTO user;
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
	private Character delYN;

	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public MeetingAttendeeDTO(MeetingAttendeeVO vo) {
		this.attendId = vo.getAttendId();
		this.meetingId = vo.getMeetingId();
		this.userId = vo.getUserId();
		this.userName = vo.getUserName();
		this.deptId = vo.getDeptId();
		this.user = Optional.ofNullable(vo.getUser()).map(UserInfoVO::convert).orElseGet(()->null);
		this.attendRole = vo.getAttendRole();
		this.assistantYN = vo.getAssistantYN();
		this.attendYN = vo.getAttendYN();
		this.attendDateTime = vo.getAttendDateTime();
		this.exitYN = vo.getExitYN();
		this.exitDateTime = vo.getExitDateTime();
		this.signYN = vo.getSignYN();
		this.signSrc = vo.getSignSrc();
		this.delYN = vo.getDelYN();
	}
	
	@Override
	public MeetingAttendeeVO convert() {
		return MeetingAttendeeVO.initVO().dto(this).build();
	}

	@Override
	public String getOfficeCode() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelDTO::getOfficeCode).orElse(null);
	}

	@Override
	public String getOfficeName() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelDTO::getOfficeName).orElse(null);
	}

	@Override
	public String getDeptCode() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelDTO::getDeptCode).orElse(null);
	}

	@Override
	public String getDeptName() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelDTO::getDeptName).orElse(null);
	}

	@Override
	public String getTitleName() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelDTO::getTitleName).orElse(null);
	}

	@Override
	public String getTitleCode() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelDTO::getTitleCode).orElse(null);
	}

	@Override
	public String getPositionName() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelDTO::getPositionName).orElse(null);
	}

	@Override
	public String getPositionCode() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelDTO::getPositionCode).orElse(null);
	}

	@Override
	public String getRankName() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelDTO::getRankName).orElse(null);
	}

	@Override
	public String getRankCode() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelDTO::getRankCode).orElse(null);
	}

	@Override
	public String getEmail() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelDTO::getEmail).orElse(null);
	}

	@Override
	public String getPersonalCellPhone() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelDTO::getPersonalCellPhone).orElse(null);
	}

	@Override
	public String getOfficeDeskPhone() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelDTO::getOfficeDeskPhone).orElse(null);
	}

	@Override
	public String getNameplate() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(UserInfoModelDTO::getNameplate).orElse(null);
	}

	@Override
	public void setTempPassword(String value) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setPasswordExpiryDate(LocalDate value) {
		// TODO Auto-generated method stub
	}

	@Override
	public MeetingAttendeeDTO filterForEssential() {
		// TODO Auto-generated method stub
		return (MeetingAttendeeDTO) super.filterForEssential();
	}

	@Override
	public MeetingAttendeeDTO filterForBasic() {
		// TODO Auto-generated method stub
		return (MeetingAttendeeDTO) super.filterForBasic();
	}

	@Override
	public MeetingAttendeeDTO filterForDetailed() {
		// TODO Auto-generated method stub
		return (MeetingAttendeeDTO) super.filterForDetailed();
	}

	@Override
	public MeetingAttendeeDTO filterForSensitive() {
		// TODO Auto-generated method stub
		return (MeetingAttendeeDTO) super.filterForSensitive();
	}

	@Override
	public MeetingAttendeeDTO filterForHighest() {
		// TODO Auto-generated method stub
		return (MeetingAttendeeDTO) super.filterForHighest();
	}
	
	
}
