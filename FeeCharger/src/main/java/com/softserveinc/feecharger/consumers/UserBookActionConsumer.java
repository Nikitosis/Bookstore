package com.softserveinc.feecharger.consumers;

import com.softserveinc.cross_api_objects.avro.AvroUserBookAction;
import com.softserveinc.cross_api_objects.avro.AvroUserBookActionStatus;
import com.softserveinc.cross_api_objects.avro.AvroUserBookActionType;
import com.softserveinc.feecharger.services.FeeChargerService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserBookActionConsumer {
    private FeeChargerService feeChargerService;

    @Autowired
    public UserBookActionConsumer(FeeChargerService feeChargerService) {
        this.feeChargerService = feeChargerService;
    }

    @KafkaListener(topics = "#{@userBookActionTopic}",containerFactory = "userBookActionListener")
    public void consume(ConsumerRecord<String, AvroUserBookAction> record){
        if(record.value().getAction()== AvroUserBookActionStatus.TAKE){
            feeChargerService.tryExtendRent(
                    record.value().getUserId(),
                    record.value().getBookId()
            );
        }
    }
}
