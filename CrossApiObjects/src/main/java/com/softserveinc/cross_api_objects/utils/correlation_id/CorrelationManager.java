package com.softserveinc.cross_api_objects.utils.correlation_id;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.util.UUID;

public class CorrelationManager {

    public static String getCorrelationId(){
        if(StringUtils.isNotBlank(MDC.get(CorrelationConstraints.CORRELATION_ID_LOG_VAR_NAME))){
            return MDC.get(CorrelationConstraints.CORRELATION_ID_LOG_VAR_NAME);
        }
        return generateCorrelationId();
    }

    public static void setCorrelationId(String correlationId){
        MDC.put(CorrelationConstraints.CORRELATION_ID_LOG_VAR_NAME,correlationId);
    }

    public static void removeCorrelationId(){
        MDC.remove(CorrelationConstraints.CORRELATION_ID_LOG_VAR_NAME);
    }

    private static String generateCorrelationId(){
        return UUID.randomUUID().toString();
    }
}
