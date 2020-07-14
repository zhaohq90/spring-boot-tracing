package in.aprilfish.tracing.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * Swagger配置属性
 **/
@Data
@Component
@ConfigurationProperties(prefix = "application.swagger")
public class SwaggerProperties {

	/**
	 * 是否启用Swagger
	 */
	private boolean enable;

	/**
	 * system服务配置
	 */
	@NestedConfigurationProperty
	private BaseConfig systemConfig;

	@Data
	public static class BaseConfig {
		/**
		 * 扫描的基本包
		 */
		private String basePackage;

		/**
		 * 描述
		 */
		private String description;

		/**
		 * 标题
		 */
		private String title;

		/**
		 * 版本
		 */
		private String version;
	}


}
