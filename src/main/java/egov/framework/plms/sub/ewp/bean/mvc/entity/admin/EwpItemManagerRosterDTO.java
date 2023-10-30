package egov.framework.plms.sub.ewp.bean.mvc.entity.admin;

import java.util.Optional;

import org.springframework.context.annotation.Profile;

import egov.framework.plms.main.core.model.able.Convertable;
import egov.framework.plms.sub.ewp.core.model.enums.SeqMenu;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@Profile("ewp")
public class EwpItemManagerRosterDTO implements Convertable<EwpItemManagerRosterVO>{
	private String seqCharge;
	private Integer seqMenu;
	private String officeCode;
	private String officeName;
	private String deptId;
	private String deptName;
	private String userId;
	private String userName;
	private String regUser;
	private String regDate;
	private String modifyUser;
	private String modifyDate;
	private Character delYN;

	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public EwpItemManagerRosterDTO(EwpItemManagerRosterVO vo) {
		this.seqCharge = vo.getSeqCharge();
		this.seqMenu = Optional.ofNullable(vo.getSeqMenu()).map(SeqMenu::getCode).orElse(null);
		this.officeCode = vo.getOfficeCode();
		this.deptId = vo.getDeptId();
		this.userId = vo.getUserId();
		this.regUser = vo.getRegUser();
		this.regDate = vo.getRegDate();
		this.modifyDate = vo.getModifyDate();
		this.modifyUser = vo.getModifyUser();
		this.delYN = vo.getDelYN();
	}

	@Override
	public EwpItemManagerRosterVO convert() {
		return EwpItemManagerRosterVO.initVO().dto(this).build();
	}
	
}
