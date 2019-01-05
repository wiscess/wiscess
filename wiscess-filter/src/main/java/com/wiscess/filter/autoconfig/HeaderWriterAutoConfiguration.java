package com.wiscess.filter.autoconfig;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.header.HeaderWriterFilter;

import com.wiscess.filter.header.P3pDisabledWriter;


public class HeaderWriterAutoConfiguration {
	
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
		writers.add( new P3pDisabledWriter());
		return writers;
	}
}
