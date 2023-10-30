package egov.framework.plms.main.core.model.able;

import egov.framework.plms.main.core.model.enums.common.OrderDirection;

public interface Sortable {
	String getOrderColumn();
	OrderDirection getOrderDirection();
	
}
