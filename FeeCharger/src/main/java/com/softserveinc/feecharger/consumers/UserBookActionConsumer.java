package com.softserveinc.feecharger.consumers;

import com.softserveinc.cross_api_objects.avro.AvroUserBookAction;
import com.softserveinc.cross_api_objects.avro.AvroUserBookActionStatus;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationConstraints;
import com.softserveinc.cross_api_objects.utils.correlation_id.CorrelationManager;
import com.softserveinc.feecharger.models.UserBook;
import com.softserveinc.feecharger.services.FeeChargerService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class UserBookActionConsumer {
    private static final Logger log= LoggerFactory.getLogger(UserBookActionConsumer.class);

    private FeeChargerService feeChargerService;

    @Autowired
    public UserBookActionConsumer(FeeChargerService feeChargerService) {
        this.feeChargerService = feeChargerService;
    }

    @KafkaListener(topics = "#{@userBookActionTopic}",containerFactory = "userBookActionListener")
    public void consume(ConsumerRecord<String, AvroUserBookAction> record,
                        @Header(CorrelationConstraints.CORRELATION_ID_HEADER_NAME) String correlationId){
        CorrelationManager.setCorrelationId(correlationId);

        log.info("Consuming UserBookAction. User id: "+record.value().getUserId()+". Book id: "+record.value().getBookId()+
                ". Status: "+record.value().getStatus().toString());

        if(record.value().getStatus()== AvroUserBookActionStatus.TAKE){
            feeChargerService.tryExtendRent(
                    record.value().getUserId(),
                    record.value().getBookId()
            );
        }

        CorrelationManager.removeCorrelationId();
    }
}
