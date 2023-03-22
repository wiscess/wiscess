package com.wiscess.web;

import java.util.Locale;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * @author wanghai
 */
@Configuration
@EnableScheduling
@EnableConfigurationProperties
public class WebConfig implements WebMvcConfigurer {
    @SuppressWarnings("deprecation")
	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
    	//setUseSuffixPatternMatch(boolean useSuffixPatternMatch)：
    	//设置是否是后缀模式匹配，如“/user”是否匹配/user.*，默认真即匹配；
		//当此参数设置为true的时候，那么/user.html，/user.aa，/user.*都能是正常访问的。
        //setUseTrailingSlashMatch (boolean useSuffixPatternMatch)：
		//设置是否自动后缀路径模式匹配，如“/user”是否匹配“/user/”，默认真即匹配；
		//当此参数设置为true的会后，那么地址/user，/user/都能正常访问。
        configurer.setUseSuffixPatternMatch(false)
                   .setUseTrailingSlashMatch(false);
    }

	/*
	 * 设置通过URL参数改变语言环境
	 */
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		lci.setParamName("lang");
		return lci;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}

	//@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver slr = new SessionLocaleResolver();
		slr.setDefaultLocale(Locale.CHINA);
		return slr;
	}

	/*
	 * 设置资源文件路径和字符集
	 */
	@Bean
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:/locale/messages");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setCacheSeconds(3600); // refresh cache once per hour
		return messageSource;
	}

	/*
	 * 让Bean验证也使用messages.properties，并支持UTF-8编码
	 */
	@Override
	public Validator getValidator() {
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.setValidationMessageSource(messageSource());
		return validator;
	}

}
