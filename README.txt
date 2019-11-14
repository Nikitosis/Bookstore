Brief explanation. Will be modified soon.

Support for logging with correlation id(in particular microservice)

-register HttpCorrelationInterceptor:
	environment.jersey().register(HttpCorrelationInterceptor.class);
It automatically intercepts http requests and sets mdc correlationId field.
So that, logger includes this correlationId in logs.
If no correlation id presents, it will generate new one and set mdc correlationId field.

-if using jax.rs.Client, be sure to add header with correlationId to the request:
	header(CorrelationConstraints.CORRELATION_ID_HEADER_NAME,MDC.get(CorrelationConstraints.CORRELATION_ID_LOG_VAR_NAME))
It gets correlationId from mdc and sets header.

-if using Kafka Producer, be sure to add header with corellationId to kafka ProducerRecord:
	record.headers().add(CorrelationConstraints.CORRELATION_ID_HEADER_NAME, CorrelationManager.getCorrelationId().getBytes());
	
-if using Kafka Consumer(KafkaListener), be sure to get header from kafka message's header and set correlationId:
	@KafkaListener(topics = "#{@userBookActionTopic}",containerFactory = "userBookActionListener")
    public void consume(ConsumerRecord<String, AvroUserBookAction> record,
                                    @Header(CorrelationConstraints.CORRELATION_ID_HEADER_NAME) String correlationId){							
		CorrelationManager.setCorrelationId(correlationId);
		...
		CorrelationManager.removeCorrelationId();
    }

To sum up, this configuration enables microservices to share the same correlationId's.
It helps developers to understand bugs and track all request way through microservices.