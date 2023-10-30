package egov.framework.plms.sub.ewp.bean.mvc.service.common;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.entity.common.CodeVO;
import egov.framework.plms.main.bean.mvc.mapper.common.CodeMapper;
import egov.framework.plms.main.bean.mvc.service.common.abst.CodeAbstractService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mckim
 * @version 1.0
 * @since 2023. 5. 17
 */
@Slf4j
@Service
@Profile("ewp")
@Primary
public class EwpCodeService extends CodeAbstractService {
	private Map<String, CodeVO> officeCodeBook;
	private Map<String, CodeVO> availableOfficeCodeBook;
	private Map<String, String> availableOfficeNameBook;
	
	public EwpCodeService(@Autowired CodeMapper mapper) {
		// TODO Auto-generated constructor stub
		super(mapper);
		initCodeBook();
	}
	
	private void initCodeBook() {
		List<CodeVO> officeCodeList = super.getComCodeList(CodeVO.builder().classCd("CD001").build());
		List<CodeVO> sortedOfficeCodeList = officeCodeList.stream()
			    .sorted(Comparator.comparing(CodeVO::getSortSeq))
			    .collect(Collectors.toList());
		officeCodeBook = sortedOfficeCodeList.stream().collect(Collectors.toMap(CodeVO::getDtlCd, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));
		availableOfficeCodeBook = sortedOfficeCodeList.stream().filter(code -> code.getUseYn() == 'Y').collect(Collectors.toMap(CodeVO::getDtlCd, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));
		availableOfficeNameBook = availableOfficeCodeBook.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getDtlNm(), (v1, v2) -> v1, LinkedHashMap::new));
	}
	
	/**
	 * 사업소 결재 승인방식 변경
	 * @param officeCode 사업소 코드
	 * @param autoYN 자동결재 여부
	 * @param loginKey 수정한 유저키
	 * @return
	 */
	public boolean putOfficeApprovalPolicy(String officeCode, Character autoYN, String loginKey) {
		String etcCol1 = (autoYN == 'Y')?"AUTH_AUTO":"AUTH_MANUAL";
		boolean result = super.putComCode(CodeVO.builder().classCd("CD001").dtlCd(officeCode).etcCol1(etcCol1).lastUpdEmpId(loginKey).build());
		if(result) {
			initCodeBook();
		}
		return result;
	}
	
	/**
	 * 사업소의 코드 조회
	 * @param officeCode 사업소 코드
	 * @return
	 */
	public CodeVO getOfficeComCodeOne(String officeCode) {
		return officeCodeBook.get(officeCode);
	}
	
	/**
	 * 사용 가능한 사업소 코드북을 이용해, 사업소 이름을 값으로 갖는 맵을 반환합니다.
	 * 이 메서드에서 생성된 맵은 COMMON_CODE테이블의 SORT_SEQ칼럼을 통해 정의된 정렬 순서를 따릅니다.
	 *
	 * @return LinkedHashMap 키는 사업소 코드, 값은 사업소 이름.
	 */
	public Map<String, String> getOfficeBook(){
		return availableOfficeNameBook;
	}
	
	/**
	 * 사업소 코드를 통해 해당 사업소의 이름을 조회합니다.
	 * @param officeCode 사업소 코드
	 * @return 등록된 사업소인 경우 사업소 이름, 그렇지 않다면 <code>NULL</code>
	 */
	public String getOfficeName(String officeCode) {
		if(officeCode.equals("0000")) {
			return "전사";
		}
		return Optional.ofNullable(officeCodeBook.get(officeCode)).map(CodeVO::getDtlNm).orElse(null);
	}
	
	
	/**
	 * 사용 가능한 사업소 코드 집합을 조회합니다.
	 * @return 사용 가능한 사업소 코드의 집합
	 */
	public Set<String> getEntriedOfficeCodeSet() {
		return availableOfficeCodeBook.keySet();
	}
	
	/**
	 * 해당 사업소 코드가 시스템에서 사용 가능한지 여부를 확인합니다.
	 * @param officeCode 사업소 코드
	 * @return 사용 가능하면 <code>true</code>, 사용 불가능하면 <code>false</code>
	 */
	public boolean isEntriedOffice(String officeCode) {
		return availableOfficeCodeBook.containsKey(officeCode);
	}
}
