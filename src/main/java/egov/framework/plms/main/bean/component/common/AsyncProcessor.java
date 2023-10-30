package egov.framework.plms.main.bean.component.common;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 비동기 작업을 처리하는 컴포넌트
 * @author mckim
 * @version 1.0
 * @since 2023. 4. 17
 */
@Component
public class AsyncProcessor {
	/**
     * 원하는 결과를 얻을 때까지 주어진 sleepMillis 간격으로 함수를 반복하여 체크하는 메서드입니다.
     *
     * @param functionToCheck 실행할 함수를 전달합니다.
     * @param sleepMillis     체크 간격을 설정합니다.
     * @param expectedResult  원하는 결과값을 전달합니다.
     * @return 원하는 결과를 얻으면 CompletableFuture를 반환합니다.
     */
	@Async
    public <T> CompletableFuture<T> checkFunctionAsync(Supplier<T> functionToCheck, long sleepMillis, T expectedResult) {
        T result;
        do {
            result = functionToCheck.get();
            if (result.equals(expectedResult)) {
                break;
            }
            try {
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (true);

        return CompletableFuture.completedFuture(result);
    }
	/**
     * 원하는 결과를 얻을 때까지 주어진 sleepMillis 간격으로 함수를 반복하여 체크하되,
     * 먼저 설정된 시간만큼 대기한 후 체크를 시작하는 메서드입니다.
     *
     * @param functionToCheck 실행할 함수를 전달합니다.
     * @param sleepMillis     체크 간격을 설정합니다.
     * @param expectedResult  원하는 결과값을 전달합니다.
     * @return 원하는 결과를 얻으면 CompletableFuture를 반환합니다.
     */
	@Async
    public <T> CompletableFuture<T> checkFunctionAsyncWithDelay(Supplier<T> functionToCheck, long sleepMillis, T expectedResult) {
        T result;
        do {
            result = functionToCheck.get();
            if (result.equals(expectedResult)) {
                break;
            }
            try {
            	Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
            	e.printStackTrace();
            }
        } while (true);

        return CompletableFuture.completedFuture(result);
    }
	/**
     * 원하는 결과를 얻을 때까지 기본 체크 간격(200ms)으로 함수를 반복하여 체크하는 메서드입니다.
     *
     * @param functionToCheck 실행할 함수를 전달합니다.
     * @param expectedResult  원하는 결과값을 전달합니다.
     * @return 원하는 결과를 얻으면 CompletableFuture를 반환합니다.
     */
    @Async
    public <T> CompletableFuture<T> checkFunctionAsync(Supplier<T> functionToCheck, T expectedResult) {
        // 기본 sleep 시간인 200ms를 사용합니다.
        return checkFunctionAsync(functionToCheck, 200, expectedResult);
    }
    /**
     * 원하는 결과를 얻을 때까지 기본 체크 간격(200ms)으로 함수를 반복하여 체크하되,
     * 먼저 설정된 시간만큼 대기한 후 체크를 시작하는 메서드입니다.
     *
     * @param functionToCheck 실행할 함수를 전달합니다.
     * @param expectedResult  원하는 결과값을 전달합니다.
     * @return 원하는 결과를 얻으면 CompletableFuture를 반환합니다.
     */
    @Async
    public <T> CompletableFuture<T> checkFunctionAsyncWithDelay(Supplier<T> functionToCheck, T expectedResult) {
        // 기본 sleep 시간인 200ms를 사용합니다.
        return checkFunctionAsyncWithDelay(functionToCheck, 200, expectedResult);
    }
    /**
     * 설정된 대기 시간 후에 주어진 함수를 실행하는 메서드입니다.
     *
     * @param functionToExecute 실행할 함수를 전달합니다.
     * @param delayMillis       대기 시간을 설정합니다.
     * @return 함수 실행 결과를 반환하는 CompletableFuture를 반환합니다.
     */
    @Async
    public <T> CompletableFuture<T> executeFunctionAfterDelay(Supplier<T> functionToExecute, long delayMillis) {
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        T result = functionToExecute.get();
        return CompletableFuture.completedFuture(result);
    }
}
