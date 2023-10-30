package egov.framework.plms.sub.ewp.bean.mvc.entity.room;

import java.time.LocalTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;

import egov.framework.plms.main.bean.mvc.entity.room.abst.RoomInfoModelDTO;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Slf4j
public class EwpRoomInfoDTO extends RoomInfoModelDTO{
	private Integer roomKey; // seq_~room
	private RoomType roomType;
	private String officeCode; // SAUPSO_ID
	private String officeName;
	private String roomCode; // S_CODE
	private String roomName; // NAME
	private String roomLabel;
	private String roomNote; // DESCRIPTION
	private Integer roomSize;
	private Integer roomFloor;
	private Character rentYN; // RENT_YN
	private String rentReason; // RENT_REASON
	private String regUser; // REG_USER
	private String regDateTime; // REG_DATE
	private String modUser; // MODIFY_USER
	private String modDateTime; // MODIFY_DATE
	private Character syncYN; //경영지원서비스와 동기화 여부
	private Character delYN; // DEL_YN
	private LocalTime openTime;
	private LocalTime closeTime;
	
	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public EwpRoomInfoDTO(EwpRoomInfoVO vo) {
		if(vo == null) {
			return;
		}
		this.roomKey = vo.getRoomKey();
		this.officeCode = vo.getOfficeCode();
		this.officeName = vo.getOfficeName();
		this.roomName = vo.getRoomName();
		this.roomLabel = vo.getRoomLabel();
		this.roomNote = vo.getRoomNote();
		this.roomSize = vo.getRoomSize();
		this.roomFloor = vo.getRoomFloor();
		this.rentYN = vo.getRentYN();
		this.rentReason = vo.getRentReason();
		this.regUser = vo.getRegUser();
		this.regDateTime = vo.getRegDateTime();
		this.modUser = vo.getModUser();
		this.modDateTime = vo.getModDateTime();
		this.syncYN = vo.getSyncYN();
		this.delYN = vo.getDelYN();
		this.roomType = vo.getRoomType();
		this.roomCode = vo.getRoomCode();
		this.openTime = vo.getOpenTime();
		this.closeTime = vo.getCloseTime();
	}
	
	public boolean isEnable() {
		return Optional.ofNullable(rentYN).map(yn -> rentYN == 'Y').orElse(false);
	}

	@Override
	public EwpRoomInfoVO convert() {
		return EwpRoomInfoVO.initVO().dto(this).build();
	}

	@Override
	public Integer getRoomId() {
		return this.roomKey;
	}

	@Override
	public void setRoomId(Integer value) {
		this.roomKey = value;
	}

	@Override
	public void setDisableYN(Character value) {
		// TODO Auto-generated method stub
		if(value == null) {
			this.rentYN = null;
		}else {
			this.rentYN = (value == 'Y')?'N':'Y';
		}
	}

	@Override
	public void setDisableComment(String value) {
		// TODO Auto-generated method stub
		this.rentReason = value;
	}

	@Override
	public void setEnable(boolean value) {
		// TODO Auto-generated method stub
		if(value) {
			this.rentYN = 'Y';
		}else {
			this.rentYN = 'N';
		}
	}

	@Override
	public Character getDisableYN() {
		// TODO Auto-generated method stub
		if(this.rentYN == null) {
			return null;
		}
		return (this.rentYN == 'Y')?'N':'Y';
	}

	@Override
	public String getDisableComment() {
		// TODO Auto-generated method stub
		return this.rentReason;
	}
	/**
	 * 다음 값들을 필터링합니다<br>
	 * {@link #officeName}
	 * <br>{@link #rentYN}, {@link #rentReason}, 
	 * {@link #regUser}, {@link #regDateTime}, 
	 * {@link #modUser}, {@link #modDateTime}, 
	 * {@link #syncYN}, {@link #roomCode}
	 * <br>{@link #roomNote}
	 */
	@Override
	public EwpRoomInfoDTO filterForEssential() {
		EwpRoomInfoDTO filteredDTO = (EwpRoomInfoDTO) super.filterForEssential();
		filteredDTO.setOfficeName(null);
		return filteredDTO;
	}
	
	/**
	 * 다음 값들을 필터링합니다<br>
	 * {@link #rentYN}, {@link #rentReason}, 
	 * {@link #regUser}, {@link #regDateTime}, 
	 * {@link #modUser}, {@link #modDateTime}, 
	 * {@link #syncYN}, {@link #roomCode}
	 * <br>{@link #roomNote}
	 */
	@Override
	public EwpRoomInfoDTO filterForBasic() {
		EwpRoomInfoDTO filteredDTO = (EwpRoomInfoDTO) super.filterForBasic();
		filteredDTO.setRegUser(null);
		filteredDTO.setRegDateTime(null);
		filteredDTO.setModUser(null);
		filteredDTO.setModDateTime(null);
		filteredDTO.setSyncYN(null);
		filteredDTO.setRoomCode(null);
		return filteredDTO;
	}

	/**
	 * 다음 값들을 필터링합니다<br>
	 * {@link #roomNote}
	 */
	@Override
	public EwpRoomInfoDTO filterForDetailed() {
		EwpRoomInfoDTO filteredDTO = (EwpRoomInfoDTO) super.filterForDetailed();
		return filteredDTO;
	}

	@Override
	public EwpRoomInfoDTO filterForSensitive() {
		EwpRoomInfoDTO filteredDTO = (EwpRoomInfoDTO) super.filterForSensitive();
		return filteredDTO;
	}

	@Override
	public EwpRoomInfoDTO filterForHighest() {
		EwpRoomInfoDTO filteredDTO = (EwpRoomInfoDTO) super.filterForHighest();
		return filteredDTO;
	}
}
