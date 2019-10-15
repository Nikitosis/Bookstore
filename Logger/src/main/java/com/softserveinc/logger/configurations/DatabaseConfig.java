package com.softserveinc.logger.configurations;

import com.softserveinc.logger.MainConfig;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@MapperScan("com.dao")
public class DatabaseConfig {

    @Autowired
    private MainConfig mainConfig;

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean=new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource());
        return factoryBean.getObject();
    }

    @Bean
    public DataSource dataSource(){
        BasicDataSource dataSource=new BasicDataSource();
        dataSource.setDriverClassName(mainConfig.getDatabase().getDriverClass());
        dataSource.setUrl(mainConfig.getDatabase().getUrl());
        dataSource.setUsername(mainConfig.getDatabase().getUser());
        dataSource.setPassword(mainConfig.getDatabase().getPassword());
        return dataSource;
    }
}
