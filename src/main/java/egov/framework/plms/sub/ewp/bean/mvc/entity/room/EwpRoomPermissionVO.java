package egov.framework.plms.sub.ewp.bean.mvc.entity.room;

import java.util.List;

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
public class EwpRoomPermissionVO {
	private Integer roomPermKey;
	private String roomCode;
	private String officeCode;
	private RoomType roomType;
	private Integer roomKey;
	private String deptId;
	private String deptName;
	private String regUser;
	private String regDate;
	private List<String> subDeptList;
	
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public EwpRoomPermissionVO(EwpRoomPermissionDTO dto) {
		if(dto == null) {
			return;
		}
		this.roomPermKey = dto.getRoomPermKey();
		this.roomCode = dto.getRoomCode();
		this.officeCode = dto.getOfficeCode();
		this.roomType = dto.getRoomType();
		this.roomKey = dto.getRoomKey();
		this.deptId = dto.getDeptId();
		this.deptName = dto.getDeptName();
		this.regUser = dto.getRegUser();
		this.regDate = dto.getRegDate();
	}
	
	public EwpRoomPermissionDTO convert() {
		return EwpRoomPermissionDTO.initDTO().vo(this).build();
	}
}
