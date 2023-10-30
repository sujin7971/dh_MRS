package egov.framework.plms.sub.ewp.bean.mvc.entity.file;

import java.time.LocalDateTime;

import org.apache.commons.io.FilenameUtils;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import egov.framework.plms.main.bean.mvc.entity.file.abst.FileDetailModelVO;
import egov.framework.plms.main.core.model.enums.file.ConversionStatus;
import egov.framework.plms.main.core.model.enums.file.FileCategory;
import egov.framework.plms.main.core.model.enums.file.FileStatus;
import egov.framework.plms.main.core.model.enums.file.RelatedEntityType;
import egov.framework.plms.main.core.model.enums.file.RelationType;
import egov.framework.plms.main.core.util.CommUtil;
import egov.framework.plms.main.core.util.FileNameUtil;
import egov.framework.plms.sub.ewp.core.model.enums.FileRole;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class MeetingFileInfoVO extends FileDetailModelVO {
	private Integer fileKey;
	private Integer originalKey;
	private String uploaderKey;
	private Integer resourceKey;
	
	private Integer meetingKey;
	private String empKey;
	private FileRole roleType;
	private FileCategory mimeType;
	private String originalName;
	private String fileName;
	private String fileExt;
	private String uuid;
	private Long size;
	private Integer page;
	private String rootPath;
	private Integer stickyBit;
	private Integer state;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime regDateTime;
	private Character delYN;
	
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public MeetingFileInfoVO(MeetingFileInfoDTO dto) {
		this.fileKey = dto.getFileKey();
		this.uploaderKey = dto.getUploaderKey();
		this.resourceKey = dto.getResourceKey();
		this.meetingKey = dto.getMeetingKey();
		this.empKey = dto.getEmpKey();
		this.roleType = dto.getRoleType();
		this.mimeType = dto.getMimeType();
		this.originalName = dto.getOriginalName();
		this.fileName = dto.getFileName();
		this.fileExt = dto.getFileExt();
		this.size = dto.getSize();
		this.page = dto.getPage();
		this.state = dto.getState();
		this.regDateTime = dto.getRegDateTime();
		this.delYN = dto.getDelYN();
	}
	
	@Override
	public MeetingFileInfoDTO convert() {
		return MeetingFileInfoDTO.initDTO().vo(this).build();
	}
	
	/**
	 * 파일 판서본VO객체 생성.
	 * @param meetingKey : 판서본이 등록될 회의 고유키
	 * @param empKey : 판서본을 소유할 사원 고유키
	 * @param orgVO : 판서본의 원본 파일 객체
	 * @return 파일에 대한 정보가 입력된 FileVO객체
	 */
	public static MeetingFileInfoVO generateCopy(Integer meetingKey, String empKey, MeetingFileInfoVO orgVO) {
		String fileName = FilenameUtils.getBaseName(orgVO.getOriginalName());
		String fileExt = "pdf";
		return MeetingFileInfoVO.builder()
				.roleType(FileRole.COPY)
				.originalKey(orgVO.getFileKey())
				.empKey(empKey)
				.meetingKey(meetingKey)
				.originalName(FileNameUtil.getPdfNameFormat(orgVO.getOriginalName()))
				.fileName(fileName)
				.fileExt(fileExt)
				.mimeType(FileCategory.PDF)
				.uuid(CommUtil.getUUID(12))
				.page(orgVO.getPage())
				.state(0)
				.build();
	}
	
	public static MeetingFileInfoVO generateMemo(Integer meetingKey, String empKey) {
		String fileName = "메모";
		String fileExt = "pdf";
		return MeetingFileInfoVO.builder()
				.roleType(FileRole.MEMO)
				.empKey(empKey)
				.meetingKey(meetingKey)
				.originalName(FileNameUtil.getPdfNameFormat("메모"))
				.fileName(fileName)
				.fileExt(fileExt)
				.mimeType(FileCategory.PDF)
				.uuid(CommUtil.getUUID(12))
				.page(1)
				.state(0)
				.build();
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Character getPageImagesGeneratedYN() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Character getWebpGeneratedYN() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalDateTime getUploadDateTime() {
		// TODO Auto-generated method stub
		return this.regDateTime;
	}
}
