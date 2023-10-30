package egov.framework.plms.main.bean.mvc.entity.common.abst;

import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class StatEntity {
	public abstract String getRefId();
	public abstract LocalDate getRefDate();
	public abstract LocalDateTime getRefDateTime();
	public abstract Integer getRefYear();
	public abstract Integer getRefMonth();
	public abstract Integer getRefDay();
	public abstract String getStatName1();
	public abstract Integer getStatValue1();
	public abstract String getStatName2();
	public abstract Integer getStatValue2();
	public abstract String getStatName3();
	public abstract Integer getStatValue3();
	
	public abstract LocalDate getStartDate();
	public abstract LocalDate getEndDate();
}
