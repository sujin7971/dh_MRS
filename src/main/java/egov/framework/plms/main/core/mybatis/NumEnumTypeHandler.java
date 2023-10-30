package egov.framework.plms.main.core.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;

import egov.framework.plms.main.core.model.enums.NumEnum;
import lombok.extern.slf4j.Slf4j;


/**
 * Mybatis에서 Integer형을 값으로 사용하는 ENUM 클래스를 다루기 위한 핸들러<br>
 * sqlSessionFactory를 통해 핸들러를 등록해줘야 한다.
 * 
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 * @param <E>
 * @see {@link egov.framework.plms.main.config.DataSourceConfig#sqlSessionFactory}
 */
@Slf4j
public abstract class NumEnumTypeHandler<E extends Enum<E>> implements TypeHandler<NumEnum> {

    private Class<E> type;

    public NumEnumTypeHandler(Class<E> type) {
    	log.info("TypeHandler Constructor");
        this.type = type;
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, NumEnum parameter, JdbcType jdbcType) throws SQLException {
    	if(parameter != null) {
    		ps.setInt(i, parameter.getCode());
    	}else {
    		ps.setObject(i, null);
    	}
    }

    /**
     * 결과 집합의 지정된 열 이름에 대응하는 값을 NumEnum 형태로 반환합니다.
     *
     * ResultSet의 getInt() 메소드를 사용하면, 해당 칼럼의 값이 SQL NULL일 경우에도 0을 반환하게 됩니다.
     * 이로 인해, 실제 DB에 저장된 값이 NULL임에도 코드값이 0인 Enum 상수(예){@link ConversionStatus.NO_REQUEST})로 매핑이 되는 문제가 발생하게 됩니다.
     * 이러한 문제를 방지하기 위해, wasNull() 메소드를 사용하여 최근에 읽은 SQL 칼럼 값이 SQL NULL인지를 확인하고, 
     * 이에 따라 적절한 값을 반환하도록 구현하였습니다.
     *
     * @param rs 결과 집합
     * @param columnName 열 이름
     * @return columnName에 대응하는 값이 SQL NULL일 경우 null, 그렇지 않을 경우 해당 값을 가진 NumEnum. 
     * @throws SQLException SQL 처리 중 발생한 예외
     */
    @Override
    public NumEnum getResult(ResultSet rs, String columnName) throws SQLException {
    	int value = rs.getInt(columnName);
        return rs.wasNull() ? null : getNumEnum(value);
    }

    @Override
    public NumEnum getResult(ResultSet rs, int columnIndex) throws SQLException {
    	int value = rs.getInt(columnIndex);
        return rs.wasNull() ? null : getNumEnum(value);
    }

    @Override
    public NumEnum getResult(CallableStatement cs, int columnIndex) throws SQLException {
    	int value = cs.getInt(columnIndex);
        return cs.wasNull() ? null : getNumEnum(value);
    }

    private NumEnum getNumEnum(Integer num) {
    	try {
    		NumEnum[] enumConstants = (NumEnum[]) type.getEnumConstants();
			for ( NumEnum codeNum: enumConstants ) {    
				if ( codeNum.getCode().equals( num ) ) {
					return codeNum;
				}
			}
			
			return null;
		} catch (Exception e) {
			throw new TypeException("Can't make enum object '" + type + "'", e);
		}
    }
}
