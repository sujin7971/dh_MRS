package egov.framework.plms.sub.ewp.bean.mvc.entity.room;

import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class EwpRoomPermissionDTO {
	private Integer roomPermKey;
	private String roomCode;
	private String officeCode;
	private RoomType roomType;
	private Integer roomKey;
	private String deptId;
	private String deptName;
	private String regUser;
	private String regDate;
	
	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public EwpRoomPermissionDTO(EwpRoomPermissionVO vo) {
		if(vo == null) {
			return;
		}
		this.roomPermKey = vo.getRoomPermKey();
		this.roomCode = vo.getRoomCode();
		this.officeCode = vo.getOfficeCode();
		this.roomType = vo.getRoomType();
		this.roomKey = vo.getRoomKey();
		this.deptId = vo.getDeptId();
		this.deptName = vo.getDeptName();
		this.regUser = vo.getRegUser();
		this.regDate = vo.getRegDate();
	}
	
	public EwpRoomPermissionVO convert() {
		return EwpRoomPermissionVO.initVO().dto(this).build();
	}
}
