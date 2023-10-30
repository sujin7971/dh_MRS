package egov.framework.plms.main.bean.mvc.entity.common;

import java.time.LocalDate;
import java.time.LocalDateTime;

import egov.framework.plms.main.bean.mvc.entity.common.abst.StatModelVO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class StatVO extends StatModelVO {
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
    
    @Builder(builderClassName = "init", builderMethodName = "initVO")
	public StatVO(StatDTO dto){
    	this.refId = dto.getRefId();
    	this.refName = dto.getRefName();
    	this.refDate = dto.getRefDate();
    	this.refDateTime = dto.getRefDateTime();
    	this.refYear = dto.getRefYear();
    	this.refMonth = dto.getRefMonth();
    	this.refDay = dto.getRefDay();
    	this.statName1 = dto.getStatName1();
    	this.statName2 = dto.getStatName2();
    	this.statName3 = dto.getStatName3();
    	this.statValue1 = dto.getStatValue1();
    	this.statValue2 = dto.getStatValue2();
    	this.statValue3 = dto.getStatValue3();
    	
    	this.startDate = dto.getStartDate();
    	this.endDate = dto.getEndDate();
    	this.userId = dto.getUserId();
    	this.officeCode = dto.getOfficeCode();
    }
    
	@Override
	public StatDTO convert() {
		return StatDTO.initDTO().vo(this).build();
	}
	
	public String getRefYearMonth() {
		if(this.refYear != null && this.refMonth != null) {
			return this.refYear+"-"+this.refMonth;
		}else {
			return null;
		}
	}
}
