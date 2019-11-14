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

To sum up, this configuration enables microservices to share the same correlationId's.
It helps developers to understand bugs and track all request way through microservices.