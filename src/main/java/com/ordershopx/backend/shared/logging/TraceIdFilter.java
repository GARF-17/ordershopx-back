package com.ordershopx.backend.shared.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class TraceIdFilter extends OncePerRequestFilter {

    private static final String TRACE_ID = "traceId";
    private static final String HEADER_TRACE_ID = "X-Trace-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String traceId = request.getHeader(HEADER_TRACE_ID);

        if (traceId == null || traceId.isBlank()) {
            traceId = request.getHeader("X-Request-Id");
        }

        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }

        MDC.put(TRACE_ID, traceId);
        response.setHeader(HEADER_TRACE_ID, traceId);

        long startTime = System.currentTimeMillis();

        log.info("traceId={} event=request_start method={} path={} ip={}",
                traceId,
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr());

        try {
            filterChain.doFilter(request, response);
        } catch (Exception ex) {

            log.error("traceId={} event=request_error path={}",
                    traceId,
                    request.getRequestURI(),
                    ex);

            throw ex;

        } finally {

            long duration = System.currentTimeMillis() - startTime;

            log.info("traceId={} event=request_end method={} path={} status={} durationMs={}",
                    traceId,
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration);

            MDC.remove(TRACE_ID);
        }
    }
}
