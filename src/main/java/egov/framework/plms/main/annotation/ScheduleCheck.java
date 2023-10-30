/**
 * 
 */
package egov.framework.plms.main.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 함수에 매핑 가능한 런타임 어노테이션. 매핑시 스케줄에 관련된 여러 기능 처리(임시 스케줄 삭제등)
 * @author k
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface ScheduleCheck {

}
