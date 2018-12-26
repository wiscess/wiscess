package com.wiscess.jpa.config;

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
import org.springframework.boot.jdbc.DataSourceBuilder;
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

import com.wiscess.utils.StringUtils;
import com.wiscess.utils.DesUtils;

@Slf4j
@Configuration
@AutoConfigureBefore({ JndiDataSourceAutoConfiguration.class,
	XADataSourceAutoConfiguration.class,
	DataSourceAutoConfiguration.class })
@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class })
@EnableConfigurationProperties(DataSourceProperties.class)
public class MultiDataSourceConfig {

	private final String DEFAULT_PREFIX="spring.datasource";
	
	private final ApplicationContext context;
	
	public MultiDataSourceConfig(ApplicationContext context) {
		this.context = context;
	}
	@Resource
    protected Environment env;
	
	@Bean(destroyMethod = "")
    @Primary
    @ConfigurationProperties(prefix=DEFAULT_PREFIX)
    public DataSource dataSource(){
    	log.info("************Config Main Datasource***********");
    	return createDatasource(DEFAULT_PREFIX,"dataSource");
    }
	
	public DataSource createDatasource(String prefix,String dataSourceName){
		String jndiName=env.getProperty(prefix+".jndiName");
    	if(StringUtils.isNotEmpty(jndiName)){
    		//调用jndi
    		log.info(jndiName);
    		JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
    		DataSource dataSource = dataSourceLookup.getDataSource(jndiName);
    		excludeMBeanIfNecessary(dataSource, dataSourceName);
    		return dataSource;
    	}
    	//普通模式的datasource
    	String url=readParameter(prefix,"url");
    	String username=readParameter(prefix,"username");
    	String password=readParameter(prefix,"password");
//    	String typeName=readParameter(prefix, "type");
//    	Class<? extends DataSource> type = null;
//    	if(StringUtils.isNotEmpty(typeName)) {
//    		try {
//    			type = (Class<? extends DataSource>) ClassUtils.forName(typeName,null);
//    		} catch (ClassNotFoundException e) {
//    			e.printStackTrace();
//    		} catch (LinkageError e) {
//    			e.printStackTrace();
//    		}
//    	}
    	
    	return DataSourceBuilder.create()
				.url(url)
				.username(username)
				.password(password)
//				.type(type)
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
