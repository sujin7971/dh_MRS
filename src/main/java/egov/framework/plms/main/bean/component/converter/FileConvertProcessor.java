package egov.framework.plms.main.bean.component.converter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.component.properties.FileConfigProperties;
import egov.framework.plms.main.core.util.FileNameUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
/**
 * <p>업로드한 문서 파일을 페이지별로 이미지로 변환</p>
 * 저장 경로 : 회의등록년/월/회의번호/저장파일명/COPY
 * 
 * 오피스 변환 명령어 : [변환기 경로] [변환할 파일 경로] [저장할 파일 경로]
 * 한글 변환 명령어 : [변환기 경로] [변환할 파일 경로] [저장할 폴더 경로]
 * 
 * 각각의 변환 명령어는 큰 따옴표로 묶여야 함.
 * 예: "...\PptxToPdfConverter.exe ...\test.pptx ...\result.pdf"
 * @author mckim
 *
 */
public class FileConvertProcessor {
	private final FileConfigProperties fileProperties;
	private final ServletContext context;
	private String REAL_PATH;//webapp경로
	@PostConstruct
	private void init() {
		REAL_PATH = context.getRealPath("/");
	}
	/**
	 * HWP -> PDF
	 * @param REAL_PATH 프로그램의 실제 경로 위치
	 * @param SOURCE_PATH PDF로 변환될 파일이 위치한 폴더 경로
	 * @param DESTINATION_PATH PDF로 변환될 파일 이름
	 * @return 변환 성공시 <code>true<code>, 실패시 <code>false<code>
	 * 
	 */
	@Async("hwpExecutor")
	public CompletableFuture<Integer> convertHwpToPdf(String REAL_PATH, String SOURCE_PATH, String DESTINATION_PATH) {
		String CVT_PATH = REAL_PATH + fileProperties.getCvt().getHwp();
		Integer result = convertOfficeToPdf(CVT_PATH, SOURCE_PATH, DESTINATION_PATH);
		return CompletableFuture.completedFuture(result);
	}
	
	@Async("hwpExecutor")
	public CompletableFuture<Integer> convertHwpToPdf(String REAL_PATH, String TARGET_FOLDER, String TARGET_NAME, String CONVERTED_FOLDER, String CONVERTED_NAME) {
		String CVT_PATH = REAL_PATH + fileProperties.getCvt().getHwp();
		Integer result = convertOfficeToPdf(CVT_PATH, TARGET_FOLDER, TARGET_NAME, CONVERTED_FOLDER, CONVERTED_NAME);
		return CompletableFuture.completedFuture(result);
	}
	
	/**
	 * WORD -> PDF
	 * @param REAL_PATH 프로그램의 실제 경로 위치
	 * @param SOURCE_PATH PDF로 변환될 파일이 위치한 폴더 경로
	 * @param DESTINATION_PATH PDF로 변환될 파일 이름
	 * @return 변환 성공시 <code>true<code>, 실패시 <code>false<code>
	 * 
	 */
	@Async("wordExecutor")
	public CompletableFuture<Integer> convertWordToPdf(String REAL_PATH, String SOURCE_PATH, String DESTINATION_PATH) {
		String CVT_PATH = REAL_PATH + fileProperties.getCvt().getWord();
		Integer result = convertOfficeToPdf(CVT_PATH, SOURCE_PATH, DESTINATION_PATH);
		return CompletableFuture.completedFuture(result);
	}
	@Async("wordExecutor")
	public CompletableFuture<Integer> convertWordToPdf(String REAL_PATH, String TARGET_FOLDER, String TARGET_NAME, String CONVERTED_FOLDER, String CONVERTED_NAME) {
		String CVT_PATH = REAL_PATH + fileProperties.getCvt().getWord();
		Integer result = convertOfficeToPdf(CVT_PATH, TARGET_FOLDER, TARGET_NAME, CONVERTED_FOLDER, CONVERTED_NAME);
		return CompletableFuture.completedFuture(result);
	}
	
	/**
	 * PPT -> PDF
	 * @param REAL_PATH 프로그램의 실제 경로 위치
	 * @param SOURCE_PATH PDF로 변환될 파일이 위치한 폴더 경로
	 * @param DESTINATION_PATH PDF로 변환될 파일 이름
	 * @return 변환 성공시 <code>true<code>, 실패시 <code>false<code>
	 * 
	 */
	@Async("pptExecutor")
	public CompletableFuture<Integer> convertPptToPdf(String REAL_PATH, String SOURCE_PATH, String DESTINATION_PATH) {
		String CVT_PATH = REAL_PATH + fileProperties.getCvt().getPpt();
		log.info("PPT -> PDF 변환 - , 파일명: {}", DESTINATION_PATH);
		Integer result = convertOfficeToPdf(CVT_PATH, SOURCE_PATH, DESTINATION_PATH);
		return CompletableFuture.completedFuture(result);
	}
	
	@Async("pptExecutor")
	public CompletableFuture<Integer> convertPptToPdf(String REAL_PATH, String TARGET_FOLDER, String TARGET_NAME, String CONVERTED_FOLDER, String CONVERTED_NAME) {
		String CVT_PATH = REAL_PATH + fileProperties.getCvt().getPpt();
		Integer result = convertOfficeToPdf(CVT_PATH, TARGET_FOLDER, TARGET_NAME, CONVERTED_FOLDER, CONVERTED_NAME);
		return CompletableFuture.completedFuture(result);
	}
	
	/**
	 * EXCEL -> PDF
	 * @param REAL_PATH 프로그램의 실제 경로 위치
	 * @param SOURCE_PATH PDF로 변환될 파일이 위치한 폴더 경로
	 * @param DESTINATION_PATH PDF로 변환될 파일 이름
	 * @return 변환 성공시 <code>true<code>, 실패시 <code>false<code>
	 * 
	 */
	@Async("excelExecutor")
	public CompletableFuture<Integer> convertExcelToPdf(String REAL_PATH, String SOURCE_PATH, String DESTINATION_PATH) {
		String CVT_PATH = REAL_PATH + fileProperties.getCvt().getExcel();
		Integer result = convertOfficeToPdf(CVT_PATH, SOURCE_PATH, DESTINATION_PATH);
		return CompletableFuture.completedFuture(result);
	}
	
	@Async("excelExecutor")
	public CompletableFuture<Integer> convertExcelToPdf(String REAL_PATH, String TARGET_FOLDER, String TARGET_NAME, String CONVERTED_FOLDER, String CONVERTED_NAME) {
		String CVT_PATH = REAL_PATH + fileProperties.getCvt().getExcel();
		Integer result = convertOfficeToPdf(CVT_PATH, TARGET_FOLDER, TARGET_NAME, CONVERTED_FOLDER, CONVERTED_NAME);
		return CompletableFuture.completedFuture(result);
	}
	
	private Integer convertOfficeToPdf(String CVT_PATH, String SOURCE_FOLDER, String SOURCE_NAME, String DESTINATION_FOLDER, String DESTINATION_NAME) {
		String SOURCE_PATH = SOURCE_FOLDER + File.separator + SOURCE_NAME;
		String DESTINATION_PATH = DESTINATION_FOLDER + File.separator + DESTINATION_NAME;
		return convertOfficeToPdf(CVT_PATH, SOURCE_PATH, DESTINATION_PATH);
	}
	
	private Integer convertOfficeToPdf(String CVT_PATH, String SOURCE_PATH, String DESTINATION_PATH) {
		String SOURCE_PATH_ARG = "\""+SOURCE_PATH+"\"";
		String DESTINATION_PATH_ARG = "\""+DESTINATION_PATH+"\"";
		File PDF = new File(DESTINATION_PATH);
		if(PDF.exists()) {
			log.debug("convertOfficeToPdf process result : success");
			return getPdfFilePages(PDF);
		}
		log.info("오피스 -> PDF 변환 - 원본 파일: {}, 변환 저장 경로: {}", CVT_PATH, DESTINATION_PATH);
		
		//String[] cmdArray = new String[] {CVT_PATH, "/word_no_field_update", orgFilePath, pdfLocation};
		String[] cmdArray = new String[] {CVT_PATH, SOURCE_PATH_ARG, DESTINATION_PATH_ARG};
		ProcessExecutor procExec = ProcessExecutor.builder().commandArray(cmdArray).build();
		Integer result = procExec.run();
		PDF = new File(DESTINATION_PATH);
		if(PDF.exists()) {
			log.debug("convertOfficeToPdf process result : success");
			return getPdfFilePages(PDF);
		}else {
			log.debug("convertOfficeToPdf process result : fail");
			return null;
		}
	}
	
	private Integer getPdfFilePages(File file) {
		PDDocument document;
		try {
			document = PDDocument.load(file);
			int numberOfPages = document.getNumberOfPages();
			document.close();
			return numberOfPages;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * PDF -> 이미지 파일
	 * @param SOURCE_PATH PDF 파일 경로
	 * @param DESTINATION_PATH 이미지 파일이 위치할 폴더 경로
	 * @param IMAGE_DPI 생성할 이미지 해상도
	 * @return 성공시 변환을 통해 생성된 이미지 파일 개수, 실패시 0
	 */
	@Async("pdfExecutor")
	public CompletableFuture<Integer> convertPdfToImages(String SOURCE_PATH, String DESTINATION_PATH, Integer IMAGE_DPI) {
		
		try {
			log.info("PDF -> IMG 변환(해상도: {}) - 원본 파일: {}, 변환 저장 경로: {}", IMAGE_DPI, SOURCE_PATH, DESTINATION_PATH);
			File SOURCE_FILE = new File(SOURCE_PATH);
			File DESTINATION_FOLDER = new File(DESTINATION_PATH);
			if (SOURCE_FILE.exists()) {
				if (!DESTINATION_FOLDER.exists()) {
					DESTINATION_FOLDER.mkdirs();
					log.debug("Folder Created -> {}", DESTINATION_FOLDER.getAbsolutePath());
				}
				ExecutorService exec = Executors.newFixedThreadPool(1);
				List<Future<?>> pending = new ArrayList<>();
				log.debug("Images copied to Folder Location: {}", DESTINATION_FOLDER.getAbsolutePath());
				PDDocument document = PDDocument.load(SOURCE_FILE);
				PDFRenderer pdfRenderer = new PDFRenderer(document);

				int numberOfPages = document.getNumberOfPages();
				log.debug("Total files to be converting -> {}", numberOfPages);

				for (int i = 0; i < numberOfPages; ++i) {
					Integer nowPage = i + 1;
					String imgName = FileNameUtil.getPageNameFormat(nowPage);
					String imgPath = DESTINATION_PATH + File.separator + imgName + ".png";
					BufferedImage bImage = pdfRenderer.renderImageWithDPI(i, IMAGE_DPI, ImageType.RGB);
					ImageIOUtil.writeImage(bImage, imgPath, IMAGE_DPI);
					//pending.add(exec.submit(() -> writeImage(bImage, imgPath, IMAGE_DPI)));
				}
				document.close();
				for(Future<?> fut: pending) {
					fut.get();
				}
				exec.shutdown();
				exec.awaitTermination(10, TimeUnit.MINUTES);
				return CompletableFuture.completedFuture(numberOfPages);
			} else {
				log.debug("변환할 pdf 파일  [ {} ] 이 존재하지 않습니다.",SOURCE_PATH);
				return CompletableFuture.completedFuture(0);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			return CompletableFuture.completedFuture(0);
		}
	}
	
	private void writeImage(BufferedImage bImage, String imgPath, Integer IMAGE_DPI) {
		try {
			ImageIOUtil.writeImage(bImage, imgPath, IMAGE_DPI);
		}catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	/**
	 * 판서한 이미지 파일 판서본 PDF 파일로 변환
	 * @param FILE_LIST 변환할 판서 이미지 리스트
	 * @param COPY_PATH 판서본을 저장할 경로(C:/B-PLMS/uploadFile/년/월/회의번호/COPY)
	 * @param FILE_NAME 판서본 이름
	 * @return
	 */
	@Async("pdfExecutor")
    public CompletableFuture<Boolean> convertImagesToPdf(File[] FILE_LIST, String COPY_PATH, String FILE_NAME) {
		log.debug("convertImagesToPdf COPY_PATH : {}", COPY_PATH);
		log.debug("convertImagesToPdf FILE_NAME : {}", FILE_NAME);
		
		log.debug("convertImagesToPdf FILE_LENGTH: {}, FILE_LIST : {}", FILE_LIST.length, FILE_LIST.toString());
		File pdfFile = null;
		PDDocument pdf = null;
        try {
	        // 문서 불러오기
	        pdfFile = createPdf(COPY_PATH, FilenameUtils.getBaseName(FILE_NAME));
	        pdf = PDDocument.load(pdfFile);
	
	       
	        for(File imgFile : FILE_LIST) {
	        	BufferedImage image = ImageIO.read(imgFile);
	            float imgWidth = image.getWidth(null);
	            float imgHeigth = image.getHeight(null);
	            image.flush();
	            PDImageXObject pdImage = LosslessFactory.createFromImage(pdf, image);
	            /*
	            PDImageXObject pdImage = PDImageXObject.createFromFileByContent(imgFile, pdf);
	            */
	            PDRectangle newRect = new PDRectangle(0, 0, imgWidth, imgHeigth);
	            PDPage newPage = new PDPage(newRect);
	            pdf.addPage(newPage);
	
	            PDPageContentStream contents = new PDPageContentStream(pdf, newPage);
	            contents.drawImage(pdImage, 0, 0, imgWidth, imgHeigth);
	            contents.close();
	        }
	
	        pdf.save(pdfFile);
	
	        // 문서 닫기
	        pdf.close();
	        return CompletableFuture.completedFuture(true);
        }catch(Exception e) {
        	e.printStackTrace();
        	if(pdf != null) {
        		try {
					pdf.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        	}
        	if(pdfFile != null) {
        		pdfFile.delete();
        	}
        	return CompletableFuture.completedFuture(false);
        }
    }
	
	/**
	 * 이미지 파일들을 PDF 파일로 변환하는 메서드.
	 * @param SOURCE_PATH 이미지 파일(들)이 위치한 경로. 단일 이미지 파일 또는 폴더 경로가 가능하다.
	 * @param DESTINATION_PATH PDF 파일이 저장될 경로. 경로에는 PDF파일의 이름이 포함되어야 한다.
	 * @return CompletableFuture<Boolean> 이미지를 PDF로 변환하는 결과. 성공 시 true, 실패 시 false 반환.
	 * <p>
	 * 참고: 아파치 PDFBox의 PDDocument.save() 메서드는 입력으로 제공된 파일 경로에 .pdf 확장자를 자동으로 추가하므로, 아래 메서드를 수정시 숙지할 필요가 있음.
	 * </p>
	 */
	@Async("pdfExecutor")
    public CompletableFuture<Boolean> convertImagesToPdf(String SOURCE_PATH, String DESTINATION_PATH) {
		log.debug("convertImagesToPdf - SOURCE_PATH : {}, DESTINATION_PATH: {}, isDeleteSource: {}", SOURCE_PATH, DESTINATION_PATH);
		List<File> FILE_LIST = new ArrayList<>();
		File SOURCE_FILE = new File(SOURCE_PATH);
		if(!SOURCE_FILE.exists()) {
			log.debug("convertImagesToPdf - 변환실패: 원본 파일이 존재하지 않음");
			return CompletableFuture.completedFuture(false);
		}else if(SOURCE_FILE.isDirectory()) {
			File[] files = SOURCE_FILE.listFiles();
		    if (files != null) {
		        FILE_LIST.addAll(Arrays.asList(files));
		    }else {
		    	log.debug("convertImagesToPdf - 변환실패: 원본 디렉토리가 비어있거나 읽기 권한이 없음");
				return CompletableFuture.completedFuture(false);
		    }
		}else {
			FILE_LIST.add(SOURCE_FILE);
		}
		File pdfFile = null;
		PDDocument pdf = null;
        try {
        	File DESTINATION_FILE = new File(DESTINATION_PATH);
	        // 문서 불러오기
	        pdfFile = createPdf(DESTINATION_FILE.getParent(), FilenameUtils.getBaseName(DESTINATION_FILE.getName()));
	        pdf = PDDocument.load(pdfFile);
	        log.debug("convertImagesToPdf FILE_LENGTH: {}, FILE_LIST : {}", FILE_LIST.size(), FILE_LIST.toString());
	        for(File imgFile : FILE_LIST) {
	        	BufferedImage image = ImageIO.read(imgFile);
	            float imgWidth = image.getWidth(null);
	            float imgHeigth = image.getHeight(null);
	            image.flush();
	            PDImageXObject pdImage = LosslessFactory.createFromImage(pdf, image);
	            /*
	            PDImageXObject pdImage = PDImageXObject.createFromFileByContent(imgFile, pdf);
	            */
	            PDRectangle newRect = new PDRectangle(0, 0, imgWidth, imgHeigth);
	            PDPage newPage = new PDPage(newRect);
	            pdf.addPage(newPage);
	
	            PDPageContentStream contents = new PDPageContentStream(pdf, newPage);
	            contents.drawImage(pdImage, 0, 0, imgWidth, imgHeigth);
	            contents.close();
	        }
	
	        pdf.save(pdfFile);
	
	        // 문서 닫기
	        pdf.close();
	        return CompletableFuture.completedFuture(true);
        }catch(Exception e) {
        	e.printStackTrace();
        	if(pdf != null) {
        		try {
					pdf.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        	}
        	if(pdfFile != null) {
        		pdfFile.delete();
        	}
        	return CompletableFuture.completedFuture(false);
        }
    }

    private File createPdf(String dirPath, String fileName) throws Exception {

        // 경로 체크
        File dir = new File(dirPath);
        if(!dir.exists()) {
            dir.mkdirs();
        }

        if(!dir.isDirectory()) {
            throw new Exception("올바른 경로가 아닙니다.");
        }

        File pdfFile = new File(dirPath + File.separator + fileName + ".pdf");
        if(pdfFile.exists()) {
            pdfFile.delete();
        }

        PDDocument document = new PDDocument();
        document.save(pdfFile);
        document.close();

        return pdfFile;
    }

	private String getArgsString(String text) {
		return "\""+text+"\"";
	}
	
	@Async("cvtExecutor")
	public CompletableFuture<Boolean> convertImagesToWebp(String REAL_PATH, String SOURCE_PATH, String DESTINATION_PATH, boolean deleteImages) {
		if (!fileProperties.getCvt().getWebp().isEnabled()) {
	        return CompletableFuture.completedFuture(true);
	    }
	    Integer quality = fileProperties.getCvt().getWebp().getQuality();
	    try {
	        String CVT_PATH = REAL_PATH + fileProperties.getCvt().getWebp().getEncoder();
	        String[] convertCommandArray;
	        if (Files.isDirectory(Paths.get(SOURCE_PATH))) {
	            // 폴더인 경우
	            convertCommandArray = new String[]{"cmd.exe", "/C", "for", "%i", "in", "(" + getArgsString(SOURCE_PATH + File.separator + "*.png") + ")", "do", getArgsString(CVT_PATH), "-q", quality.toString(), "-resize", "1920", "0", getArgsString(SOURCE_PATH + File.separator + "%~ni.png"), "-o", getArgsString(DESTINATION_PATH + File.separator + "%~ni.webp")};
	        } else {
	            // 단일 이미지 파일인 경우
	            convertCommandArray = new String[]{getArgsString(CVT_PATH), "-q", quality.toString(), "-resize", "1920", "0", getArgsString(SOURCE_PATH), "-o", getArgsString(DESTINATION_PATH)};
	        }

	        if (deleteImages) {
	            String[] deleteCommandArray;
	            if (Files.isDirectory(Paths.get(SOURCE_PATH))) {
	                deleteCommandArray = new String[]{"del", "/f", "/q", getArgsString(DESTINATION_PATH + File.separator + "%~ni.png")};
	            } else {
	                deleteCommandArray = new String[]{"del", "/f", "/q", getArgsString(DESTINATION_PATH)};
	            }
	            convertCommandArray = ArrayUtils.add(convertCommandArray, "&&");
	            convertCommandArray = ArrayUtils.addAll(convertCommandArray, deleteCommandArray);
	        }

	        log.info("PNG -> WEBP 파일 변환 - 명령어: {}", Arrays.asList(convertCommandArray).toString());
	        ProcessExecutor procExec = ProcessExecutor.builder().commandArray(convertCommandArray).build();
	        Integer result = procExec.run();
	        return CompletableFuture.completedFuture(true);
	    } catch (Exception e) {
	        log.error(e.getMessage());
	        return CompletableFuture.completedFuture(false);
	    }
	}
	
	@Async("cvtExecutor")
	public CompletableFuture<Boolean> convertImagesToWebp(String REAL_PATH, String SOURCE_FOLDER, String SOURCE_NAME, String DESTINATION_PATH, boolean deleteImages) {
		String SOURCE_PATH = SOURCE_FOLDER+File.separator+SOURCE_NAME;
		return convertImagesToWebp(REAL_PATH, SOURCE_PATH, DESTINATION_PATH, deleteImages);
	}
	
	@Async("cvtExecutor")
	public CompletableFuture<Boolean> convertWebpToImages(String REAL_PATH, String SOURCE_PATH) {
		if(!fileProperties.getCvt().getWebp().isEnabled()) {
			return CompletableFuture.completedFuture(true);
		}
		try {
			String CVT_PATH = REAL_PATH + fileProperties.getCvt().getWebp().getDecoder();
			String[] cvtCmdArray = new String[] {"cmd.exe", "/C", "for", "%i", "in", "("+getArgsString(SOURCE_PATH+File.separator+"*.webp")+")", "do", getArgsString(CVT_PATH), getArgsString(SOURCE_PATH+File.separator+"%~ni.webp"), "-o", getArgsString(SOURCE_PATH+File.separator+"%~ni.tiff")};
			String[] delCmdArray = new String[] {"del", "/f", "/q", getArgsString(SOURCE_PATH+File.separator+"%~ni.webp")};
			String[] cmdArray = ArrayUtils.add(cvtCmdArray, "&&");
			cmdArray = ArrayUtils.addAll(cmdArray, delCmdArray);
			log.info("PNG -> WEBP 파일 변환 - 명령어: {}", Arrays.asList(cmdArray).toString());
			ProcessExecutor procExec = ProcessExecutor.builder().commandArray(cmdArray).build();
			Integer result = procExec.run();
			return CompletableFuture.completedFuture(true);
		} catch (Exception e) {
			log.error(e.getMessage());
			return CompletableFuture.completedFuture(false);
		}
	}
	

}
