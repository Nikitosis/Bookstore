package com;

import com.codahale.metrics.health.HealthCheck;
import com.configurations.AppConfig;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.migrations.CloseableLiquibase;
import io.dropwizard.migrations.CloseableLiquibaseWithClassPathMigrationsFile;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.ws.rs.Path;
import java.util.EnumSet;
import java.util.Map;

public class MainApp extends Application<MainConfig> {
    public static void main(String[] args) throws Exception {
        new MainApp().run(args);
    }

    @Override
    public void initialize(Bootstrap<MainConfig> bootstrap) {
        //to use environment variables inside configuration.yml
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );

        //for swagger
        bootstrap.addBundle(new SwaggerBundle<MainConfig>(){
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(MainConfig mainConfig) {
                return mainConfig.getSwagger();
            }
        });
    }

    @Override
    public void run(MainConfig mainConfig, Environment environment) throws Exception {
        AnnotationConfigWebApplicationContext parent = new AnnotationConfigWebApplicationContext();
        AnnotationConfigWebApplicationContext ctx=new AnnotationConfigWebApplicationContext();

        //first, register MainConfig in application context(to inject it into other @Configuration-s later on)
        parent.refresh();
        parent.getBeanFactory().registerSingleton("configuration", mainConfig);
        parent.registerShutdownHook();
        parent.start();

        ctx.setParent(parent);
        ctx.register(AppConfig.class);
        ctx.refresh();
        ctx.registerShutdownHook();
        ctx.start();

        //register healthchecks
        Map<String,HealthCheck> healthChecks = ctx.getBeansOfType(HealthCheck.class);
        for(Map.Entry<String,HealthCheck> entry : healthChecks.entrySet()) {
            environment.healthChecks().register("template", entry.getValue());
        }

        //register resources
        Map<String,Object> resources = ctx.getBeansWithAnnotation(Path.class);
        for(Map.Entry<String,Object> entry : resources.entrySet()) {
            environment.jersey().register(entry.getValue());
        }

        //setting sessionHandler
        environment.servlets().setSessionHandler(new SessionHandler());

        //last, but not least, let's link Spring to the embedded Jetty in Dropwizard
        environment.servlets().addServletListeners(new ContextLoaderListener(ctx));

        //add Spring Security filter
        FilterRegistration.Dynamic filterRegistration=environment.servlets().addFilter("springSecurityFilterChain", DelegatingFilterProxy.class);
        filterRegistration.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class),false,"/*");
    }
}
