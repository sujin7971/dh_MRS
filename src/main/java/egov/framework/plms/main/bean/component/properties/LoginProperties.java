package egov.framework.plms.main.bean.component.properties;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.core.model.enums.user.DomainRole;
import lombok.Data;

@Data
@Component
@ConfigurationProperties("config.login")
public class LoginProperties {
	private Integer MaximumConcurrentUser;
	private LoginPermission loginPermission;
	private List<TempAccount> tempAccount;
	
	@Data
    public static class TempAccount {
        private String principal;
        private String credential;
        private DomainRole domainRole;
        private String name;
    }
	
	@Data
	public static class LoginPermission{
		private boolean generalUserEnabled;
		private boolean systemAdminEnabled;
		private boolean masterAdminEnabled;
		private boolean approvalManagerEnabled;
		private boolean guestUserEnabled;
	}
	
	public List<TempAccount> getTempAccount(){
		if(this.tempAccount == null) {
			this.tempAccount = new ArrayList<TempAccount>();
		}
		return this.tempAccount;
	}
}
