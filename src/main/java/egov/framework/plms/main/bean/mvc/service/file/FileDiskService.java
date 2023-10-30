package egov.framework.plms.main.bean.mvc.service.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.component.properties.DrmConfigProperties;
import egov.framework.plms.main.bean.mvc.service.file.abst.FileDiskAbstractService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("FileDiskService")
public class FileDiskService extends FileDiskAbstractService{
	
	public FileDiskService(@Autowired DrmConfigProperties drmConfig) {
		super(drmConfig);
	}
}
