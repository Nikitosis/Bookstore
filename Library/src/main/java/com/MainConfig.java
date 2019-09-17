package com;

import com.configurations.DependencyService;
import com.configurations.SecurityConfig;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import java.util.Map;

public class MainConfig extends Configuration {

    private DataSourceFactory database;

    private DependencyService clientBookLoggerService;

    private SecurityConfig security;

    public DataSourceFactory getDatabase() {
        return database;
    }

    public void setDatabase(DataSourceFactory database) {
        this.database = database;
    }

    public DependencyService getClientBookLoggerService() {
        return clientBookLoggerService;
    }

    public void setClientBookLoggerService(DependencyService clientBookLoggerService) {
        this.clientBookLoggerService = clientBookLoggerService;
    }

    public SecurityConfig getSecurity() {
        return security;
    }

    public void setSecurity(SecurityConfig security) {
        this.security = security;
    }
}
