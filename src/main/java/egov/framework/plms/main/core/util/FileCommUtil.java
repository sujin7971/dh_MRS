package egov.framework.plms.main.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import egov.framework.plms.main.core.model.enums.file.FileCategory;
import lombok.extern.slf4j.Slf4j;


/**
 * 파일에 대한 공통 로직을 모아둔 클래스
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
@Slf4j
public class FileCommUtil {
	
	public static FileCategory getFileCategory(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType != null) {
            contentType = contentType.toLowerCase();

            if (contentType.startsWith("image/")) {
                return FileCategory.IMG;
            } else if (contentType.equals("application/pdf")) {
                return FileCategory.PDF;
            } else if (contentType.equals("application/msword") || contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                return FileCategory.WORD;
            } else if (contentType.equals("application/vnd.ms-excel") || contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
                return FileCategory.EXCEL;
            } else if (contentType.equals("application/vnd.ms-powerpoint") || contentType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")) {
                return FileCategory.PPT;
            } else if (contentType.equals("application/x-hwp") || contentType.equals("application/haansofthwp")) {
                return FileCategory.HWP;
            } else if (contentType.startsWith("audio/")) {
            	return FileCategory.AUDIO;
            }
        }

        return FileCategory.UNKNOWN;
    }
	
	public static String getMimeType(MultipartFile file) {
		Tika tika = new Tika();
		String mimeType ="application/octet-stream";
        try {
			mimeType = tika.detect(file.getInputStream());
		} catch (IOException e) {
			return null;
		}
        return mimeType;
	}

	public static String getMimeType(File file) {
		Tika tika = new Tika();
		String mimeType ="application/octet-stream";
        try {
			mimeType = tika.detect(file);
		} catch (IOException e) {
			return null;
		}
        return mimeType;
	}
	/**
	 * 클라이언트가 업로드 요청한 MultipartFile을 서버에 저장.
	 * @param SAVE_FOLDER : 저장할 경로
	 * @param SAVE_NAME : 저장할 이름
	 * @param multipartFile : 저장할 파일
	 * @throws IOException
	 */
	public static void copyFile(String SAVE_FOLDER_PATH, String SAVE_NAME, MultipartFile multipartFile) throws IOException {
		File SAVE_FOLDER = new File(SAVE_FOLDER_PATH);
		if (!SAVE_FOLDER.exists()) {
			SAVE_FOLDER.mkdirs();
		}
		File file = new File(SAVE_FOLDER, SAVE_NAME);
		file.setWritable(true); //쓰기가능설정
		file.setReadable(true);	//읽기가능설정
		Files.copy(multipartFile.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
	
	public static void copyFile(String SAVE_FILE_NAME, MultipartFile multipartFile) throws IOException {
		File file = new File(SAVE_FILE_NAME);
		file.setWritable(true); //쓰기가능설정
		file.setReadable(true);	//읽기가능설정
		Files.copy(multipartFile.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
	
	public static void copyFile(String SAVE_FOLDER_PATH, String SAVE_NAME, File sourceFile) throws IOException {
		File SAVE_FOLDER = new File(SAVE_FOLDER_PATH);
		if (!SAVE_FOLDER.exists()) {
			SAVE_FOLDER.mkdirs();
		}
		File file = new File(SAVE_FOLDER, SAVE_NAME);
		file.setWritable(true); //쓰기가능설정
		file.setReadable(true);	//읽기가능설정
		Files.copy(sourceFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
	
	public static void copyFile(String SAVE_FILE_NAME, File sourceFile) throws IOException {
		File file = new File(SAVE_FILE_NAME);
		file.setWritable(true); //쓰기가능설정
		file.setReadable(true);	//읽기가능설정
		Files.copy(sourceFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
	
	public static void copyFilesWithoutOverwriting(File srcDir, File destDir) throws IOException {
	    if (srcDir.isDirectory()) {
	        if (!destDir.exists()) {
	            destDir.mkdir();
	        }
	        String[] children = srcDir.list();
	        for (int i = 0; i < children.length; i++) {
	            copyFilesWithoutOverwriting(new File(srcDir, children[i]),
	                    new File(destDir, children[i]));
	        }
	    } else {
	        if (!destDir.exists()) {
	            FileUtils.copyFile(srcDir, destDir);
	        }
	    }
	}
	
	public static File convert(MultipartFile file) throws IOException
	{    
	    File convFile = new File(file.getOriginalFilename());
	    convFile.createNewFile(); 
	    FileOutputStream fos = new FileOutputStream(convFile); 
	    fos.write(file.getBytes());
	    fos.close(); 
	    return convFile;
	}
	
	public static ResponseEntity<Resource> getFileResource(File file) {
		try {
			Path path = file.toPath();
			log.info("경로 요청: {}", path.toString());
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.parseMediaType(FileCommUtil.getMimeType(file)));
			headers.setContentDisposition(ContentDisposition.builder("inline").build());
			headers.setContentLength(file.length());
	
			Resource resource = new InputStreamResource(Files.newInputStream(path));
			return new ResponseEntity<>(resource, headers, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
		}
	}
	
	public static ResponseEntity<Resource> getFileDownloadResource(File file) {
		return getFileDownloadResource(file, file.getName());
	}
	
	public static ResponseEntity<Resource> getFileDownloadResource(File file, String downloadName) {
		try {
			Path path = file.toPath();
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.parseMediaType(FileCommUtil.getMimeType(file)));
			headers.setContentDisposition(ContentDisposition.builder("attachment").filename(downloadName).build());
			headers.setContentLength(file.length());
			
			Resource resource = new InputStreamResource(Files.newInputStream(path));
			return new ResponseEntity<>(resource, headers, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
		}
	}
	
	public static ResponseEntity<Resource> getImageFileResource(File file) throws IOException {
		Path path = file.toPath();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "image/png");
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline");

		Resource resource = new InputStreamResource(Files.newInputStream(path));
		return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	}
	
	public static ResponseEntity<Resource> getPdfFileResource(File file) throws IOException {
		Path path = file.toPath();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline");

		Resource resource = new InputStreamResource(Files.newInputStream(path));
		return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	}
	
	public static ResponseEntity<byte[]> getImageFileByteResource(File file) throws IOException {
	    Path path = file.toPath();
	    
	    HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.CONTENT_TYPE, "image/png");
	    headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline");

	    byte[] imageData = Files.readAllBytes(path);
	    return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
	}
	
}
