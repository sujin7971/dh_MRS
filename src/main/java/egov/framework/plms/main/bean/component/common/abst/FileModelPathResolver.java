package egov.framework.plms.main.bean.component.common.abst;

import java.io.File;
import java.time.LocalDateTime;

import egov.framework.plms.main.bean.mvc.entity.file.abst.FileDetailModelVO;
import egov.framework.plms.main.core.exception.ApiNotFoundException;
import egov.framework.plms.main.core.model.enums.file.FileCategory;
import egov.framework.plms.main.core.model.enums.file.RelationType;
import lombok.extern.slf4j.Slf4j;

/**
 * 이 클래스는 파일 경로를 결정하는 로직을 담당합니다. FileDetailModelVO를 상속하는 파일 모델을 이용하여, 관계 유형 및
 * 파일 유형에 따라 해당 파일 모델이 가리키는 경로를 반환하는 메서드들을 제공합니다. 이 클래스는 추상 클래스로, 각각의 운영 프로필에 
 * 맞게 이 클래스를 상속받아 구현하고 컴포넌트 빈으로 사용해야 합니다.
 *<br>
 * 이 클래스로부터 파생된 메서드들은 입력 파일 모델의 검증 작업을 수행하지 않습니다. 이 클래스를 사용하기 전에 필요하다면, 파일 모델이
 * 올바르게 설정되었는지 확인해야 합니다. 또한, 이 클래스의 메서드들은 파일의 물리적 위치만을 반환하며, 파일의 존재 여부나 접근 가능 여부를 
 * 확인하지 않습니다.
 *<br>
 * 이 클래스를 사용하는 클라이언트는 파일 모델의 상태와 관계 유형, 파일 유형 등의 정보를 이해하고 있어야 하며, 반환된 경로를 사용하여 
 * 추가 작업을 수행할 수 있습니다.
 *
 * @author mckim
 * @version 1.0
 * @since 2023. 6. 15
 * @param <T> {@link FileDetailModelVO}를 상속받은 파일 모델
 */
@Slf4j
public abstract class FileModelPathResolver<T extends FileDetailModelVO> {
	private final String UPLOAD_ROOT_PATH;

	/**
	 * 파일 모델 경로 해결자 생성자
	 *
	 * @param UPLOAD_ROOT_PATH 모든 파일의 최상위 경로
	 */
	public FileModelPathResolver(String UPLOAD_ROOT_PATH) {
		this.UPLOAD_ROOT_PATH = UPLOAD_ROOT_PATH;
	}

	/**
	 * 회의 등록 날짜 시간을 가져오는 추상 메서드 서브 클래스에서 이 메서드를 재정의하면, 회의 ID를 기반으로 회의 등록 날짜 시간을
	 * 가져옵니다.
	 *
	 * @param meetingId 회의 ID
	 * @return 해당 회의의 등록 날짜 시간
	 */
	protected abstract LocalDateTime getMeetingRegDateTime(Integer meetingId);

	/**
	 * 파일 모델을 기반으로 파일의 기본 경로를 가져옵니다. 생성자를 통해 전달받은 모든 파일의 최상위 경로로부터, 파일 분류별로 다른 기본 경로를 가집니다.
	 *
	 * @param fileModel 파일 상세 정보 모델
	 * @return 파일의 기본 경로
	 */
	protected String getFileBasePath(T fileModel) {
		RelationType relationType = fileModel.getRelationType();
		if (relationType == null) {
			return getUserFileBasePath(fileModel);
		}
		switch (relationType) {
			case MEETING_REPORT:
			case MEETING_COPY:
			case MEETING_MEMO:
			case MEETING_PHOTO:
			case MEETING_VOICE:
				return getMeetingFileBasePath(fileModel);
			default:
				return getUserFileBasePath(fileModel);
		}
	}

	/**
	 * 파일 모델을 기반으로 사용자 파일의 기본 경로를 가져옵니다.
	 *
	 * @param fileModel 파일 상세 정보 모델
	 * @return 사용자 파일의 기본 경로
	 */
	protected String getUserFileBasePath(T fileModel) {
		String ROOT_PATH = UPLOAD_ROOT_PATH;
		if (!ROOT_PATH.endsWith(File.separator)) {
			ROOT_PATH += File.separator;
		}
		String uploaderId = fileModel.getUploaderId();
		LocalDateTime uploadDateTime = fileModel.getUploadDateTime();
		Integer year = uploadDateTime.getYear();
		String UUID = fileModel.getUuid();
		return ROOT_PATH + "user" + File.separator + uploaderId + File.separator + year + File.separator + UUID;
	}

	/**
	 * 파일 모델을 기반으로 회의 파일의 기본 경로를 가져옵니다.
	 *
	 * @param fileModel 파일 상세 정보 모델
	 * @return 회의 파일의 기본 경로
	 */
	protected String getMeetingFileBasePath(T fileModel) {
		String ROOT_PATH = UPLOAD_ROOT_PATH;
		if (!ROOT_PATH.endsWith(File.separator)) {
			ROOT_PATH += File.separator;
		}
		Integer meetingId = fileModel.getRelatedEntityId();
		LocalDateTime regDateTime = getMeetingRegDateTime(meetingId);
		Integer year = regDateTime.getYear();
		return ROOT_PATH + "meeting" + File.separator + year + File.separator + meetingId;
	}

	/**
	 * 회의 ID를 기반으로 회의 파일의 기본 경로를 가져옵니다.
	 *
	 * @param meetingId 회의 ID
	 * @return 회의 파일의 기본 경로
	 */
	protected String getMeetingFileBasePath(Integer meetingId) {
		String ROOT_PATH = UPLOAD_ROOT_PATH;
		if (!ROOT_PATH.endsWith(File.separator)) {
			ROOT_PATH += File.separator;
		}
		LocalDateTime regDateTime = getMeetingRegDateTime(meetingId);
		Integer year = regDateTime.getYear();
		return ROOT_PATH + "meeting" + File.separator + year + File.separator + meetingId;
	}

	/**
	 * 파일 모델을 기반으로 업로드된 파일의 경로를 가져옵니다.
	 *
	 * @param fileModel 파일 상세 정보 모델
	 * @return 업로드된 파일의 경로
	 */
	public String getUploadedFilePath(T fileModel) {
		RelationType relationType = fileModel.getRelationType();
		if (relationType == null) {
			return getUploadedUserFilePath(fileModel);
		}
		switch (relationType) {
			case MEETING_REPORT:
			case MEETING_COPY:
			case MEETING_MEMO:
			case MEETING_PHOTO:
			case MEETING_VOICE:
				return getUploadedMeetingFilePath(fileModel);
			default:
				return getUploadedUserFilePath(fileModel);
		}
	}

	/**
	 * 파일 모델을 기반으로 업로드된 파일 객체를 가져옵니다.
	 *
	 * @param fileModel 파일 상세 정보 모델
	 * @return 업로드된 파일 객체
	 */
	public File getUploadedFile(T fileModel) {
		String path = getUploadedFilePath(fileModel);
		return new File(path);
	}

	/**
	 * 파일 모델을 기반으로 미리보기를 위한 변환된 파일의 경로를 가져옵니다.
	 *
	 * @param fileModel 파일 상세 정보 모델
	 * @return 미리보기를 위한 변환된 파일의 경로
	 */
	public String getConvertedFilePathForPreview(T fileModel) {
		RelationType relationType = fileModel.getRelationType();
		if (relationType == null) {
			return getConvertedUserFilePathForPreview(fileModel);
		}
		switch (relationType) {
			case MEETING_REPORT:
				return getMeetingReportFilePath(fileModel);
			case MEETING_COPY:
				return getMeetingCopyFilePath(fileModel);
			case MEETING_MEMO:
				return getMeetingMemoFilePath(fileModel);
			case MEETING_PHOTO:
				return getMeetingPhotoFilePath(fileModel);
			case MEETING_VOICE:
				return getMeetingVoiceFilePath(fileModel);
			default:
				return getConvertedUserFilePathForPreview(fileModel);
		}
	}

	/**
	 * 파일 모델을 기반으로 미리보기를 위한 변환된 파일 객체를 가져옵니다.
	 *
	 * @param fileModel 파일 상세 정보 모델
	 * @return 미리보기를 위한 변환된 파일 객체
	 */
	public File getConvertedFileForPreview(T fileModel) {
		String path = getConvertedFilePathForPreview(fileModel);
		return new File(path);
	}

	/**
	 * 파일 모델을 기반으로 업로드된 사용자 파일의 경로를 가져옵니다.
	 *
	 * @param fileModel 파일 상세 정보 모델
	 * @return 업로드된 사용자 파일의 경로
	 */
	protected String getUploadedUserFilePath(T fileModel) {
		String BASE_PATH = getFileBasePath(fileModel);
		if (!BASE_PATH.endsWith(File.separator)) {
			BASE_PATH += File.separator;
		}
		String FILE_NAME = fileModel.getUuid() + "." + fileModel.getFileExt();
		return BASE_PATH + FILE_NAME;
	}

	/**
	 * 파일 모델을 기반으로 변환된 사용자 파일의 경로를 가져옵니다.
	 *
	 * @param fileModel 파일 상세 정보 모델
	 * @return 변환된 사용자 파일의 경로
	 */
	protected String getConvertedUserFilePathForPreview(T fileModel) {
		String BASE_PATH = getFileBasePath(fileModel);
		if (!BASE_PATH.endsWith(File.separator)) {
			BASE_PATH += File.separator;
		}
		FileCategory fileCategory = fileModel.getFileCategory();
		switch (fileCategory) {
			case IMG: {
				String FILE_NAME = fileModel.getUuid() + ".webp";
				return BASE_PATH + FILE_NAME;
			}
			case HWP:
			case WORD:
			case PPT:
			case EXCEL: {
				String FILE_NAME = fileModel.getUuid() + ".pdf";
				return BASE_PATH + FILE_NAME;
			}
			default: {
				String FILE_NAME = fileModel.getUuid() + "." + fileModel.getFileExt();
				return BASE_PATH + FILE_NAME;
			}
		}
	}

	/**
	 * 파일 모델을 기반으로 업로드된 회의 파일의 경로를 가져옵니다.
	 *
	 * @param fileModel 파일 상세 정보 모델
	 * @return 업로드된 회의 파일의 경로
	 */
	protected String getUploadedMeetingFilePath(T fileModel) {
		RelationType relationType = fileModel.getRelationType();
		if (relationType == null) {
			return getUploadedUserFilePath(fileModel);
		}
		switch (relationType) {
			case MEETING_REPORT:
				return getMeetingReportFilePath(fileModel);
			case MEETING_COPY:
				return getMeetingCopyFilePath(fileModel);
			case MEETING_MEMO:
				return getMeetingMemoFilePath(fileModel);
			case MEETING_PHOTO:
				return getMeetingPhotoFilePath(fileModel);
			case MEETING_VOICE:
				return getMeetingVoiceFilePath(fileModel);
			case MEETING_MATERIAL:
			default:
				return getUploadedUserFilePath(fileModel);
		}
	}

	/**
	 * 파일 모델을 기반으로 회의 복사본 파일의 경로를 가져옵니다.
	 *
	 * @param fileModel 파일 상세 정보 모델
	 * @return 회의 복사본 파일의 경로
	 */
	protected String getMeetingCopyFilePath(T fileModel) throws ApiNotFoundException {
		String BASE_PATH = getFileBasePath(fileModel);
		if (!BASE_PATH.endsWith(File.separator)) {
			BASE_PATH += File.separator;
		}
		String FILE_NAME = fileModel.getUuid() + "." + fileModel.getFileExt();

		return BASE_PATH + "COPY" + File.separator + FILE_NAME;
	}

	/**
	 * 파일 모델을 기반으로 회의 보고서 파일의 경로를 가져옵니다.
	 *
	 * @param fileModel 파일 상세 정보 모델
	 * @return 회의 보고서 파일의 경로
	 */
	protected String getMeetingReportFilePath(T fileModel) throws ApiNotFoundException {
		String BASE_PATH = getFileBasePath(fileModel);
		if (!BASE_PATH.endsWith(File.separator)) {
			BASE_PATH += File.separator;
		}
		String FILE_NAME = fileModel.getUuid() + "." + fileModel.getFileExt();

		return BASE_PATH + "REPORT" + File.separator + FILE_NAME;
	}

	/**
	 * 파일 모델을 기반으로 회의 메모 파일의 경로를 가져옵니다.
	 *
	 * @param fileModel 파일 상세 정보 모델
	 * @return 회의 메모 파일의 경로
	 */
	protected String getMeetingMemoFilePath(T fileModel) throws ApiNotFoundException {
		String BASE_PATH = getFileBasePath(fileModel);
		if (!BASE_PATH.endsWith(File.separator)) {
			BASE_PATH += File.separator;
		}
		String FILE_NAME = fileModel.getUuid() + "." + fileModel.getFileExt();

		return BASE_PATH + "MEMO" + File.separator + FILE_NAME;
	}

	/**
	 * 이 메소드는 주어진 회의 ID와 사용자 ID에 대한 메모 이미지 컬렉션의 경로를 반환합니다.
	 * 각 사용자가 회의 중에 작성한 메모의 이미지들이 이 경로에 저장됩니다.
	 * 
	 * @param meetingId 회의 ID
	 * @param userId 사용자 ID
	 * @return 메모 이미지 컬렉션의 경로를 문자열로 반환합니다.
	 */
	public String getMeetingMemoImageCollectionPath(Integer meetingId, String userId) {
	    String BASE_PATH = getMeetingFileBasePath(meetingId);
	    if (!BASE_PATH.endsWith(File.separator)) {
	        BASE_PATH += File.separator;
	    }
	    String FOLDER_NAME = "MEMO";

	    return BASE_PATH + FOLDER_NAME + File.separator + userId;
	}

	/**
	 * 파일 모델을 기반으로 회의 사진 파일의 경로를 가져옵니다.
	 *
	 * @param fileModel 파일 상세 정보 모델
	 * @return 회의 사진 파일의 경로
	 */
	protected String getMeetingPhotoFilePath(T fileModel) throws ApiNotFoundException {
		String BASE_PATH = getFileBasePath(fileModel);
		if (!BASE_PATH.endsWith(File.separator)) {
			BASE_PATH += File.separator;
		}
		String FILE_NAME = fileModel.getUuid() + "." + fileModel.getFileExt();

		return BASE_PATH + "PHOTO" + File.separator + FILE_NAME;
	}

	/**
	 * 파일 모델을 기반으로 회의 음성 파일의 경로를 가져옵니다.
	 *
	 * @param fileModel 파일 상세 정보 모델
	 * @return 회의 음성 파일의 경로
	 */
	protected String getMeetingVoiceFilePath(T fileModel) throws ApiNotFoundException {
		String BASE_PATH = getFileBasePath(fileModel);
		if (!BASE_PATH.endsWith(File.separator)) {
			BASE_PATH += File.separator;
		}
		String FILE_NAME = fileModel.getUuid() + "." + fileModel.getFileExt();

		return BASE_PATH + "VOICE" + File.separator + FILE_NAME;
	}

	/**
     * 원본 이미지 컬렉션의 경로를 가져옵니다.
     *
     * @param fileModel 파일 상세 정보 모델
     * @return 원본 이미지 컬렉션의 경로
     */
	public String getOriginalImageCollectionPath(T fileModel) {
		String BASE_PATH = getFileBasePath(fileModel);
		if (!BASE_PATH.endsWith(File.separator)) {
			BASE_PATH += File.separator;
		}
		String FOLDER_NAME = "IMAGE_COLLECTION";

		return BASE_PATH + FOLDER_NAME;
	}

	/**
     * 참석자가 판서한 회의자료의 이미지 컬렉션의 경로를 가져옵니다.
     *
     * @param fileModel 파일 상세 정보 모델
     * @return 참석자가 작성한 이미지의 경로
     */
	public String getAttendeeWrittenImagePath(T fileModel, String userId) {
		String BASE_PATH = getMeetingFileBasePath(fileModel);
		if (!BASE_PATH.endsWith(File.separator)) {
			BASE_PATH += File.separator;
		}
		String FOLDER_NAME = fileModel.getUuid();

		return BASE_PATH + "NOTE" + File.separator + FOLDER_NAME + File.separator + userId;
	}

	/**
     * 판서본 생성을 위한 통합 이미지 컬렉션의 경로를 가져옵니다.
     *
     * @param fileModel 파일 상세 정보 모델
     * @return 복사본을 위한 통합 이미지의 경로
     */
	public String getIntegratedImagePathForPrint(T fileModel) {
		String BASE_PATH = getFileBasePath(fileModel);
		if (!BASE_PATH.endsWith(File.separator)) {
			BASE_PATH += File.separator;
		}
		String FOLDER_NAME = fileModel.getUuid();

		return BASE_PATH + "PRINT" + File.separator + FOLDER_NAME;
	}
}
