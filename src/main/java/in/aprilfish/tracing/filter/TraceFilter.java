package in.aprilfish.tracing.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * trace-id记录/清除filter
 * 对每一个进来的请求设置一个唯一的trace-id，并在请求结束的时候在header中返回，用以追踪请求日志
 */
@Slf4j
public class TraceFilter implements Filter {

    public static ThreadLocal<String> TRACE_ID=new ThreadLocal<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("TraceFilter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String traceId=UUID.randomUUID().toString().replaceAll("-","");
        TRACE_ID.set(traceId);
        MDC.put("trace-id",traceId);

        response = new ContentCachingResponseWrapper((HttpServletResponse)response);
        ((HttpServletResponse)response).addHeader("trace-id",TRACE_ID.get());

        chain.doFilter(request, response);

        //清空ThreadLocal避免内存泄漏和数据污染
        TRACE_ID.remove();
        MDC.clear();
    }

    @Override
    public void destroy() {
        log.info("TraceFilter destroy");
    }

}
