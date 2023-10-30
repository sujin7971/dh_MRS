package egov.framework.plms.main.bean.mvc.service.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.mapper.common.CodeMapper;
import egov.framework.plms.main.bean.mvc.service.common.abst.CodeAbstractService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("CodeService")
public class CodeService extends CodeAbstractService {
	public CodeService(@Autowired CodeMapper mapper) {
		// TODO Auto-generated constructor stub
		super(mapper);
	}
}
