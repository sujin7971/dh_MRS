package egov.framework.plms.sub.ewp.bean.component.properties;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.component.properties.ReserveConfigProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Component
@RequiredArgsConstructor
@Profile("ewp")
public class EwpPropertiesProvider {
	private final ReserveConfigProperties reserveProperties;
}
