package com;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

public class MainConfig extends Configuration {

    private DataSourceFactory database;

    public DataSourceFactory getDatabase() {
        return database;
    }

    public void setDatabase(DataSourceFactory database) {
        this.database = database;
    }
}
