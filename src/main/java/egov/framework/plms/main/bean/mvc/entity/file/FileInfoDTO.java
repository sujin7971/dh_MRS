package egov.framework.plms.main.bean.mvc.entity.file;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import egov.framework.plms.main.bean.mvc.entity.file.abst.FileInfoModelDTO;
import egov.framework.plms.main.core.model.enums.file.ConversionStatus;
import egov.framework.plms.main.core.model.enums.file.FileCategory;
import egov.framework.plms.main.core.model.enums.file.FileStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileInfoDTO extends FileInfoModelDTO{
	private Integer fileId;
	private Integer sourceId;
	private String uploaderId;
	private FileCategory fileCategory;
	private String uploadedFileName;
	private String fileLabel;
	private String fileExt;
	private Long fileSize;
	private Integer pageCount;
	private FileStatus fileStatus;
	private ConversionStatus conversionStatus;
	private Character pdfGeneratedYN;
	private Character pageImagesGeneratedYN;
	private Character webpGeneratedYN;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime uploadDateTime;
	private Character delYN;
	
	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public FileInfoDTO(FileInfoVO vo) {
		this.fileId = vo.getFileId();
	    this.sourceId = vo.getSourceId();
	    this.uploaderId = vo.getUploaderId();
	    this.fileCategory = vo.getFileCategory();
	    this.uploadedFileName = vo.getUploadedFileName();
	    this.fileLabel = vo.getFileLabel();
	    this.fileExt = vo.getFileExt();
	    this.fileSize = vo.getFileSize();
	    this.pageCount = vo.getPageCount();
	    this.fileStatus = vo.getFileStatus();
	    this.conversionStatus = vo.getConversionStatus();
	    this.pdfGeneratedYN = vo.getPdfGeneratedYN();
	    this.pageImagesGeneratedYN = vo.getPageImagesGeneratedYN();
	    this.webpGeneratedYN = vo.getWebpGeneratedYN();
	    this.uploadDateTime = vo.getUploadDateTime();
	}

	@Override
	public FileInfoVO convert() {
		return FileInfoVO.initVO().dto(this).build();
	}
}
