package com.softserveinc.cross_api_objects.utils.correlation_id;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import java.io.IOException;
import java.util.UUID;

public class HttpCorrelationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final String correlationId=getCorrelationIdFromHeader((HttpServletRequest)servletRequest);
        CorrelationManager.setCorrelationId(correlationId);

        filterChain.doFilter(servletRequest,servletResponse);

        CorrelationManager.removeCorrelationId();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    private String getCorrelationIdFromHeader(final HttpServletRequest servletRequest) {
        String correlationId = servletRequest.getHeader(CorrelationConstraints.CORRELATION_ID_HEADER_NAME);
        if (StringUtils.isBlank(correlationId)) {
            correlationId = generateUniqueCorrelationId();
        }
        return correlationId;
    }

    private String generateUniqueCorrelationId() {
        return UUID.randomUUID().toString();
    }
}
