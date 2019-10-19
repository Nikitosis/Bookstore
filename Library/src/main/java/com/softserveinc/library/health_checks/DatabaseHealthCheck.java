package com.softserveinc.library.health_checks;

import com.codahale.metrics.health.HealthCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DatabaseHealthCheck extends HealthCheck {
    private final DataSource dataSource;

    @Autowired
    public DatabaseHealthCheck(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected Result check() throws Exception {
        if(dataSource.getConnection().isValid(1000)){
            return Result.healthy();
        }
        else{
            return Result.unhealthy("Cannot connect to database");
        }
    }
}
