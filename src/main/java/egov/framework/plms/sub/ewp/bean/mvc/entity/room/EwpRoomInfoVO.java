package egov.framework.plms.sub.ewp.bean.mvc.entity.room;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import egov.framework.plms.main.bean.mvc.entity.room.abst.RoomInfoModelVO;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.model.response.ErrorMessage;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import egov.framework.plms.main.core.util.CommUtil;
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
public class EwpRoomInfoVO extends RoomInfoModelVO {
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
	
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public EwpRoomInfoVO(EwpRoomInfoDTO dto) {
		if(dto == null) {
			return;
		}
		this.roomKey = dto.getRoomKey();
		this.officeCode = dto.getOfficeCode();
		this.officeName = dto.getOfficeName();
		this.roomName = dto.getRoomName();
		this.roomLabel = dto.getRoomLabel();
		this.roomNote = dto.getRoomNote();
		this.roomSize = dto.getRoomSize();
		this.roomFloor = dto.getRoomFloor();
		this.rentYN = dto.getRentYN();
		this.rentReason = dto.getRentReason();
		this.regUser = dto.getRegUser();
		this.regDateTime = dto.getRegDateTime();
		this.modUser = dto.getModUser();
		this.modDateTime = dto.getModDateTime();
		this.syncYN = dto.getSyncYN();
		this.delYN = dto.getDelYN();
		this.roomType = dto.getRoomType();
		this.roomCode = dto.getRoomCode();
		this.openTime = dto.getOpenTime();
		this.closeTime = dto.getCloseTime();
	}
	
	public static ResponseMessage validation(EwpRoomInfoDTO room) {
		List<ErrorMessage> error = new ArrayList<>();
		
		if(CommUtil.isEmpty(room.getOfficeCode())) {
			error.add(ErrorMessage.builder(
					ErrorMessage.ErrorCode.EMPTY_VALUE)
					.message("사업소를 선택해주세요.")
					.build());
		}
		
		if(room.getRoomType() == null) {
			error.add(ErrorMessage.builder(
					ErrorMessage.ErrorCode.EMPTY_VALUE)
					.message("회의실 종류를 선택해주세요.")
					.build());
		}
		
		if(CommUtil.isEmpty(room.getRoomName())) {
			error.add(ErrorMessage.builder(
					ErrorMessage.ErrorCode.EMPTY_VALUE)
					.message(ErrorMessage.MessageCode.ROOM.EMPTY_NAME.value())
					.build());
		} else if(!CommUtil.isValidLength(room.getRoomName(), 25)) {
				error.add(ErrorMessage.builder(
						ErrorMessage.ErrorCode.INVALID_VALUE)
						.message(ErrorMessage.MessageCode.ROOM.INVALID_NAME.value())
						.build());
			}
		
		if(CommUtil.isEmpty(room.getRentYN())) {
			error.add(ErrorMessage.builder(
					ErrorMessage.ErrorCode.EMPTY_VALUE)
					.message("대여가능 여부를 선택해주세요.")
					.build());
		}
		
		if(!CommUtil.isEmpty(room.getRentReason())) {
			if(!CommUtil.isValidLength(room.getRentReason(), 500)) {
				error.add(ErrorMessage.builder(
						ErrorMessage.ErrorCode.INVALID_VALUE)
						.message("입력한 장소의 대여불가사유의 길이가 너무 큽니다. 500자 이내로 입력해 주세요.")
						.build());
			}
		}
		
		if(!CommUtil.isEmpty(room.getRoomNote())) {
			if(!CommUtil.isValidLength(room.getRoomNote(), 150)) {
				error.add(ErrorMessage.builder(
						ErrorMessage.ErrorCode.INVALID_VALUE)
						.message("입력한 장소의 기타정보 길이가 너무 큽니다. 150자 이내로 입력해 주세요.")
						.build());
			}
		}
		
		if(error.size() == 0) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK).build();
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.error(error)
					.build();
		}
	}

	@Override
	public EwpRoomInfoDTO convert() {
		return EwpRoomInfoDTO.initDTO().vo(this).build();
	}
	
	public void setOfficeName(String name) {
		this.officeName = name;
	}

	@Override
	public boolean isEnable() {
		return Optional.ofNullable(this.rentYN).map(yn -> (yn == 'Y')?true:false).orElse(false);
	}
	
	@Override
	public Integer getRoomId() {
		return this.roomKey;
	}

	@Override
	public Character getDisableYN() {
		return (this.rentYN == 'Y')?'N':'Y';
	}

	@Override
	public String getDisableComment() {
		// TODO Auto-generated method stub
		return this.rentReason;
	}
}
