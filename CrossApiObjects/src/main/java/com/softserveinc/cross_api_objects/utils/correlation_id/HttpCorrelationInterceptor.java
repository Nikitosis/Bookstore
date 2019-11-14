package com.softserveinc.cross_api_objects.utils.correlation_id;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.MDC;
import org.springframework.stereotype.Component;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.UUID;

@Component
@Provider
public class HttpCorrelationInterceptor implements ContainerRequestFilter, ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        final String correlationId=getCorrelationIdFromHeader(containerRequestContext);
        MDC.put(CorrelationConstraints.CORRELATION_ID_LOG_VAR_NAME,correlationId);
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {
        MDC.remove(CorrelationConstraints.CORRELATION_ID_LOG_VAR_NAME);
    }

    private String getCorrelationIdFromHeader(final ContainerRequestContext containerRequestContext) {
        String correlationId = containerRequestContext.getHeaderString(CorrelationConstraints.CORRELATION_ID_HEADER_NAME);
        if (StringUtils.isBlank(correlationId)) {
            correlationId = generateUniqueCorrelationId();
        }
        return correlationId;
    }

    private String generateUniqueCorrelationId() {
        return UUID.randomUUID().toString();
    }
}
