package egov.framework.plms.main.bean.mvc.controller.file;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import egov.framework.plms.main.bean.component.properties.FileConfigProperties;
import egov.framework.plms.main.bean.component.properties.FileConfigProperties.ConversionServer;
import egov.framework.plms.main.bean.mvc.entity.file.FileConvertVO;
import egov.framework.plms.main.bean.mvc.entity.file.FileDetailDTO;
import egov.framework.plms.main.bean.mvc.entity.file.FileDetailVO;
import egov.framework.plms.main.bean.mvc.service.file.FileCvtService;
import egov.framework.plms.main.bean.mvc.service.file.FileDiskService;
import egov.framework.plms.main.bean.mvc.service.file.abst.FileInfoAbstractService;
import egov.framework.plms.main.core.model.enums.file.ConversionStatus;
import egov.framework.plms.main.core.model.enums.file.ConversionStep;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import egov.framework.plms.main.core.util.CommUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileRestController {
	private final FileInfoAbstractService<FileDetailVO> fileServ;
	private final FileDiskService diskServ;
	private final FileConfigProperties fileProperties;
	private final FileCvtService driveCvtServ;
	
	@PostMapping(value = "/file/list")
	public ResponseMessage postFileList(Authentication authentication, @RequestParam("list") @Nullable List<MultipartFile> files){
		Set<Integer> successSet = files.stream().map(file -> {
			return fileServ.uploadFile(file).map(FileDetailVO::getFileId).orElse(null);
		}).filter(result -> result != null).collect(Collectors.toSet());
		return ResponseMessage.builder(ResponseMessage.StatusCode.OK).data(successSet).build();
	}
	
	/**
	 * 파일 정보 요청
	 * @param fileId : 정보를 요청할 파일 고유키
	 * @return
	 */
	@GetMapping(value = "/file/{fileId}")
	public FileDetailDTO getFileOne(@PathVariable Integer fileId) {
		return fileServ.selectFileOne(fileId).map(FileDetailVO::convert).orElse(null);
	}
	
	/**
	 * 파일 검색
	 * @param vo : 파일목록 검색할 조건
	 * @return : 파일목록
	 */
	@GetMapping(value = "/file/list")
	public List<FileDetailDTO> getFileListView(FileDetailDTO param) {
		List<FileDetailVO> voList = fileServ.selectFileList(Optional.ofNullable(param).map(FileDetailDTO::convert).orElseGet(()->null));
		return voList.stream().map(vo -> vo.convert()).collect(Collectors.toList());
	}
	
	/**
	 * 파일 다운로드 요청. HttpServletResponse를 통해 전송한다. FileService에 파리미터를 모두 넘겨 처리.
	 * @param request : 브라우저를 구분하기 위한 HttpServletRequest 객체
	 * @param response : 파일을 담아 전송할 HttpServletResponse 객체
	 * @param link : 다운로드 요청한 파일 링크
	 * @throws Exception 
	 */
	@GetMapping(value = "/file/{fileId}/download")
	public ResponseEntity<Resource> downloadFile(HttpServletRequest request, HttpServletResponse response, @PathVariable Integer fileId){
		FileDetailVO fileVO = fileServ.selectFileOne(fileId).orElse(null);
		File fileInstance = fileServ.createFileDownloadInstance(fileId).orElse(null);
		if(fileVO == null || fileInstance == null) {
			return new ResponseEntity<>(null, null, HttpStatus.NOT_FOUND);
		}
		String encodedFileName = CommUtil.getBrowserEncodedText(request, fileVO.getUploadedFileName());
		return diskServ.fileDownload(fileInstance, encodedFileName);
	}
	
	/**
	 * 파일 조회
	 * @param link : 조회할 파일 링크
	 * @return
	 * @throws IOException
	 */
	@GetMapping(value = "/file/{fileId}/view")
	public ResponseEntity<Resource> viewFile(Authentication authentication, @PathVariable Integer fileId) throws IOException{
		FileDetailVO fileVO = fileServ.selectFileOne(fileId).orElse(null);
		File fileInstance = fileServ.createFileViewInstance(fileId).orElse(null);
		if(fileVO == null || fileInstance == null) {
			return new ResponseEntity<>(null, null, HttpStatus.NOT_FOUND);
		}
		return diskServ.fileView(fileInstance);
	}
	
	@PutMapping(value = "/file-conversion/{trId}/{cvtStep}")
	public void updateFileConverstionStep(HttpServletRequest request, @PathVariable Integer trId, @PathVariable ConversionStep cvtStep) throws IOException{
		ConversionServer conversionServer = fileProperties.getConversionServer();
		if(!conversionServer.isEnabled()) {
			return;
		}
		if(!conversionServer.getBaseUrl().contains(CommUtil.getIp(request))){
			return;
		}
		Optional<FileConvertVO> cvtOpt = driveCvtServ.selectCvtOne(trId);
		if(cvtOpt.isPresent()) {
			FileConvertVO cvt = cvtOpt.get();
			driveCvtServ.updateCvtOne(FileConvertVO.builder().cvtId(trId).cvtStep(cvtStep).build());
			fileServ.updateFileOne(FileDetailVO.builder()
					.fileId(cvt.getFileId())
					.conversionStatus(ConversionStatus.COMPLETED)
					.pdfGeneratedYN('Y')
					.build());
		}
	}
}
