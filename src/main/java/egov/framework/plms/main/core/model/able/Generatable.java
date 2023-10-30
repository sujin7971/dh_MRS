package egov.framework.plms.main.core.model.able;


/**
 * Generatable클래스는 다른 클래스를 생성 가능하다
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 * @param <E>
 */
public interface Generatable<E> {
	E generate();
}
