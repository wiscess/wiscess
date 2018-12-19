package com.wiscess.filter.autoconfig;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.header.writers.CacheControlHeadersWriter;
import org.springframework.security.web.header.writers.ContentSecurityPolicyHeaderWriter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XContentTypeOptionsHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter.XFrameOptionsMode;

import com.wiscess.filter.header.P3pDisabledWriter;
import com.wiscess.utils.StringUtils;

import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;

@ConditionalOnWebApplication
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
	    registration.setOrder(4);
	    return registration;
	} 
	private List<HeaderWriter> getHeaderWriters() {
		List<HeaderWriter> writers = new ArrayList<HeaderWriter>();
		addIfNotNull(writers, new P3pDisabledWriter());
		addIfNotNull(writers, new CacheControlHeadersWriter());
		if(StringUtils.isNotEmpty(policyDirectives)) {
			addIfNotNull(writers, new ContentSecurityPolicyHeaderWriter(policyDirectives));
		}
//		addIfNotNull(writers, new DelegatingRequestMatcherHeaderWriter());
		//addIfNotNull(writers, new HpkpHeaderWriter());
		//强制使用https传输
		//addIfNotNull(writers, new HstsHeaderWriter());
		addIfNotNull(writers, new ReferrerPolicyHeaderWriter(ReferrerPolicy.SAME_ORIGIN));
//		addIfNotNull(writers, new StaticHeadersWriter());
		addIfNotNull(writers, new XContentTypeOptionsHeaderWriter());
		addIfNotNull(writers, new XXssProtectionHeaderWriter());
		addIfNotNull(writers, new XFrameOptionsHeaderWriter(XFrameOptionsMode.SAMEORIGIN));
		return writers;
	}
	private <T> void addIfNotNull(List<T> values, T value) {
		if (value != null) {
			values.add(value);
		}
	}

}
