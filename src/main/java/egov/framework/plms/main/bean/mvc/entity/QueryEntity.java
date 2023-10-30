package egov.framework.plms.main.bean.mvc.entity;

import egov.framework.plms.main.core.model.enums.CrudOperation;
import lombok.Builder;
import lombok.Data;

/**
 * 쿼리 개체
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 * @param <T>
 */
@Data
@Builder
public class QueryEntity<T> {
	/** 시작일자 */
	private String startDate;
	/** 종료일자 */
	private String endDate;
	/** 특정일자 */
	private String dueDate;
	/** 검색어 */
	private String searchWord;
	/** 검색대상 */
	private String searchTarget;
	/** 정렬순서 */
	private String orderCmd;
	/** 사용자 고유키 */
	private String userKey;
	/** 사용자 부서키 */
	private String deptId;
	/** 개수 */
	private Integer count;
	/** 수행 명령어 */
	private CrudOperation operation;
	/** 데이터 */
	private T data;
}
