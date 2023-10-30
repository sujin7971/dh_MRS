package egov.framework.plms.main.core.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;


/**
 * Logback의 Mybatis를 통한 쿼리 실행시 적용할 필터 설정
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
public class LogbackQueryFilter extends Filter<ILoggingEvent>{
    @Override
    public FilterReply decide(ILoggingEvent event) {    
        if (event.getMessage().contains("NO_SQL_LOG")) {// 로깅 내용에 해당 문구가 포함되어 있는 경우 해당 로그를 출력하지 않음
            return FilterReply.DENY;
        }else{
            return FilterReply.ACCEPT;
        }
    }
}
