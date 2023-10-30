package egov.framework.plms.main.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;
//https://github.com/srikanth-lingala/zip4j

/**
 * 파일 압축 로직을 지원할 클래스. 현재 사용 안함
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
@Slf4j
@Deprecated
public class ZipUtil {
    public static void zipFile() throws IOException {
        Date now = new Date();
        String nowTime = now.toString();
        
        // 압축 시작
        System.out.println("zip encoding start -------------------------------" + nowTime);
    	
        FileObject[] files = new FileObject[2];
        // file 1
        Path path1 = Paths.get("C:\\test.pptx");  //소스파일
        InputStream is1 = Files.newInputStream(path1);
        FileObject file1 = new FileObject(path1.getFileName().toString(), is1);

        // file 2
        Path path2 = Paths.get("C:\\test1.pptx");  //소스파일
        InputStream is2 = Files.newInputStream(path2);
        FileObject file2 = new FileObject(path2.getFileName().toString(), is2);

        // add
        files[0] = file1;
        files[1] = file2;

        // 비밀번호
        String password = "password";

        // 저장될 ZIP 파일의 위치
        Path zipPath = Paths.get("C:\\Users\\lime\\Desktop\\ZIPFILE2" + ".zip"); // 생성할 zip파일 경로 및 이름
        ZipFile zipFile = new ZipFile(zipPath.toFile(), password.toCharArray()); // 입력한 경로 생성 및 비밀번호 설정

        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setEncryptFiles(true); // 암호화 설정 여부
        zipParameters.setEncryptionMethod(EncryptionMethod.AES); // 암호화 방식 설정

        for (FileObject file : files) {
            zipParameters.setFileNameInZip(file.getName());
            zipFile.addStream(file.getStream(), zipParameters); // 기존에 있으면 덮어쓰기, zip파일이 이미 존재 하면 그 zip 파일에 항목 추가
        }
        
        Date now2 = new Date();
        String nowTime2 = now2.toString();
        System.out.println("zip encoding finish -------------------------------" + nowTime2);
        
        // 압축 풀기
        System.out.println("zip decoding start -------------------------------" + nowTime2);
        
        String source = "C:\\Users\\lime\\Desktop\\ZIPFILE2.zip";
        String destination = "C:\\Users\\lime\\Desktop\\temp"; //zip 파일
        char [] password2 = {'p','a','s','s','w','o','r','d'};   //패스워드

        ZipFile zipFile2 = new ZipFile(source); // zip파일 경로 입력 및 파일까지 작성
		if (zipFile2.isEncrypted()) { // 암호화 되어있다면
		    zipFile2.setPassword(password2); // 암호 설정 (char형으로 입력)
		}
		
		zipFile2.extractFile("test.pptx", destination); // 지정한 위치에 압축 해제
        
		Date now3 = new Date();
		String nowTime3 = now3.toString();
		System.out.println("zip decoding finish -------------------------------" + nowTime3);
    }

    public static class FileObject {
        private String name;
        private InputStream stream;

        public FileObject(String name, InputStream stream) {
            this.name = name;
            this.stream = stream;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public InputStream getStream() {
            return stream;
        }

        public void setStream(InputStream stream) {
            this.stream = stream;
        }
    }
}