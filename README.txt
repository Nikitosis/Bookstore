Brief explanation. Will be modified soon.

Support for logging with correlation id(in particular microservice)

First of all you have to include logback.xml and configure logger to use it:
	<dependency>
            <groupId>net.logstash.logback</groupId>
            <artifactId>logstash-logback-encoder</artifactId>
            <version>6.2</version>
        </dependency>

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();
        ContextInitializer initializer = new ContextInitializer(context);
        initializer.autoConfig();

Doing so instead of configuring logger in configuration.yml prevents microservice from falling if logstash is not available

-register correlationFilter after all fiters are set:
	//filter for setting correlationId
        FilterRegistration.Dynamic correlationFilter=environment.servlets().addFilter("correlationFilter", HttpCorrelationFilter.class);
        correlationFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class),false,"/*");
	
It automatically intercepts http requests and sets mdc correlationId field.
So that, logger includes this correlationId in logs.
If no correlation id presents, it will generate new one and set mdc correlationId field.

-if using jax.rs.Client, be sure to register HttpCorrelationInterceptor to the Client:
	ClientBuilder.newClient().register(HttpClientCorrelationFilter.class);

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