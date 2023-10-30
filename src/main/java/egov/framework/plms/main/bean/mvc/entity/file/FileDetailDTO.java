package egov.framework.plms.main.bean.mvc.entity.file;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import egov.framework.plms.main.bean.mvc.entity.file.abst.FileDetailModelDTO;
import egov.framework.plms.main.core.model.enums.file.ConversionStatus;
import egov.framework.plms.main.core.model.enums.file.FileCategory;
import egov.framework.plms.main.core.model.enums.file.FileStatus;
import egov.framework.plms.main.core.model.enums.file.RelatedEntityType;
import egov.framework.plms.main.core.model.enums.file.RelationType;
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
public class FileDetailDTO extends FileDetailModelDTO{
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
	
	private Integer relationId;
	private String registerId;
	private Integer relatedEntityId;
	private RelatedEntityType relatedEntityType;
	private RelationType relationType;
	
	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public FileDetailDTO(FileDetailVO vo) {
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
	    
	    this.relationId = vo.getRelationId();
		this.registerId = vo.getRegisterId();
		this.relatedEntityId = vo.getRelatedEntityId();
		this.relatedEntityType = vo.getRelatedEntityType();
		this.relationType = vo.getRelationType();
	}
	
	@Override
	public FileDetailVO convert() {
		return FileDetailVO.initVO().dto(this).build();
	}

	@Override
	public void setRelatedEntityType(RelatedEntityType value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRelationType(RelationType value) {
		// TODO Auto-generated method stub
		
	}
}
