package com;

import io.dropwizard.Configuration;

import java.util.Map;

public class MainConfig extends Configuration {

    private Map<String,String> database;

    public Map<String, String> getDatabase() {
        return database;
    }

    public void setDatabase(Map<String, String> database) {
        this.database = database;
    }
}
