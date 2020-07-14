package in.aprilfish.tracing.config;

import com.alibaba.fastjson.JSON;
import in.aprilfish.tracing.filter.HttpTraceLogFilter;
import in.aprilfish.tracing.properties.ApplicationFilterProperties;
import in.aprilfish.tracing.properties.ApplicationProperties;
import in.aprilfish.tracing.properties.SwaggerProperties;
import in.aprilfish.tracing.filter.TraceFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;

/**
 * 自定义WebMvc配置
 */
@Slf4j
@Configuration
public class CustomWebMvcConfig implements WebMvcConfigurer {

	@Autowired
	private ApplicationProperties applicationProperties;

	/**
	 * Filter配置
	 */
	private ApplicationFilterProperties filterConfig;

	private SwaggerProperties swaggerConfig;

	@PostConstruct
	public void init() {
		filterConfig = applicationProperties.getFilter();
		swaggerConfig = applicationProperties.getSwagger();
		log.debug("ApplicationProperties：{}", JSON.toJSONString(applicationProperties));
	}

	//@Bean
	public FilterRegistrationBean traceFilter() {
		ApplicationFilterProperties.FilterConfig traceFilterConfig = filterConfig.getTrace();
		FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
		filterRegistrationBean.setFilter(new TraceFilter());
		filterRegistrationBean.setEnabled(traceFilterConfig.isEnable());
		filterRegistrationBean.addUrlPatterns(traceFilterConfig.getUrlPatterns());
		filterRegistrationBean.setOrder(traceFilterConfig.getOrder());
		filterRegistrationBean.setAsyncSupported(traceFilterConfig.isAsync());

		return filterRegistrationBean;
	}

	@Bean
	public FilterRegistrationBean httpTraceLogFilter() {
		FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
		filterRegistrationBean.setFilter(new HttpTraceLogFilter());
		filterRegistrationBean.setOrder(2);

		return filterRegistrationBean;
	}

}
