package egov.framework.plms.main.core.model.able;


/**
 * 필터링 수준에 따라 객체의 정보를 걸러주는 인터페이스
 * @author mckim
 * @version 1.0
 * @since 2023. 5. 3
 * @param <T>
 */
public interface Filterable<T extends Filterable<T>> {
	/**
     * 필수적인 정보만 걸러내어 전달합니다. (예: 사용자 이름 또는 부서 이름)
     * @return 필수적인 정보만 포함한 객체
     */
    T filterForEssential();

    /**
     * 기본 정보를 걸러내어 전달합니다.
     * @return 기본 정보만 포함한 객체
     */
    T filterForBasic();

    /**
     * 상세한 정보를 걸러내어 전달합니다.
     * @return 상세한 정보만 포함한 객체
     */
    T filterForDetailed();

    /**
     * 민감한 정보까지 포함하여 전달합니다.
     * @return 민감한 정보까지 포함한 객체
     */
    T filterForSensitive();

    /**
     * 절대로 전달되지 않아야 하는 정보만 걸러냅니다. (예: 사용자의 비밀번호)
     * @return 최고 수준의 필터링을 적용한 객체
     */
    T filterForHighest();
}
