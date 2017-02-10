package com.wiscess.jpa.config;

import javax.annotation.Resource;
import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.support.JmxUtils;

import com.wiscess.common.utils.StringUtil;
import com.wiscess.util.DesUtils;

@Slf4j
@Configuration
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
    @ConfigurationProperties(prefix=DEFAULT_PREFIX)
    public DataSource dataSource(){
    	log.info("************配置主数据源***********");
    	return createDatasource(DEFAULT_PREFIX);
    }
	
	public DataSource createDatasource(String prifix){
		String jndiName=env.getProperty(prifix+".jndiName");
    	if(StringUtil.isNotEmpty(jndiName)){
    		//调用jndi
    		log.info(jndiName);
    		JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
    		DataSource dataSource = dataSourceLookup.getDataSource(jndiName);
    		excludeMBeanIfNecessary(dataSource, "dataSource");
    		return dataSource;
    	}
    	String url=readParameter(prifix,"url");
    	String username=readParameter(prifix,"username");
    	String password=readParameter(prifix,"password");
    	
    	return DataSourceBuilder.create()
				.url(url)
				.username(username)
				.password(password)
				.build();
	}
    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("dataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
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
