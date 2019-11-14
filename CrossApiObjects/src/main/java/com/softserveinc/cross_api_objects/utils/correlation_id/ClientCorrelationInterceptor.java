package com.softserveinc.cross_api_objects.utils.correlation_id;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.MDC;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.UUID;

@Provider
public class ClientCorrelationInterceptor implements ClientRequestFilter {
    private static final String CORRELATION_ID_HEADER_NAME ="X-Correlation-Id";
    private static final String CORRELATION_ID_LOG_VAR_NAME ="correlationId";


    @Override
    public void filter(ClientRequestContext clientRequestContext) throws IOException {
        final String correlationId=getCorrelationId();
        clientRequestContext.getHeaders().add(CORRELATION_ID_HEADER_NAME,correlationId);
    }

    private String getCorrelationId(){
        if(StringUtils.isNotBlank((String)MDC.get(CORRELATION_ID_LOG_VAR_NAME))){
            return (String)MDC.get(CORRELATION_ID_LOG_VAR_NAME);
        }
        return generateUniqueCorrelationId();
    }

    private String generateUniqueCorrelationId() {
        return UUID.randomUUID().toString();
    }
}
