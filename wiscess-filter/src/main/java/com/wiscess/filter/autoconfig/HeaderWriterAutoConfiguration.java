package com.wiscess.filter.autoconfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.header.writers.CacheControlHeadersWriter;
import org.springframework.security.web.header.writers.ContentSecurityPolicyHeaderWriter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.security.web.header.writers.XContentTypeOptionsHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

import com.wiscess.filter.StaticResourceHeaderFilter;
import com.wiscess.filter.header.P3pDisabledWriter;
import com.wiscess.utils.StringUtils;

@Configuration
@ConditionalOnWebApplication
public class HeaderWriterAutoConfiguration {

	@Value("${header.enableContentSecurityPolicy:true}")
	private boolean enableContentSecurityPolicy;

	@Value("${header.contentSecurityPolicy:}")
	private String policyDirectives;

	@Value("${header.referrerPolicy:}")
	private String referrerPolicy;

    @Autowired
    private Environment env;
    
	@Bean
	public FilterRegistrationBean<HeaderWriterFilter> headerFilterRegistration() {
		List<HeaderWriter> writers = getHeaderWriters();
		if (writers.isEmpty()) {
			throw new IllegalStateException(
					"Headers security is enabled, but no headers will be added. Either add headers or disable headers security");
		}
		//加载List参数，首先加载权限定义中的忽略认证的路径
		List<String> ignoreList = getPropertyList("security.ignored");
		//再次加载资源配置的路径
		ignoreList.addAll(getPropertyList("spring.resources.location"));
		
		HeaderWriterFilter headersFilter = new StaticResourceHeaderFilter(writers,ignoreList);
	    FilterRegistrationBean<HeaderWriterFilter> registration = new FilterRegistrationBean<HeaderWriterFilter>();
	    registration.setFilter(headersFilter);
	    registration.addUrlPatterns("/**");
	    registration.setName("headerWriteFilter");
	    registration.setOrder(-9990);
	    return registration;
	} 
	private List<HeaderWriter> getHeaderWriters() {
		List<HeaderWriter> writers = new ArrayList<HeaderWriter>();
		writers.add(new P3pDisabledWriter());
		writers.add(new XContentTypeOptionsHeaderWriter());
		writers.add(new XXssProtectionHeaderWriter());
		writers.add(new CacheControlHeadersWriter());
		if(enableContentSecurityPolicy) {
			if(StringUtils.isEmpty(policyDirectives)){
				writers.add(new ContentSecurityPolicyHeaderWriter("default-src 'self' data: 'unsafe-inline' 'unsafe-eval';img-src 'self' data:"));
			}else{
				writers.add(new ContentSecurityPolicyHeaderWriter(policyDirectives));
			}
		}
		if(StringUtils.isNotEmpty(referrerPolicy)) {
			writers.add(new ReferrerPolicyHeaderWriter(ReferrerPolicy.valueOf(referrerPolicy)));
		}
		return writers;
	}
	
	/**
	 * 加载参数的List数据
	 * @param propertyName
	 * @return
	 */
	private List<String> getPropertyList(String propertyName){
		List<String> result = new ArrayList<String>();
		//先直接取参数
		if(env.containsProperty(propertyName)) {
			String value = env.getProperty(propertyName);
			if(StringUtils.isNotEmpty(value)) {
				result.addAll(Arrays.asList(value.split(",")));
			}
		}else {
			//按List方式读取
			for (int i = 0; i < Integer.MAX_VALUE; i++) {
				String value = env.getProperty(propertyName+"[" + i + "]");
				if (value == null) {
					//没有数据跳出循环
					break;
				}
				result.add(value);
			}
		}
		return result;
	}
}
