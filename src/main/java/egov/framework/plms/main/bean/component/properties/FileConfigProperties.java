package egov.framework.plms.main.bean.component.properties;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties("config.file")
public class FileConfigProperties {
	private String uploadPath;
	private Cvt cvt;
	private ConversionServer conversionServer;
	
	@Data
	public static class Cvt{
		private String desktop;
		private List<String> cvtUserList;
		private Integer poolMax;
		private Integer imgDpi;
		private String hwp;
		private String ppt;
		private String word;
		private String excel;
		private String image;
		private Webp webp;
		
		@Data
		public static class Webp{
			private boolean enabled;
			private Integer quality;
			private String encoder;
			private String decoder;
		}
	}
	@Data
	public static class ConversionServer{
		private boolean enabled;
		private String baseUrl;
	}
}
