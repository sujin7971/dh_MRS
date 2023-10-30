package egov.framework.plms.main.bean.mvc.entity.common.abst;

import java.time.LocalDate;
import java.time.LocalDateTime;

import egov.framework.plms.main.core.model.able.Convertable;

public abstract class StatModelDTO extends StatEntity implements Convertable<StatModelVO> {
	public abstract void setRefId(String value);
	public abstract void setRefDate(LocalDate value);
	public abstract void setRefDateTime(LocalDateTime value);
	public abstract void setRefYear(Integer value);
	public abstract void setRefMonth(Integer value);
	public abstract void setRefDay(Integer value);
	public abstract void setStatName1(String value);
	public abstract void setStatValue1(Integer value);
	public abstract void setStatName2(String value);
	public abstract void setStatValue2(Integer value);
	public abstract void setStatName3(String value);
	public abstract void setStatValue3(Integer value);
	
	public abstract void setStartDate(LocalDate value);
	public abstract void setEndDate(LocalDate value);
}
