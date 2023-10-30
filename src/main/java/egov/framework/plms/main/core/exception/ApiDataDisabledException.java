package egov.framework.plms.main.core.exception;

import egov.framework.plms.main.core.exception.abst.ApiException;
import egov.framework.plms.main.core.model.enums.error.ErrorCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiDataDisabledException extends ApiException {
	private final ErrorCodeEnum errorCode;
}
