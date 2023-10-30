package egov.framework.plms.main.bean.mvc.entity.common;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import egov.framework.plms.main.bean.mvc.entity.common.abst.StatModelDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatDTO extends StatModelDTO {
	private String refId;
	private String refName;
	private LocalDate refDate;
    private LocalDateTime refDateTime;
    private Integer refYear;
    private Integer refMonth;
    private Integer refDay;
    private String statName1;
    private String statName2;
    private String statName3;
    private Integer statValue1;
    private Integer statValue2;
    private Integer statValue3;
    
    private LocalDate startDate;
    private LocalDate endDate;
    private String userId;
    private String officeCode;
    
    @Builder(builderClassName = "init", builderMethodName = "initDTO")
	public StatDTO(StatVO vo){
    	this.refId = vo.getRefId();
    	this.refName = vo.getRefName();
    	this.refDate = vo.getRefDate();
    	this.refDateTime = vo.getRefDateTime();
    	this.refYear = vo.getRefYear();
    	this.refMonth = vo.getRefMonth();
    	this.refDay = vo.getRefDay();
    	this.statName1 = vo.getStatName1();
    	this.statName2 = vo.getStatName2();
    	this.statName3 = vo.getStatName3();
    	this.statValue1 = vo.getStatValue1();
    	this.statValue2 = vo.getStatValue2();
    	this.statValue3 = vo.getStatValue3();
    	
    	this.startDate = vo.getStartDate();
    	this.endDate = vo.getEndDate();
    	this.userId = vo.getUserId();
    	this.officeCode = vo.getOfficeCode();
    }
    
	@Override
	public StatVO convert() {
		return StatVO.initVO().dto(this).build();
	}
	
	public String getRefYearMonth() {
		if(this.refYear != null && this.refMonth != null) {
			return this.refYear+"-"+this.refMonth;
		}else {
			return null;
		}
	}
}
