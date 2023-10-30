package egov.framework.plms.main.bean.mvc.entity.file;

import java.time.LocalDateTime;

import egov.framework.plms.main.bean.mvc.entity.file.abst.FileInfoModelVO;
import egov.framework.plms.main.core.model.enums.file.ConversionStatus;
import egov.framework.plms.main.core.model.enums.file.FileCategory;
import egov.framework.plms.main.core.model.enums.file.FileStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
@Builder(toBuilder = true)
@ToString
public class FileInfoVO extends FileInfoModelVO{
	private Integer fileId;
	private Integer sourceId;
	private String uploaderId;
	private FileCategory fileCategory;
	private String uploadedFileName;
	private String fileLabel;
	private String fileExt;
	private String uuid;
	private Long fileSize;
	private Integer pageCount;
	private FileStatus fileStatus;
	private ConversionStatus conversionStatus;
	private Character pdfGeneratedYN;
	private Character pageImagesGeneratedYN;
	private Character webpGeneratedYN;
	private LocalDateTime uploadDateTime;
	private Character delYN;
	
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public FileInfoVO(FileInfoDTO dto) {
		this.fileId = dto.getFileId();
	    this.sourceId = dto.getSourceId();
	    this.uploaderId = dto.getUploaderId();
	    this.fileCategory = dto.getFileCategory();
	    this.uploadedFileName = dto.getUploadedFileName();
	    this.fileLabel = dto.getFileLabel();
	    this.fileExt = dto.getFileExt();
	    this.uuid = null;
	    this.fileSize = dto.getFileSize();
	    this.pageCount = dto.getPageCount();
	    this.fileStatus = dto.getFileStatus();
	    this.conversionStatus = dto.getConversionStatus();
	    this.pdfGeneratedYN = dto.getPdfGeneratedYN();
	    this.pageImagesGeneratedYN = dto.getPageImagesGeneratedYN();
	    this.webpGeneratedYN = dto.getWebpGeneratedYN();
	    this.uploadDateTime = dto.getUploadDateTime();
	}
	
	@Override
	public FileInfoDTO convert() {
		return FileInfoDTO.initDTO().vo(this).build();
	}
}
