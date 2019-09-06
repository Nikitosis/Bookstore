package com;

import com.configurations.DependencyService;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import java.util.Map;

public class MainConfig extends Configuration {

    private DataSourceFactory database;

    private DependencyService clientBookLoggerService;

    public DependencyService getClientBookLoggerService() {
        return clientBookLoggerService;
    }

    public void setClientBookLoggerService(DependencyService clientBookLoggerService) {
        this.clientBookLoggerService = clientBookLoggerService;
    }

    public DataSourceFactory getDatabase() {
        return database;
    }

    public void setDatabase(DataSourceFactory database) {
        this.database = database;
    }
}
