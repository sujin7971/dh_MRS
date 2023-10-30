package egov.framework.plms.sub.ewp.bean.mvc.entity.admin;

import java.util.Optional;

import egov.framework.plms.main.core.model.able.Convertable;
import egov.framework.plms.main.core.model.enums.user.ManagerRole;
import egov.framework.plms.sub.ewp.core.model.enums.SeqMenu;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 동서발전 경영지원서비스의 품목(Item) 담당자(Manager)의 Model
 * 
 * @author mckim
 * @version 1.0
 * @since 2023. 2. 9
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class EwpItemManagerRosterVO implements Convertable<EwpItemManagerRosterDTO> {

	private String seqCharge;
	private SeqMenu seqMenu;
	private String officeCode;
	private String deptId;
	private String userId;
	private String regUser;
	private String regDate;
	private String modifyUser;
	private String modifyDate;
	private Character delYN;

	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public EwpItemManagerRosterVO(EwpItemManagerRosterDTO dto) {
		this.seqCharge = dto.getSeqCharge();
		this.seqMenu = Optional.ofNullable(dto.getSeqMenu()).map(SeqMenu::valueOf).orElse(null);
		this.officeCode = dto.getOfficeCode();
		this.deptId = dto.getDeptId();
		this.userId = dto.getUserId();
		this.regUser = dto.getRegUser();
		this.regDate = dto.getRegDate();
		this.modifyDate = dto.getModifyDate();
		this.modifyUser = dto.getModifyUser();
		this.delYN = dto.getDelYN();
	}

	@Override
	public EwpItemManagerRosterDTO convert() {
		return EwpItemManagerRosterDTO.initDTO().vo(this).build();
	}

	public ManagerRole getManagerRole() {
		ManagerRole role = null;
		switch (this.seqMenu) {
		case ITEM_MEETING_ROOM:
			role = ManagerRole.MEETING_ROOM_MANAGER;
			break;
		case ITEM_EDU_ROOM:
			role = ManagerRole.EDU_ROOM_MANAGER;
			break;
		case ITEM_HALL:
			role = ManagerRole.HALL_MANAGER;
			break;
		}
		return role;
	}
}
