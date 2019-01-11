package com.wiscess.filter.autoconfig;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.header.writers.CacheControlHeadersWriter;
import org.springframework.security.web.header.writers.ContentSecurityPolicyHeaderWriter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.security.web.header.writers.XContentTypeOptionsHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

import com.wiscess.filter.header.P3pDisabledWriter;
import com.wiscess.utils.StringUtils;


public class HeaderWriterAutoConfiguration {

	@Value("${header.contentSecurityPolicy:}")
	private String policyDirectives;
	@Bean
	public FilterRegistrationBean<HeaderWriterFilter> headerFilterRegistration() {
		List<HeaderWriter> writers = getHeaderWriters();
		if (writers.isEmpty()) {
			throw new IllegalStateException(
					"Headers security is enabled, but no headers will be added. Either add headers or disable headers security");
		}
		HeaderWriterFilter headersFilter = new HeaderWriterFilter(writers);
	    FilterRegistrationBean<HeaderWriterFilter> registration = new FilterRegistrationBean<HeaderWriterFilter>();
	    registration.setFilter(headersFilter);
	    registration.addUrlPatterns("/*");
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
		if(StringUtils.isEmpty(policyDirectives)){
			writers.add(new ContentSecurityPolicyHeaderWriter("default-src 'self' data: 'unsafe-inline' 'unsafe-eval';img-src 'self' data:"));
		}else{
			writers.add(new ContentSecurityPolicyHeaderWriter(policyDirectives));
		}
		writers.add(new ReferrerPolicyHeaderWriter(ReferrerPolicy.SAME_ORIGIN));
		return writers;
	}
}
