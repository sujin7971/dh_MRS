package egov.framework.plms.sub.lime.bean.mvc.service.meeting;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.entity.file.FileDetailVO;
import egov.framework.plms.main.bean.mvc.service.meeting.abst.MeetingNoteAbstractService;
import egov.framework.plms.main.core.exception.ApiNotFoundException;
import egov.framework.plms.sub.lime.bean.component.file.LimeFileModelPathResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("lime")
public class LimeMeetingNoteService extends MeetingNoteAbstractService<FileDetailVO>{
	private final LimeFileModelPathResolver filePathResolver;
	
	/**
	 * 회의자료의 페이지별 이미지를 보관한 폴더 경로를 반환.<br>
	 * 이미지 파일 유형의 회의자료는 이미지를 보관한 폴더가 없는 경우, 해당 폴더 생성과 함께 원본 이미지 변환을 통해 생성된 webp파일을 해당 폴더에 0001.webp 이름으로 저장.
	 * 
	 * @param fileModel 원본 이미지 파일 모델
	 * @return 원본 이미지 파일이 저장된 폴더
	 * @throws ApiNotFoundException 원본 이미지 파일을 찾지 못한 경우 예외가 발생합니다.
	 */
	@Override
	protected File getNoteSourceFolder(FileDetailVO fileModel) throws ApiNotFoundException {
		String SRC_PATH = filePathResolver.getOriginalImageCollectionPath(fileModel);
		File SRC_FOLDER = new File(SRC_PATH);
		if(!SRC_FOLDER.exists()) {
			//이미지파일의 경우 원본 이미지를 WEBP로 변환후 따로 IMAGE_COLLECTION폴더를 생성하지 않음
			//WEBP 이미지 파일을 IMAGE_COLLECTION 폴더 추가와 함께 복사하고, 해당 이미지 파일 이름을 0001.webp로 변경
			String WEBP_PATH = filePathResolver.getConvertedFilePathForPreview(fileModel);
			File WEBP_FILE = new File(WEBP_PATH);
			try {
				FileUtils.copyFileToDirectory(WEBP_FILE, SRC_FOLDER);
				File copiedFile = new File(SRC_FOLDER, WEBP_FILE.getName());
				File renamedFile = new File(SRC_FOLDER, "0001.webp");
				if (!copiedFile.renameTo(renamedFile)) {
					throw new IOException("Failed to rename file " + copiedFile);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return SRC_FOLDER;
	}
	
	@Override
	protected File getNoteDrawFolder(FileDetailVO fileModel, String userId) throws ApiNotFoundException {
		String DRAW_PATH = filePathResolver.getAttendeeWrittenImagePath(fileModel, userId);
		File DRAW_FOLDER = new File(DRAW_PATH);
		return DRAW_FOLDER;
	}
	
	@Override
	protected File getMemoFolder(Integer meetingId, String userId) throws ApiNotFoundException {
		String MEMO_PATH = filePathResolver.getMeetingMemoImageCollectionPath(meetingId, userId);
		File MEMO_FOLDER = new File(MEMO_PATH);
		return MEMO_FOLDER;
	}

}
