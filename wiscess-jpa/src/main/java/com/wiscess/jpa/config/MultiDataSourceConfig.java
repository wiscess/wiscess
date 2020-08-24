package com.wiscess.jpa.config;

import java.sql.SQLException;

import javax.annotation.Resource;
import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JndiDataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.support.JmxUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.wiscess.utils.DesUtils;
import com.wiscess.utils.StringUtils;
@Slf4j
@Configuration
@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class })
@AutoConfigureBefore({ JndiDataSourceAutoConfiguration.class,
	XADataSourceAutoConfiguration.class,
	DataSourceAutoConfiguration.class })
@EnableConfigurationProperties(DataSourceProperties.class)
public class MultiDataSourceConfig {

	private final String DEFAULT_PREFIX="spring.datasource";

	private final ApplicationContext context;
	
	public MultiDataSourceConfig(ApplicationContext context) {
		this.context = context;
	}
	@Resource
    protected Environment env;
	
	@Bean
    @Primary
    @Qualifier("primaryDruidConfig")
    @ConfigurationProperties(prefix = DEFAULT_PREFIX)
    public DruidConfig primaryDruidConfig(){
        return new DruidConfig();
    }
    
	@Bean(initMethod = "init",destroyMethod = "close")
	@Primary
	@Qualifier("dataSource") 
    @ConfigurationProperties(prefix=DEFAULT_PREFIX)
    public DataSource dataSource(@Qualifier("primaryDruidConfig") DruidConfig druidConfig){
    	log.info("************Config Main Druid Datasource***********");
    	return initDruidDataSource(druidConfig, "dataSource");
    }
	
    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("dataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
    public DataSource initDruidDataSource(DruidConfig config,String dataSourceName) {
    	if(StringUtils.isNotEmpty(config.getJndiName())){
    		//调用jndi
    		String jndiName=config.getJndiName();
    		log.info("init jndiName dataSource:"+jndiName);
    		JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
    		DataSource dataSource = dataSourceLookup.getDataSource(jndiName);
    		excludeMBeanIfNecessary(dataSource, dataSourceName);
    		return dataSource;
    	}
	    DruidDataSource datasource = new DruidDataSource();

	    datasource.setUrl(config.getUrl());
	    datasource.setUsername(config.getUsername());
	    datasource.setPassword(config.getPassword());
	    datasource.setDriverClassName(config.getDriverClassName());
	    datasource.setInitialSize(config.getInitialSize());
	    datasource.setMinIdle(config.getMinIdle());
	    datasource.setMaxActive(config.getMaxActive());

	    // common config
	    datasource.setMaxWait(config.getMaxWait());
	    datasource.setTimeBetweenEvictionRunsMillis(config.getTimeBetweenEvictionRunsMillis());
	    datasource.setMinEvictableIdleTimeMillis(config.getMinEvictableIdleTimeMillis());
	    datasource.setMaxEvictableIdleTimeMillis(config.getMaxEvictableIdleTimeMillis());
    	String validationQuery=config.getValidationQuery();
	    if(StringUtils.isEmpty(validationQuery)) {
	    	//根据类型自动设置一个query
	    	String driverName=config.getDriverClassName();
	    	if(driverName.contains("sqlserver")) {
	    		validationQuery="select 1  ";
	    	}else if(driverName.contains("mysql")) {
	    		validationQuery="select 1  from dual";
	    	}
	    }
    	datasource.setValidationQuery(validationQuery);
    	
	    datasource.setTestWhileIdle(config.isTestWhileIdle());
	    datasource.setTestOnBorrow(config.isTestOnBorrow());
	    datasource.setTestOnReturn(config.isTestOnReturn());
	    datasource.setPoolPreparedStatements(config.isPoolPreparedStatements());
	    datasource.setMaxPoolPreparedStatementPerConnectionSize(config.getMaxPoolPreparedStatementPerConnectionSize());
	    try {
	        datasource.setFilters(config.getFilters());
	    } catch (SQLException e) {
	        log.error("druid configuration initialization filter : {0}", e);
	    }
	    datasource.setConnectionProperties(config.getConnectionProperties());

	    return datasource;
	}
	
	protected void excludeMBeanIfNecessary(Object candidate, String beanName) {
		try {
			MBeanExporter mbeanExporter = this.context.getBean(MBeanExporter.class);
			if (JmxUtils.isMBean(candidate.getClass())) {
				mbeanExporter.addExcludedBean(beanName);
			}
		}
		catch (NoSuchBeanDefinitionException ex) {
		}
	}
    /**
     * 读取参数，如果有加密的参数，则处理加密的参数
     * @param prefix
     * @param originPara
     * @return
     */
    protected String readParameter(String prefix,String originPara){
    	if(env.containsProperty(prefix+"."+originPara)){
    		return env.getProperty(prefix+"."+originPara);
    	}
    	String encryptedName="encrypted"+toUpperFirst(originPara);
    	if(env.containsProperty(prefix+"."+encryptedName)){
    		return DesUtils.decrypt(env.getProperty(prefix+"."+encryptedName));
    	}
    	return "";
    }
    
    protected static String toUpperFirst(String str){
    	return str.replaceFirst(str.substring(0, 1),str.substring(0, 1).toUpperCase()) ;
    }
}
