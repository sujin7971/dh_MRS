package egov.framework.plms.main.bean.mvc.entity.file.abst;

import egov.framework.plms.main.core.model.able.Convertable;
import egov.framework.plms.main.core.model.enums.file.ConversionStatus;
import egov.framework.plms.main.core.model.enums.file.FileCategory;
import egov.framework.plms.main.core.model.enums.file.FileStatus;

public abstract class FileInfoModelDTO extends FileInfoEntity implements Convertable<FileInfoModelVO>{
	public abstract void setFileId(Integer value);
	public abstract void setSourceId(Integer value);
	public abstract void setUploaderId(String value);
	public abstract void setUploadedFileName(String value);
	public abstract void setFileLabel(String value);
	public abstract void setFileExt(String value);
	public abstract void setFileSize(Long value);
	public abstract void setFileCategory(FileCategory value);
	public abstract void setFileStatus(FileStatus value);
	public abstract void setConversionStatus(ConversionStatus value);
	public abstract void setPdfGeneratedYN(Character value);
	public abstract void setPageImagesGeneratedYN(Character value);
	public abstract void setWebpGeneratedYN(Character value);
	public abstract void setDelYN(Character value);
}
