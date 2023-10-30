package egov.framework.plms.main.bean.mvc.entity.auth;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
/**
 * 해당 리소스 그룹과 권한 연결 모델
 * @author mckim
 */
public class ResourceGroupVO {
	private String grpId;
	private String grpDiv;
	private String grpCode;
	private Integer srcId;
	private Integer permId;
	
	public static String generateId(String grpDiv, String grpCode, Integer srcId) {
		return grpDiv+"_"+grpCode+"_"+srcId;
	}
}
