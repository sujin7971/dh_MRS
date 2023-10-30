package egov.framework.plms.main.core.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;

import egov.framework.plms.main.core.model.enums.CodeEnum;
import lombok.extern.slf4j.Slf4j;
/**
 * Mybatis에서 String형을 값으로 사용하는 ENUM 클래스를 다루기 위한 핸들러<br>
 * sqlSessionFactory를 통해 핸들러를 등록해줘야 한다.
 * 
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 * @param <E>
 * @see {@link egov.framework.plms.main.config.DataSourceConfig#sqlSessionFactory}
 */
@Slf4j
public abstract class CodeEnumTypeHandler<E extends Enum<E>> implements TypeHandler<CodeEnum> {

    private Class<E> type;

    public CodeEnumTypeHandler(Class<E> type) {
    	log.info("TypeHandler Constructor");
        this.type = type;
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, CodeEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, Optional.ofNullable(parameter).map(enumParam -> enumParam.getCode()).orElse(null));
    }

    @Override
    public CodeEnum getResult(ResultSet rs, String columnName) throws SQLException {
        return getCodeEnum(rs.getString(columnName));
    }

    @Override
    public CodeEnum getResult(ResultSet rs, int columnIndex) throws SQLException {
        return getCodeEnum(rs.getString(columnIndex));
    }

    @Override
    public CodeEnum getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return getCodeEnum(cs.getString(columnIndex));
    }

    private CodeEnum getCodeEnum(String code) {
    	try {
			CodeEnum[] enumConstants = (CodeEnum[]) type.getEnumConstants();
			for ( CodeEnum codeNum: enumConstants ) {    
				if ( codeNum.getCode().equals( code ) ) {
					return codeNum;
				}
			}
			
			return null;
		} catch (Exception e) {
			throw new TypeException("Can't make enum object '" + type + "'", e);
		}
    }
}
