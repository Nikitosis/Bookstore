package com;

import com.codahale.metrics.health.HealthCheck;
import com.configurations.AppConfig;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.ws.rs.Path;
import java.util.Map;

public class MainApp extends Application<MainConfig> {
    public static void main(String[] args) throws Exception {
        new MainApp().run(args);
    }

    @Override
    public void run(MainConfig mainConfig, Environment environment) throws Exception {
        AnnotationConfigWebApplicationContext parent = new AnnotationConfigWebApplicationContext();
        AnnotationConfigWebApplicationContext ctx=new AnnotationConfigWebApplicationContext();

        parent.refresh();
        parent.getBeanFactory().registerSingleton("configuration", mainConfig);
        parent.registerShutdownHook();
        parent.start();

        ctx.setParent(parent);
        ctx.register(AppConfig.class);
        ctx.refresh();
        ctx.registerShutdownHook();
        ctx.start();

        //healthchecks
        Map<String,HealthCheck> healthChecks = ctx.getBeansOfType(HealthCheck.class);
        for(Map.Entry<String,HealthCheck> entry : healthChecks.entrySet()) {
            environment.healthChecks().register("template", entry.getValue());
        }

        //com.resources
        Map<String,Object> resources = ctx.getBeansWithAnnotation(Path.class);
        for(Map.Entry<String,Object> entry : resources.entrySet()) {
            environment.jersey().register(entry.getValue());
        }
    }
}
