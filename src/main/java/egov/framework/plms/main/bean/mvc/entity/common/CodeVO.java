package egov.framework.plms.main.bean.mvc.entity.common;

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
public class CodeVO {
	private String classCd;
	private String classNm;
	private String dtlCd;
	private String dtlNm;
	private String dispColNm;
	private String classType;
	private Integer sortSeq;
	private String rmk;
	private Character useYn;
	private String etcCol1;
	private String etcCol2;
	private String etcCol3;
	private String etcCol4;
	private String updCode;
	private String insDate;
	private String insEmpId;
	private String lastUpdDate;
	private String lastUpdEmpId;
}
