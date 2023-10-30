package egov.framework.plms.main.core.model.able;

/**
 * Convertable 클래스는 다른 클래스로 변환 가능하다
 * 
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 * @param <C>
 */
public interface Convertable<C> {
	C convert();
}
