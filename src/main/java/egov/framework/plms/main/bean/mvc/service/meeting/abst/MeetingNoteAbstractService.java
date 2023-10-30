package egov.framework.plms.main.bean.mvc.service.meeting.abst;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import egov.framework.plms.main.bean.mvc.entity.file.abst.FileDetailModelVO;
import egov.framework.plms.main.core.exception.ApiNotFoundException;
import egov.framework.plms.main.core.model.enums.error.ErrorCode;
import egov.framework.plms.main.core.util.FileCommUtil;
import egov.framework.plms.main.core.util.FileNameUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 회의 판서에 관한 추상 서비스 클래스입니다.
 * 이 클래스는 FileDetailModelVO 타입을 상속받은 VO 클래스를 통해 서비스를 제공합니다.
 * 실제 서비스 클래스는 이 추상 클래스를 상속받아 구현되며, 해당 VO 클래스를 통해 실제 파일 객체를 가져오기 위한 추상 메서드를 상속한 클래스에서 구현해야합니다.
 * 
 * @author mckim
 * @version 1.0
 * @since 2023. 5. 19
 */
@Slf4j
public abstract class MeetingNoteAbstractService<T extends FileDetailModelVO> {
	/**
	 * 회의자료 페이지 원본 이미지 조회
	 * @param fileModel
	 * @param pageNo
	 * @return
	 */
	public ResponseEntity<Resource> showNotePage(T fileModel, Integer pageNo) {
		// TODO Auto-generated method stub
		File SRC_FOLDER = getNoteSourceFolder(fileModel);
		String PAGE_PATH = SRC_FOLDER.getAbsolutePath() + File.separator + FileNameUtil.getPageNameFormat(pageNo);
		File PAGE_FILE = new File(PAGE_PATH + ".webp");
		if(!PAGE_FILE.exists()) {
			PAGE_FILE = new File(PAGE_PATH + ".png");
		}
		return getFileResponseEntity(PAGE_FILE);
	}
	
	/**
	 * 회의자료의 페이지별 이미지를 보관한 폴더 경로를 반환.<br>
	 * 회의자료가 이미지 파일인 경우 이미지를 WEBP로 변환하지만 별도의 이미지 파일 보관 폴더를 생성하지 않으므로 메서드 구현시 관련 로직을 추가해야함.
	 * 
	 * @param fileModel 원본 이미지 파일 모델
	 * @return 원본 이미지 파일이 저장된 폴더
	 * @throws ApiNotFoundException 원본 이미지 파일을 찾지 못한 경우 예외가 발생합니다.
	 */
	protected abstract File getNoteSourceFolder(T fileModel);
	/**
	 * 회의자료 페이지 사용자 이미지 조회
	 * @param fileModel
	 * @param pageNo
	 * @return
	 */
	public ResponseEntity<Resource> showNotePage(T fileModel, String userId, Integer pageNo) {
		File DRAW_FOLDER = getNoteDrawFolder(fileModel, userId);
		if(!DRAW_FOLDER.exists()) {
			DRAW_FOLDER.mkdirs();
		}
		File PAGE_FILE = getPageFile(DRAW_FOLDER, pageNo);
		if(PAGE_FILE == null) {
			return showNotePage(fileModel, pageNo);
		}
		return getFileResponseEntity(PAGE_FILE);
	}
	
	protected abstract File getNoteDrawFolder(T fileModel, String userId);

	public boolean drawNotePage(T fileModel, String userId, Integer pageNo,
			String image) {
		boolean result = true;
		File DRAW_FOLDER = getNoteDrawFolder(fileModel, userId);
		if(!DRAW_FOLDER.exists()) {
			DRAW_FOLDER.mkdirs();
		}
		if(image == null) {
			File PAGE_FILE = getPageFile(DRAW_FOLDER, pageNo);
			if(!PAGE_FILE.exists()) {
				throw new ApiNotFoundException(ErrorCode.FILE.NOT_FOUND);
			}
			try {
				FileUtils.delete(PAGE_FILE);
			} catch (Exception e) {
				result = false;
				log.error(e.getMessage());
			}
		}else {
			image = image.replaceAll("\\\"","");
			byte[] decodedImage = Base64.getDecoder().decode(image);
			FileOutputStream fileStream = null;
			OutputStream bufferedStream = null;
			try {
				String IMAGE_PATH = DRAW_FOLDER.getPath() + File.separator + FileNameUtil.getPageNameFormat(pageNo) + ".png";
				File IMAGE = new File(IMAGE_PATH);
				fileStream = new FileOutputStream(IMAGE);
				bufferedStream = new BufferedOutputStream(fileStream);
				bufferedStream.write(decodedImage);
		    } catch (IOException e) {
		        log.error(e.getMessage());
		    } finally {
		    	try {
					bufferedStream.close();
				} catch (IOException e) {
					log.error(e.getMessage());
				}
		    	try {
		    		fileStream.close();
				} catch (IOException e) {
					log.error(e.getMessage());
				}
		    }
		}
		return result;
	}
	
	private File getPageFile(File FOLDER, Integer pageNo) {
		File[] FOLDER_IMAGE_LIST = FOLDER.listFiles();
		File PAGE_FILE = null;
		String PAGE_NAME = FileNameUtil.getPageNameFormat(pageNo) + ".png";
		if(FOLDER_IMAGE_LIST != null) {
			for(File IMAGE : FOLDER_IMAGE_LIST) {
				String imgName = IMAGE.getName();
				if(imgName.equals(PAGE_NAME)) {
					PAGE_FILE = IMAGE;
				}
			}
		}
		return PAGE_FILE;
	}

	public Integer getMemoPageLength(Integer meetingId, String userId) {
		File MEMO_FOLDER = getMemoFolder(meetingId, userId);
		File[] pages = MEMO_FOLDER.listFiles();
		if(pages == null || pages.length == 0) {
			addMemoPage(MEMO_FOLDER, 1);
			pages = MEMO_FOLDER.listFiles();
		}
		return pages.length;
	}
	
	public ResponseEntity<Resource> showMemoPage() {
		ResponseEntity<Resource> entity = null;
		ClassPathResource resource = new ClassPathResource("/img/note.png");
		try {
			File MEMO_SOURCE_FILE = resource.getFile();
			entity = FileCommUtil.getImageFileResource(MEMO_SOURCE_FILE);
		} catch (IOException e) {
			e.printStackTrace();
			entity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return entity;
	}

	public ResponseEntity<Resource> showMemoPage(Integer meetingId, String userId, Integer pageNo) {
		// TODO Auto-generated method stub
		File MEMO_FOLDER = getMemoFolder(meetingId, userId);
		File PAGE_FILE = getPageFile(MEMO_FOLDER, pageNo);
		return getFileResponseEntity(PAGE_FILE);
	}
	
	public boolean addMemoPage(Integer meetingId, String userId, Integer pageNo) {
		File MEMO_FOLDER = getMemoFolder(meetingId, userId);
		File[] pages = MEMO_FOLDER.listFiles();
		Integer nowPageLength = (pages != null)?pages.length:0;
		if(nowPageLength >= pageNo) {
			for(Integer i = nowPageLength; i >= pageNo; i--) {
				File page = pages[i - 1];
				Path newPath = Paths.get(MEMO_FOLDER.getAbsolutePath(), FileNameUtil.getPageNameFormat(i+1) + ".png");
				try {
					Files.move(page.toPath(), newPath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return addMemoPage(MEMO_FOLDER, pageNo);
	}
	
	private boolean addMemoPage(File MEMO_FOLDER, Integer pageNo) {
		ClassPathResource resource = new ClassPathResource("/img/note.png");
		boolean result = true;
		try {
			File MEMO_FILE = resource.getFile();
			FileCommUtil.copyFile(MEMO_FOLDER.getAbsolutePath(), FileNameUtil.getPageNameFormat(pageNo) + ".png", MEMO_FILE);
		} catch (IOException e) {
			result = false;
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean drawMemoPage(Integer meetingId, String userId, Integer pageNo,
			String image) {
		boolean result = true;
		File MEMO_FOLDER = getMemoFolder(meetingId, userId);
		if(image == null) {
			addMemoPage(MEMO_FOLDER, pageNo);
		}else {
			image = image.replaceAll("\\\"","");
			byte[] decodedImage = Base64.getDecoder().decode(image);
			FileOutputStream fileStream = null;
			OutputStream bufferedStream = null;
			try {
				String IMAGE_PATH = MEMO_FOLDER.getPath() + File.separator + FileNameUtil.getPageNameFormat(pageNo) + ".png";
				File IMAGE = new File(IMAGE_PATH);
				fileStream = new FileOutputStream(IMAGE);
				bufferedStream = new BufferedOutputStream(fileStream);
				bufferedStream.write(decodedImage);
		    } catch (IOException e) {
		        log.error(e.getMessage());
		    } finally {
		    	try {
					bufferedStream.close();
				} catch (IOException e) {
					log.error(e.getMessage());
				}
		    	try {
		    		fileStream.close();
				} catch (IOException e) {
					log.error(e.getMessage());
				}
		    }
		}
		return result;
	}
	
	public boolean deleteMemoPage(Integer meetingId, String userId, Integer pageNo) {
		boolean result = true;
		File MEMO_FOLDER = getMemoFolder(meetingId, userId);
		File PAGE_FILE = getPageFile(MEMO_FOLDER, pageNo);
		if(!PAGE_FILE.exists()) {
			throw new ApiNotFoundException(ErrorCode.FILE.NOT_FOUND);
		}
		try {
			File[] MEMO_PAGE_LIST = MEMO_FOLDER.listFiles();
			Integer MEMO_PAGE_LENGTH = (MEMO_PAGE_LIST != null)?MEMO_PAGE_LIST.length:0;
			FileUtils.delete(PAGE_FILE);
			for(Integer i = pageNo + 1; i <= MEMO_PAGE_LENGTH; i++) {
				File page = MEMO_PAGE_LIST[i - 1];
				Path newPath = Paths.get(MEMO_FOLDER.getAbsolutePath(), FileNameUtil.getPageNameFormat(i-1) + ".png");
				try {
					Files.move(page.toPath(), newPath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			result = false;
			log.error(e.getMessage());
		}
		return result;
	}
	
	protected abstract File getMemoFolder(Integer meetingId, String userId);

	private ResponseEntity<Resource> getFileResponseEntity(File FILE){
		ResponseEntity<Resource> entity = null;
		try {
		    entity = FileCommUtil.getImageFileResource(FILE);
		} catch (FileNotFoundException e) {
			entity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			log.error(e.getMessage());
		} catch (IOException e) {
			entity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			log.error(e.getMessage());
		} 
		return entity;
	}
}
