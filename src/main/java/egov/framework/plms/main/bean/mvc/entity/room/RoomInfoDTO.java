package egov.framework.plms.main.bean.mvc.entity.room;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;

import egov.framework.plms.main.bean.mvc.entity.room.abst.RoomInfoModelDTO;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomInfoDTO extends RoomInfoModelDTO {
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
	
	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public RoomInfoDTO(RoomInfoVO vo) {
		if(vo == null) {
			return;
		}
		this.roomId = vo.getRoomId();
		this.roomName = vo.getRoomName();
		this.roomLabel = vo.getRoomLabel();
		this.roomNote = vo.getRoomNote();
		this.roomSize = vo.getRoomSize();
		this.roomFloor = vo.getRoomFloor();
		this.disableYN = vo.getDisableYN();
		this.disableComment = vo.getDisableComment();
		this.writerId = vo.getWriterId();
		this.regDateTime = vo.getRegDateTime();
		this.modDateTime = vo.getModDateTime();
		this.delYN = vo.getDelYN();
		this.roomType = vo.getRoomType();
		this.roomCode = vo.getRoomCode();
	}
	
	@Override
	public RoomInfoVO convert() {
		return RoomInfoVO.initVO().dto(this).build();
	}

	public boolean isEnable() {
		return Optional.ofNullable(disableYN).map(yn -> disableYN == 'Y').orElse(false);
	}


	@Override
	public void setEnable(boolean value) {
		if(value) {
			this.disableYN = 'N';
		}else {
			this.disableYN = 'Y';
		}
	}

	@Override
	public RoomInfoDTO filterForEssential() {
		// TODO Auto-generated method stub
		return (RoomInfoDTO) super.filterForEssential();
	}

	@Override
	public RoomInfoDTO filterForBasic() {
		// TODO Auto-generated method stub
		return (RoomInfoDTO) super.filterForBasic();
	}

	@Override
	public RoomInfoDTO filterForDetailed() {
		// TODO Auto-generated method stub
		return (RoomInfoDTO) super.filterForDetailed();
	}

	@Override
	public RoomInfoDTO filterForSensitive() {
		// TODO Auto-generated method stub
		return (RoomInfoDTO) super.filterForSensitive();
	}

	@Override
	public RoomInfoDTO filterForHighest() {
		// TODO Auto-generated method stub
		return (RoomInfoDTO) super.filterForHighest();
	}
	
	
}
