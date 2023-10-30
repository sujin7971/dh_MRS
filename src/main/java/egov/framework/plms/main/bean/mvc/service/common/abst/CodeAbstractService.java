package egov.framework.plms.main.bean.mvc.service.common.abst;

import java.util.List;

import egov.framework.plms.main.bean.mvc.entity.common.CodeVO;
import egov.framework.plms.main.bean.mvc.mapper.common.CodeMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CodeAbstractService {
	protected final CodeMapper mapper;
	public CodeAbstractService(CodeMapper mapper) {
		this.mapper = mapper;
	}
	
	public List<CodeVO> getComCodeList(CodeVO codeVO){
		List<CodeVO> a = mapper.getComCodeList(codeVO);
		return a;
	}
	
	/**
	 * 코드 조회
	 * @param codeVO
	 * @return
	 */
	public CodeVO getComCodeOne(CodeVO codeVO) {
		return mapper.getComCodeOne(codeVO);
	}
	
	/**
	 * 공통 코드 수정
	 * @param codeVO
	 * @return
	 */
	public boolean putComCode(CodeVO codeVO) {
		try {
			Integer result = mapper.putComCode(codeVO);
			return (result == 0)?false:true;
		}catch(Exception e) {
			log.error("Failed to put ComCode with params: {}", codeVO.toString());
			log.error("Failed to put ComCode messages: {}", e.toString());
			e.printStackTrace();
			return false;
		}
	}
}
