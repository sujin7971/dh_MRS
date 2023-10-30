package egov.framework.plms.sub.ewp.bean.mvc.entity.file;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import egov.framework.plms.main.bean.mvc.entity.file.abst.FileDetailModelDTO;
import egov.framework.plms.main.core.model.enums.file.ConversionStatus;
import egov.framework.plms.main.core.model.enums.file.FileCategory;
import egov.framework.plms.main.core.model.enums.file.FileStatus;
import egov.framework.plms.main.core.model.enums.file.RelatedEntityType;
import egov.framework.plms.main.core.model.enums.file.RelationType;
import egov.framework.plms.sub.ewp.core.model.enums.FileRole;
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
public class MeetingFileInfoDTO extends FileDetailModelDTO {
	private Integer fileKey;
	private String uploaderKey;
	private Integer resourceKey;
	
	private Integer meetingKey;
	private String empKey;
	private FileRole roleType;
	private FileCategory mimeType;
	private String originalName;
	private String fileName;
	private String fileExt;
	private Long size;
	private Integer page;
	private Integer state;
	private LocalDateTime regDateTime;
	private Character delYN;
	
	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public MeetingFileInfoDTO(MeetingFileInfoVO vo) {
		this.fileKey = vo.getFileKey();
		this.uploaderKey = vo.getUploaderKey();
		this.resourceKey = vo.getResourceKey();
		this.meetingKey = vo.getMeetingKey();
		this.empKey = vo.getEmpKey();
		this.roleType = vo.getRoleType();
		this.mimeType = vo.getMimeType();
		this.originalName = vo.getOriginalName();
		this.fileName = vo.getFileName();
		this.fileExt = vo.getFileExt();
		this.size = vo.getSize();
		this.page = vo.getPage();
		this.state = vo.getState();
		this.delYN = vo.getDelYN();
	}

	@Override
	public MeetingFileInfoVO convert() {
		return MeetingFileInfoVO.initVO().dto(this).build();
	}

	@Override
	public Integer getRelationId() {
		return null;
	}

	@Override
	public String getRegisterId() {
		return this.uploaderKey;
	}

	@Override
	public Integer getRelatedEntityId() {
		return this.meetingKey;
	}

	@Override
	public RelatedEntityType getRelatedEntityType() {
		return RelatedEntityType.MEETING;
	}

	@Override
	public RelationType getRelationType() {
		switch(this.roleType) {
			case MATERIAL:
				return RelationType.MEETING_MATERIAL;
			case MEMO:
				return RelationType.MEETING_MEMO;
			case COPY:
				return RelationType.MEETING_COPY;
			case REPORT:
				return RelationType.MEETING_REPORT;
			case PHOTO:
				return RelationType.MEETING_PHOTO;
			case VOICE:
				return RelationType.MEETING_VOICE;
			default:
				return null;
		}
	}

	@Override
	public Integer getFileId() {
		return this.fileKey;
	}

	@Override
	public Integer getSourceId() {
		return this.resourceKey;
	}

	@Override
	public String getUploaderId() {
		return this.uploaderKey;
	}

	@Override
	public String getUploadedFileName() {
		return this.originalName;
	}

	@Override
	public String getFileLabel() {
		return this.fileName;
	}

	@Override
	public Long getFileSize() {
		return this.size;
	}

	@Override
	public FileCategory getFileCategory() {
		return this.mimeType;
	}

	@Override
	public FileStatus getFileStatus() {
		if(this.roleType == FileRole.COPY) {
			switch(this.state) {
				case -3:
					return FileStatus.DELETED_BY_LOGIC;
				case -2:
					return FileStatus.DELETED_BY_EXPIRATION;
				case -1:
					return FileStatus.FILE_MISSING;
				case 0:
					return FileStatus.FILE_PENDING;
				case 1:
					return FileStatus.FILE_PENDING;
				case 2:
					return FileStatus.EXISTS;
				default:
					return null;
			}
		}else {
			switch(this.state) {
				case -3:
					return FileStatus.DELETED_BY_LOGIC;
				case -2:
					return FileStatus.DELETED_BY_EXPIRATION;
				case -1:
					return FileStatus.EXISTS;
				case 0:
					return FileStatus.EXISTS;
				case 1:
					return FileStatus.EXISTS;
				case 2:
					return FileStatus.EXISTS;
				default:
					return null;
			}
		}
	}

	@Override
	public ConversionStatus getConversionStatus() {
		if(this.roleType == FileRole.COPY) {
			switch(this.state) {
				case -3:
					return ConversionStatus.COMPLETED;
				case -2:
					return ConversionStatus.COMPLETED;
				case -1:
					return ConversionStatus.FAILED;
				case 0:
					return ConversionStatus.NOT_STARTED;
				case 1:
					return ConversionStatus.IMAGES_TO_PDF;
				case 2:
					return ConversionStatus.COMPLETED;
				default:
					return null;
			}
		}else {
			switch(this.state) {
				case -3:
					return ConversionStatus.COMPLETED;
				case -2:
					return ConversionStatus.COMPLETED;
				case -1:
					return ConversionStatus.FAILED;
				case 0:
					return ConversionStatus.NOT_STARTED;
				case 1:
					return ConversionStatus.DOC_TO_PDF;
				case 2:
					return ConversionStatus.COMPLETED;
				default:
					return null;
			}
		}
	}

	@Override
	public Character getPdfGeneratedYN() {
		return null;
	}

	@Override
	public Character getPageImagesGeneratedYN() {
		return null;
	}

	@Override
	public Character getWebpGeneratedYN() {
		return null;
	}

	@Override
	public void setRelationId(Integer value) {
		
	}

	@Override
	public void setRegisterId(String value) {
		this.uploaderKey = value;
	}

	@Override
	public void setRelatedEntityId(Integer value) {
		this.meetingKey = value;
	}

	@Override
	public void setRelatedEntityType(RelatedEntityType value) {
		
	}

	@Override
	public void setRelationType(RelationType value) {
		switch(value) {
			case MEETING_MATERIAL:
				this.roleType = FileRole.MATERIAL;
			case MEETING_MEMO:
				this.roleType = FileRole.MEMO;
			case MEETING_COPY:
				this.roleType = FileRole.COPY;
			case MEETING_REPORT:
				this.roleType = FileRole.REPORT;
			case MEETING_PHOTO:
				this.roleType = FileRole.PHOTO;
			case MEETING_VOICE:
				this.roleType = FileRole.VOICE;
			default:
		}
	}

	@Override
	public void setFileId(Integer value) {
		this.fileKey = value;
	}

	@Override
	public void setSourceId(Integer value) {
		this.resourceKey = value;
	}

	@Override
	public void setUploaderId(String value) {
		this.uploaderKey = value;
	}

	@Override
	public void setUploadedFileName(String value) {
		this.originalName = value;
	}

	@Override
	public void setFileLabel(String value) {
		this.fileName = value;
	}

	@Override
	public void setFileSize(Long value) {
		this.size = value;
	}

	@Override
	public void setFileCategory(FileCategory value) {
		this.mimeType = value;
	}

	@Override
	public void setFileStatus(FileStatus value) {
		
	}

	@Override
	public void setConversionStatus(ConversionStatus value) {
		
	}

	@Override
	public void setPdfGeneratedYN(Character value) {
		
	}

	@Override
	public void setPageImagesGeneratedYN(Character value) {
		
	}

	@Override
	public void setWebpGeneratedYN(Character value) {
		
	}

	@Override
	public LocalDateTime getUploadDateTime() {
		// TODO Auto-generated method stub
		return this.regDateTime;
	}
}
