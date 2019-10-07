package com;

import com.configuration.DependencyService;
import com.configuration.FeeChargeConfig;
import com.configuration.OktaOAuthConfig;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

public class MainConfig extends Configuration {

    private DataSourceFactory database;

    private DependencyService libraryService;

    private FeeChargeConfig feeChargeConfig;

    private OktaOAuthConfig oktaOAuth;

    public DataSourceFactory getDatabase() {
        return database;
    }

    public void setDatabase(DataSourceFactory database) {
        this.database = database;
    }

    public DependencyService getLibraryService() {
        return libraryService;
    }

    public void setLibraryService(DependencyService libraryService) {
        this.libraryService = libraryService;
    }

    public FeeChargeConfig getFeeChargeConfig() {
        return feeChargeConfig;
    }

    public void setFeeChargeConfig(FeeChargeConfig feeChargeConfig) {
        this.feeChargeConfig = feeChargeConfig;
    }

    public OktaOAuthConfig getOktaOAuth() {
        return oktaOAuth;
    }

    public void setOktaOAuth(OktaOAuthConfig oktaOAuth) {
        this.oktaOAuth = oktaOAuth;
    }
}
