package egov.framework.plms.sub.ewp.bean.mvc.entity.meeting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingAttendeeModelDTO;
import egov.framework.plms.main.core.model.enums.meeting.AttendRole;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpUserInfoDTO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpUserInfoVO;
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
public class EwpMeetingAttendeeDTO extends MeetingAttendeeModelDTO {
	private Integer attendKey;
	private Integer meetingKey;
	private String userKey;
	private EwpUserInfoDTO user;
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
	public EwpMeetingAttendeeDTO(EwpMeetingAttendeeVO vo) {
		this.attendKey = vo.getAttendKey();
		this.meetingKey = vo.getMeetingKey();
		this.userKey = vo.getUserKey();
		this.userName = vo.getUserName();
		this.deptId = vo.getDeptId();
		this.user = Optional.ofNullable(vo.getUser()).map(EwpUserInfoVO::convert).orElseGet(()->null);
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
	public EwpMeetingAttendeeVO convert() {
		return EwpMeetingAttendeeVO.initVO().dto(this).build();
	}

	@Override
	public String getOfficeCode() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoDTO::getOfficeCode).orElse(null);
	}

	@Override
	public String getOfficeName() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoDTO::getOfficeName).orElse(null);
	}

	@Override
	public String getDeptCode() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoDTO::getDeptCode).orElse(null);
	}

	@Override
	public String getDeptName() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoDTO::getDeptName).orElse(null);
	}

	@Override
	public String getUserId() {
		// TODO Auto-generated method stub
		return this.userKey;
	}

	@Override
	public String getTitleName() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoDTO::getTitleName).orElse(null);
	}

	@Override
	public String getTitleCode() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoDTO::getTitleCode).orElse(null);
	}

	@Override
	public String getPositionName() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoDTO::getPositionName).orElse(null);
	}

	@Override
	public String getPositionCode() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoDTO::getPositionCode).orElse(null);
	}

	@Override
	public String getRankName() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoDTO::getRankName).orElse(null);
	}

	@Override
	public String getRankCode() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoDTO::getRankCode).orElse(null);
	}

	@Override
	public String getEmail() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoDTO::getEmail).orElse(null);
	}

	@Override
	public String getPersonalCellPhone() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoDTO::getPersonalCellPhone).orElse(null);
	}

	@Override
	public String getOfficeDeskPhone() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoDTO::getOfficeDeskPhone).orElse(null);
	}

	@Override
	public String getNameplate() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.user).map(EwpUserInfoDTO::getNameplate).orElse(null);
	}

	@Override
	public void setAttendId(Integer value) {
		this.attendKey = value;
	}

	@Override
	public void setMeetingId(Integer value) {
		// TODO Auto-generated method stub
		this.meetingKey = value;
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
	public Integer getAttendId() {
		// TODO Auto-generated method stub
		return this.attendKey;
	}

	@Override
	public Integer getMeetingId() {
		// TODO Auto-generated method stub
		return this.meetingKey;
	}

	@Override
	public void setUserId(String value) {
		// TODO Auto-generated method stub
		this.userKey = value;
	}

	/**
	 * 다음 값들을 필터링합니다<br>
	 * {@link #attendRole}, {@link #assistantYN}
	 * <br>{@link #attendYN}, {@link #attendDateTime}, {@link #exitYN}, {@link #exitDateTime}
	 * <br>{@link #signYN}, {@link #delYN}
	 */
	@Override
	public EwpMeetingAttendeeDTO filterForEssential() {
		EwpMeetingAttendeeDTO filteredDTO = (EwpMeetingAttendeeDTO) super.filterForEssential();
		if(filteredDTO.getUser() != null) {
			filteredDTO.setUser(filteredDTO.getUser().filterForEssential());
		}
		return this;
	}
	
	/**
	 * 다음 값들을 필터링합니다<br>
	 * {@link #attendYN}, {@link #attendDateTime}, {@link #exitYN}, {@link #exitDateTime}
	 * <br>{@link #signYN}, {@link #delYN}
	 */
	@Override
	public EwpMeetingAttendeeDTO filterForBasic() {
		EwpMeetingAttendeeDTO filteredDTO = (EwpMeetingAttendeeDTO) super.filterForBasic();
		if(filteredDTO.getUser() != null) {
			filteredDTO.setUser(filteredDTO.getUser().filterForBasic());
		}
		return filteredDTO;
	}

	/**
	 * 다음 값들을 필터링합니다<br>
	 * {@link #signSrc}, {@link #delYN}
	 */
	@Override
	public EwpMeetingAttendeeDTO filterForDetailed() {
		EwpMeetingAttendeeDTO filteredDTO = (EwpMeetingAttendeeDTO) super.filterForDetailed();
		if(filteredDTO.getUser() != null) {
			filteredDTO.setUser(filteredDTO.getUser().filterForDetailed());
		}
		return filteredDTO;
	}

	@Override
	public EwpMeetingAttendeeDTO filterForSensitive() {
		EwpMeetingAttendeeDTO filteredDTO = (EwpMeetingAttendeeDTO) super.filterForSensitive();
		if(filteredDTO.getUser() != null) {
			filteredDTO.setUser(filteredDTO.getUser().filterForDetailed());
		}
		return filteredDTO;
	}

	@Override
	public EwpMeetingAttendeeDTO filterForHighest() {
		EwpMeetingAttendeeDTO filteredDTO = (EwpMeetingAttendeeDTO) super.filterForHighest();
		if(filteredDTO.getUser() != null) {
			filteredDTO.setUser(filteredDTO.getUser().filterForDetailed());
		}
		return filteredDTO;
	}
}
