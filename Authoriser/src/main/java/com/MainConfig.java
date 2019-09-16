package com;

import com.configurations.SecurityConfig;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

public class MainConfig extends Configuration {

    private DataSourceFactory database;

    private SecurityConfig security;

    public SecurityConfig getSecurity() {
        return security;
    }

    public void setSecurity(SecurityConfig security) {
        this.security = security;
    }

    public DataSourceFactory getDatabase() {
        return database;
    }

    public void setDatabase(DataSourceFactory database) {
        this.database = database;
    }
}
