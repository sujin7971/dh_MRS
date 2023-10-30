package egov.framework.plms.main.bean.mvc.entity.room;

import java.util.Optional;

import egov.framework.plms.main.bean.mvc.entity.room.abst.RoomInfoModelVO;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
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
public class RoomInfoVO extends RoomInfoModelVO {
	private Integer roomId;
	private RoomType roomType;
	private String roomCode;
	private String roomName;
	private String roomLabel;
	private String roomNote;
	private Integer roomSize;
	private Integer roomFloor;
	private Character disableYN;
	private String disableComment;
	private String writerId;
	private String regDateTime;
	private String modDateTime;
	private Character delYN;
	
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public RoomInfoVO(RoomInfoDTO dto) {
		if(dto == null) {
			return;
		}
		this.roomId = dto.getRoomId();
		this.roomName = dto.getRoomName();
		this.roomLabel = dto.getRoomLabel();
		this.roomNote = dto.getRoomNote();
		this.roomSize = dto.getRoomSize();
		this.roomFloor = dto.getRoomFloor();
		this.disableYN = dto.getDisableYN();
		this.disableComment = dto.getDisableComment();
		this.writerId = dto.getWriterId();
		this.regDateTime = dto.getRegDateTime();
		this.modDateTime = dto.getModDateTime();
		this.delYN = dto.getDelYN();
		this.roomType = dto.getRoomType();
		this.roomCode = dto.getRoomCode();
	}

	@Override
	public RoomInfoDTO convert() {
		return RoomInfoDTO.initDTO().vo(this).build();
	}

	@Override
	public boolean isEnable() {
		return Optional.ofNullable(this.disableYN).map(yn -> (yn == 'Y')?true:false).orElse(false);
	}
}
