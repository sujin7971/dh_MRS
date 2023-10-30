package egov.framework;

import java.util.Scanner;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class PlmsApplicationTests {
	private Scanner sc;
	@PostConstruct
	public void init() {
		sc = new Scanner(System.in);
	}
    @Test
    public void contextLoads() throws Exception {
    	while(true) {
    		System.out.println("\n1.직원 복호화 ");
    		System.out.println("\n2.문자열 복호화 ");
    		System.out.println("\n3.비트연산 ");
    		System.out.println("\n4.종료 ");
    		System.out.println("\n\n 메뉴 선택 : ");
    		String str = sc.nextLine();
    		try {
    			Integer menuNum = Integer.parseInt(str);
    			switch(menuNum) {
	    			case 1:
	    				decryptEmp();
	    				break;
	    			case 2:
	    				break;
	    			case 3:
	    				bitwiseOp();
	    				break;
	    			case 4:
	    				sc.close();
	    				return;
    			}
    		}catch(NumberFormatException e) {
    			System.out.println("\n1~4 숫자만 입력. ");
    		}
    	}
    }
    
    public void encryptString() {
    	while(true) {
    		System.out.println("\n암호화 할 문자열 : ");
    		String str = sc.nextLine();
    		if(str.equals("q") || str.equals("e")) {
    			break;
    		}
    	}
    }
    
    public void bitwiseOp() {
    	log.info("5(1,4) & 2(2) : {}", 5 & 2);
    	log.info("5(1,4) | 2(2) : {}", 5 | 2);
    	log.info("14(2,4,8) & 4(4) : {}", 14 & 4);
    	log.info("14(2,4,8) | 4(4) : {}", 14 | 4);
    	log.info("45(1,4,8,32) & 27(1,2,8,16) : {}", 45 & 27);
    	log.info("45(1,4,8,32) | 27(1,2,8,16) : {}", 45 | 27);
    }
    
    //@Test
    public void decryptEmp() {
    }

}
