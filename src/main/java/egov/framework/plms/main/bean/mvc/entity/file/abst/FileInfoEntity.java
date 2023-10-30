package egov.framework.plms.main.bean.mvc.entity.file.abst;

import java.time.LocalDateTime;

import egov.framework.plms.main.core.model.enums.file.ConversionStatus;
import egov.framework.plms.main.core.model.enums.file.FileCategory;
import egov.framework.plms.main.core.model.enums.file.FileStatus;

public abstract class FileInfoEntity {
	public abstract Integer getFileId();
	public abstract Integer getSourceId();
	public abstract String getUploaderId();
	public abstract String getUploadedFileName();
	public abstract String getFileLabel();
	public abstract String getFileExt();
	public abstract Long getFileSize();
	public abstract FileCategory getFileCategory();
	public abstract FileStatus getFileStatus();
	public abstract ConversionStatus getConversionStatus();
	public abstract Character getPdfGeneratedYN();
	public abstract Character getPageImagesGeneratedYN();
	public abstract Character getWebpGeneratedYN();
	public abstract Character getDelYN();
	public abstract LocalDateTime getUploadDateTime();
}
