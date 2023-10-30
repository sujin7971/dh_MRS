package egov.framework.plms.main.core.model.able;

import java.time.LocalDate;

import egov.framework.plms.main.core.model.enums.common.OrderColumn;
import egov.framework.plms.main.core.model.enums.common.OrderDirection;

public interface Pageable {
	Integer getRowNum();
	LocalDate getStartDate();
	LocalDate getEndDate();
	Integer getPageNo();
	Integer getPageCnt();
	OrderColumn getOrderColumn();
	OrderDirection getOrderDirection();
	
	default Integer getOffset() {
		if(getPageNo() != null && getPageCnt() != null) {
			return (getPageNo() - 1) * getPageCnt();
		}else {
			return null;
		}
	}
	
	default Integer getLimit() {
		if(getPageCnt() != null) {
			return getPageCnt();
		}else {
			return 10;
		}
	}
}
