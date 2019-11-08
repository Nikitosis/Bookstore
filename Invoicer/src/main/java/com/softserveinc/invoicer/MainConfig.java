package com.softserveinc.invoicer;

import com.softserveinc.cross_api_objects.configuration.AwsS3Config;
import com.softserveinc.invoicer.configurations.InvoicerConfig;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

public class MainConfig extends Configuration {

    private DataSourceFactory database;

    private InvoicerConfig invoicerConfig;

    private AwsS3Config awsS3Config;

    public DataSourceFactory getDatabase() {
        return database;
    }

    public void setDatabase(DataSourceFactory database) {
        this.database = database;
    }

    public InvoicerConfig getInvoicerConfig() {
        return invoicerConfig;
    }

    public void setInvoicerConfig(InvoicerConfig invoicerConfig) {
        this.invoicerConfig = invoicerConfig;
    }

    public AwsS3Config getAwsS3Config() {
        return awsS3Config;
    }

    public void setAwsS3Config(AwsS3Config awsS3Config) {
        this.awsS3Config = awsS3Config;
    }
}