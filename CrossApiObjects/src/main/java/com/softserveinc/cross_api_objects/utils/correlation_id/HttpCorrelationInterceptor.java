package com.softserveinc.cross_api_objects.utils.correlation_id;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.MDC;
import org.springframework.stereotype.Component;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.UUID;

@Provider
@Priority(6000)
public class HttpCorrelationInterceptor implements ContainerRequestFilter, ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        final String correlationId=getCorrelationIdFromHeader(containerRequestContext);
        CorrelationManager.setCorrelationId(correlationId);
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {
        CorrelationManager.removeCorrelationId();
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
