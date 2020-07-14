package in.aprilfish.tracing.filter;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Http跟踪日志记录filter
 */
@Slf4j
public class HttpTraceLogFilter extends OncePerRequestFilter implements Ordered {

	private static final String NEED_TRACE_PATH_PREFIX = "/";
	private static final String IGNORE_CONTENT_TYPE = "multipart/form-data";

	public static ThreadLocal<String> TRACE_ID=new ThreadLocal<>();

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE - 10;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (!isRequestValid(request)) {
			filterChain.doFilter(request, response);
			return;
		}
		if (!(request instanceof ContentCachingRequestWrapper)) {
			request = new ContentCachingRequestWrapper(request);
		}
		if (!(response instanceof ContentCachingResponseWrapper)) {
			response = new ContentCachingResponseWrapper(response);
		}
		int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
		long startTime = System.currentTimeMillis();
		try {
			String traceId=UUID.randomUUID().toString().replaceAll("-","");
			TRACE_ID.set(traceId);
			MDC.put("trace-id",traceId);

			response.addHeader("trace-id",TRACE_ID.get());

			filterChain.doFilter(request, response);
			status = response.getStatus();
		} finally {
			String path = request.getRequestURI();
			if (path.startsWith(NEED_TRACE_PATH_PREFIX) && !Objects.equals(IGNORE_CONTENT_TYPE, request.getContentType())) {

				String requestBody = IOUtils.toString(request.getInputStream(), "utf-8");
				log.info(requestBody);
				//1. 记录日志
				HttpTraceLog traceLog = new HttpTraceLog();
				traceLog.setTraceId(TRACE_ID.get());
				traceLog.setPath(path);
				traceLog.setMethod(request.getMethod());
				long latency = System.currentTimeMillis() - startTime;
				traceLog.setTimeTaken(latency);
				traceLog.setTime(LocalDateTime.now().toString());
				traceLog.setParameterMap(JSON.toJSONString(request.getParameterMap()));
				traceLog.setStatus(status);
				traceLog.setRequestBody(getRequestBody(request));
				traceLog.setResponseBody(getResponseBody(response));

				log.info("Http trace log: {}",JSON.toJSONString(traceLog));
			}
			updateResponse(response);

			//清空ThreadLocal避免内存泄漏和数据污染
			TRACE_ID.remove();
			MDC.clear();
		}
	}

	private boolean isRequestValid(HttpServletRequest request) {
		try {
			new URI(request.getRequestURL().toString());
			return true;
		} catch (URISyntaxException ex) {
			return false;
		}
	}

	private String getRequestBody(HttpServletRequest request) {
		String requestBody = "";
		ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
		try {
			requestBody = new String(wrapper.getContentAsByteArray(), wrapper.getCharacterEncoding());
		} catch (IOException e) {
			// NOOP
		}

		return requestBody;
	}

	private String getResponseBody(HttpServletResponse response) {
		String responseBody = "";
		ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
		if (wrapper != null) {
			try {
				responseBody = IOUtils.toString(wrapper.getContentAsByteArray(), wrapper.getCharacterEncoding());
			} catch (IOException e) {
				// NOOP
			}
		}
		return responseBody;
	}

	private void updateResponse(HttpServletResponse response) throws IOException {
		ContentCachingResponseWrapper responseWrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
		Objects.requireNonNull(responseWrapper).copyBodyToResponse();
	}

	@Data
	private static class HttpTraceLog {
		private String traceId;
		private String path;
		private String parameterMap;
		private String method;
		private Long timeTaken;
		private String time;
		private Integer status;
		private String requestBody;
		private String responseBody;
	}

}
