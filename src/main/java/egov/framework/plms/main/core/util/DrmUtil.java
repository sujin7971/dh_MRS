/**
 * 
 */
package egov.framework.plms.main.core.util;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasoo.fcwpkg.packager.WorkPackager;

import lombok.extern.slf4j.Slf4j;

//import egovframework.ewp.service.common.impl.CommonServiceImpl;
@Slf4j
public class DrmUtil {
	
	WorkPackager objWorkPackager = new WorkPackager();
	
	public int getFileTypeCode(File tmpFile) {
		int retVal = 0;
		// 복호화 된문서가 암호화된 문서를 덮어쓰지 않음
		objWorkPackager.setOverWriteFlag(false);
		String SourceFile = tmpFile.getAbsolutePath();
		retVal = objWorkPackager.GetFileType(SourceFile);
		
		log.debug("파일형태는 " + FileTypeStr(retVal) + "[" + retVal + "]"	+ " 입니다.");
		
		return retVal;
	}
	
	public boolean getFileType(File tmpFile) {
		boolean result = false;
		int retVal = 0;
		// 복호화 된문서가 암호화된 문서를 덮어쓰지 않음
		objWorkPackager.setOverWriteFlag(false);
		String SourceFile = tmpFile.getAbsolutePath();
		retVal = objWorkPackager.GetFileType(SourceFile);
		
		log.debug("파일형태는 " + FileTypeStr(retVal) + "[" + retVal + "]"	+ " 입니다.");
		
		if(retVal == 103){
			// 대상 문서가 FSN로 암호화 되었을 때만 복호화 실행
			result = true;
		}else {
			log.info("DRM 디코딩 파일이 아닙니다, 파일형태는 " + FileTypeStr(retVal) + "[" + retVal + "]"	+ " 입니다.");
		}
		return result;
	}
	
	private  static String FileTypeStr(int i) 
	{
		String ret = null;
		switch(i)
		{
	    	case 20 : ret = "파일을 찾을 수 없습니다."; break;
	    	case 21 : ret = "파일 사이즈가 0 입니다.";  break;
	    	case 22 : ret = "파일을 읽을 수 없습니다."; break;
	    	case 29 : ret = "암호화 파일이 아닙니다.";  break;
	    	case 26 : ret = "FSD 파일입니다.";       	break;
	    	case 105: ret = "Wrapsody 파일입니다.";  	break;
	    	case 106: ret = "NX 파일입니다.";			break;	    	
	    	case 101: ret = "MarkAny 파일입니다.";   	break;
	    	case 104: ret = "INCAPS 파일입니다.";    	break;
	    	case 103: ret = "FSN 파일입니다.";       	break;
		}
		return ret;		
	}

	public File DoExtract(File encrytFile, String keyPath, String key, String decryptFilePath) {
		boolean bret = false;
		//boolean nret = false;
		// 암호화 된 파일 복호화
		String encryptFilePath= encrytFile.getAbsolutePath();
//		objWorkPackager.SetLogInfo(40, "C:/");
		bret = objWorkPackager.DoExtract(keyPath, 			// fsdinit폴더 FullPath 설정
											key, 			// 고객사 Key(default)
											encryptFilePath, 	// 복호화 대상 문서 FullPath
											decryptFilePath 	// 복호화 된 문서 FullPath
										);

		log.debug("복호화 결과값 : " + bret);
		log.debug("복호화 문서 : "	+ objWorkPackager.getContainerFilePathName());
		log.debug("오류코드 : " + objWorkPackager.getLastErrorNum());
		log.debug("오류값 : "	+ objWorkPackager.getLastErrorStr());
		if(bret){
			return new File(decryptFilePath);
		}else{
			return null;
		}
	}
	
}
