package in.aprilfish.tracing.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

    /**
     * 过滤器配置
     */
    @NestedConfigurationProperty
    private ApplicationFilterProperties filter;

    /**
     * Swagger配置
     */
    @NestedConfigurationProperty
    private SwaggerProperties swagger;

}
