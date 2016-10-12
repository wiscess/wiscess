package com.wiscess.jpa.config;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@Configuration
public class MultiDataSourceConfig {

	@Bean
    @Primary
    @ConfigurationProperties(prefix="spring.dataSource")
    public DataSource dataSource() {
    	log.debug("************配置主数据源***********");
    	
    	return DataSourceBuilder.create().build();
    }
    @Bean
    public JdbcTemplate jdbcTemplate(
            @Qualifier("dataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
    
    @Bean(name = "dataSource1")
    @Qualifier("dataSource1")
    @ConfigurationProperties(prefix="spring.datasource1")
    public DataSource dataSource1() {
    	log.debug("************配置次数据源***********");
        return DataSourceBuilder.create().build();
    }
    @Bean(name = "jdbcTemplate1")
    @Qualifier("jdbcTemplate1")
    public JdbcTemplate jdbcTemplate1(
            @Qualifier("dataSource1") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
