package egov.framework.plms.main.bean.mvc.entity.file.abst;

import egov.framework.plms.main.core.model.able.Convertable;
import egov.framework.plms.main.core.model.enums.file.ConversionStatus;
import egov.framework.plms.main.core.model.enums.file.FileCategory;
import egov.framework.plms.main.core.model.enums.file.FileStatus;

public abstract class FileInfoModelVO extends FileInfoEntity implements Convertable<FileInfoModelDTO>{
	public abstract String getUuid();
	
}
