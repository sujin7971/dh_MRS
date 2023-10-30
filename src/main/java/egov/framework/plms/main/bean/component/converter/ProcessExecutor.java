package egov.framework.plms.main.bean.component.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
/**
 * 프로세스 실행을 처리할 클래스
 * 
 * @author mckim
 *
 */
@Builder
public class ProcessExecutor {
	/** {@code -100 TIMEOUT_EXCEPTION} 제한 시간 만료 */
	public final static Integer TIMEOUT_EXCEPTION = -100;
	/** {@code -101 EXECUTION_EXCEPTION} 실행 오류 */
	public final static Integer EXECUTION_EXCEPTION = -101;
	/** {@code -102 INTERRUPTED_EXCEPTION} 인터럽트 */
	public final static Integer INTERRUPTED_EXCEPTION = -102;
	/** {@code -103 CONVERT_EXCEPTION} 변환 실패 */
	public final static Integer CONVERT_EXCEPTION = -103;
	/** {@code -103 IO_EXCEPTION} 파일 접근 실패 */
	public final static Integer IO_EXCEPTION = -103;
	/** {@code 100 OK} 성공 */
	public final static Integer EXEC_OK = 100;
	
	private Process process;
	private long timeout;
	private ExecuteWatchdog watchdog;
	private String[] commandArray;
	private String commandString;
	private File dir;
	@PostConstruct
	public void initTimeout() {
		timeout = 600;
	}
	/**
	 * 클래스 생성시 생성자를 통해 입력받은 명령어를 실행한다.
	 * @return 
	 * {@link #EXEC_OK}<br>
	 * {@link #CONVERT_EXCEPTION}<br>
	 * {@link #INTERRUPTED_EXCEPTION}<br>
	 * {@link #EXECUTION_EXCEPTION}<br>
	 * {@link #TIMEOUT_EXCEPTION}<br>
	 */
	public Integer run() {
    	ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<Integer> task = new Callable<Integer>() {
            public Integer call() throws Exception {
                return byRuntime();
            }
        };
        Future<Integer> future = executor.submit(task);
        try {
			Integer result = future.get(600, TimeUnit.SECONDS);
			log.debug("ProcessExecutor run result : {}", result);
			if(result == 0) {
				return EXEC_OK;
			}else {
				return CONVERT_EXCEPTION;
			}
		} catch (InterruptedException e) {
			log.error(e.getMessage());
			return INTERRUPTED_EXCEPTION;
		} catch (ExecutionException e) {
			log.error(e.getMessage());
			return EXECUTION_EXCEPTION;
		} catch (TimeoutException e) {
			log.error(e.getMessage());
			return TIMEOUT_EXCEPTION;
		} finally {
			destroy();
		}
    }
	
	/**
	 * 런타임 객체를 통한 실행
	 * @return
	 */
	public Integer byRuntime() {
		InputStreamReader inputReader = null;
		BufferedReader bufferedReader = null;
		try {
			if(commandArray != null) {
				process = Runtime.getRuntime().exec(commandArray, null, dir);
				log.info("command: {}", Arrays.toString(commandArray));
			}else {
				process = Runtime.getRuntime().exec(commandString, null, dir);
				log.info("command: {}", commandString);
			}
			consumeStream(process);
			Integer result = process.exitValue();
			return result;
		} catch (IOException e) {
			log.error(e.getMessage());
			return IO_EXCEPTION;
		} catch (InterruptedException e) {
			log.error(e.getMessage());
			return INTERRUPTED_EXCEPTION;
		}finally {
			try {
				if (bufferedReader != null) {
			    	bufferedReader.close();
			    }
				if (inputReader != null) {
			        inputReader.close();
			    }
			} catch (IOException e) {
				log.error(e.getMessage());
			}
			destroy();
		}
	}
	
	/**
	 * 프로세스 빌더를 통한 실행
	 * @param command
	 * @return
	 */
	public Integer byProcessBuilder() {
		InputStreamReader inputReader = null;
		BufferedReader bufferedReader = null;
		
        ProcessBuilder builder;
        
        if(commandArray != null) {
        	builder = new ProcessBuilder(commandArray);
		}else {
			builder = new ProcessBuilder(commandString);
		}
        try {
        	process = builder.start();
        	printStream(process);
			Integer result = process.exitValue();
			return result;
		} catch (IOException e) {
			log.error(e.getMessage());
			return IO_EXCEPTION;
		} catch (InterruptedException e) {
			log.error(e.getMessage());
			return INTERRUPTED_EXCEPTION;
		}finally {
			try {
				if (bufferedReader != null) {
			    	bufferedReader.close();
			    }
				if (inputReader != null) {
			        inputReader.close();
			    }
			} catch (IOException e) {
				log.error(e.getMessage());
			}
			destroy();
		}
        
    }
	
	/**
	 * 커맨드 라인을 통한 실행
	 * @return
	 */
	private Integer byCommonsExec() {
	    DefaultExecutor executor = new DefaultExecutor();
	    CommandLine cmdLine;
	    if(commandArray != null) {
	    	cmdLine = CommandLine.parse(commandArray[0]);
		    for (int i=1, n=commandArray.length ; i<n ; i++ ) {
		        cmdLine.addArgument(commandArray[i]);
		    }
		}else {
			cmdLine = CommandLine.parse(commandString);
		}
	    log.info("command: {}", cmdLine);
	    try {
	    	ExecuteWatchdog watchdog = executor.getWatchdog();
	    	this.watchdog = watchdog;
			Integer result = executor.execute(cmdLine);
			return result;
		} catch (ExecuteException e) {
			log.error(e.getMessage());
			return EXECUTION_EXCEPTION;
		} catch (IOException e) {
			log.error(e.getMessage());
			return IO_EXCEPTION;
		}
	}
	
	private void consumeStream(Process process)
            throws IOException, InterruptedException {
		StreamGobbler gb1 = new StreamGobbler(process.getInputStream());
		StreamGobbler gb2 = new StreamGobbler(process.getErrorStream());
		gb1.start();
		gb2.start();

		while (true) {
			if (!gb1.isAlive() && !gb2.isAlive()) { // 두개의 스레드가 정지할면 프로세스 종료때까지 기다린다.
				System.out.println("Thread gb1 Status : " + gb1.getState());
				System.out.println("Thread gb2 Status : " + gb1.getState());
				process.waitFor();
				break;
			}
		}
    }
	
	private void printStream(Process process)
            throws IOException, InterruptedException {
		process.waitFor();
        try (InputStream psout = process.getInputStream()) {
            copy(psout, System.out);
        }
        try (InputStream psout = process.getErrorStream()) {
            copy(psout, System.out);
        }
    }

    public void copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int n = 0;
        while ((n = input.read(buffer)) != -1) {
            output.write(buffer, 0, n);
        }
    }
	
	private void destroy() {
		if(process != null) {
			process.destroy();
		}
		if(watchdog != null) {
			watchdog.destroyProcess();
		}
	}
	
	/*
	 * jna를 사용한 pid 조회
	private long getProcessId(Process p) {
	    long pid = -1;

	    try {
	        if (p.getClass().getName().equals("java.lang.Win32Process") ||
	                p.getClass().getName().equals("java.lang.ProcessImpl"))
	        {
	            Field f = p.getClass().getDeclaredField("handle");
	            f.setAccessible(true);
	            long handl = f.getLong(p);
	            Kernel32 kernel = Kernel32.INSTANCE;
	            WinNT.HANDLE hand = new WinNT.HANDLE();
	            hand.setPointer(Pointer.createConstant(handl));
	            pid = kernel.GetProcessId(hand);
	            f.setAccessible(false);
	        }
	    } catch (Exception e) {
	        pid = -1;
	    }
	    return pid;
	}
	*/
	
	class StreamGobbler extends Thread {
		InputStream is;

		public StreamGobbler(InputStream is) {
			this.is = is;
		}

		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line;
				while ((line = br.readLine()) != null)
					System.out.println(line);
			} catch (IOException ioe) {
				System.out.println(ioe);
			}
		}
	}
}
