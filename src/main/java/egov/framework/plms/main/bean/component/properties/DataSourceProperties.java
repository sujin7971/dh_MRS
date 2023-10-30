package egov.framework.plms.main.bean.component.properties;

import lombok.Data;

@Data
public class DataSourceProperties {
	private String username;
    private String password;
    private String jdbcUrl;
    private String driverClassName;
}
