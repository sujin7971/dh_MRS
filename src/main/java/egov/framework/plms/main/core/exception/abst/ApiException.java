package egov.framework.plms.main.core.exception.abst;

import egov.framework.plms.main.core.model.enums.error.ErrorCodeEnum;

public abstract class ApiException extends RuntimeException {
	public abstract ErrorCodeEnum getErrorCode();

	@Override
	public String getMessage() {
		return getErrorCode().getMessage();
	}
	
	
}
