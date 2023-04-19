package com.wiscess.thymeleaf;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import com.wiscess.thymeleaf.processor.TreeInputProcessor;

@Slf4j
@AutoConfiguration
public class ThymeleafWebWiscessConfiguration {
	
	public void configure(WiscessDialect dialect){
	}
	@Bean(name="wiscessDialect")
	@ConditionalOnMissingBean
	public WiscessDialect wiscessDialect(){
		log.info("ThymeleafWebWiscessConfiguration init.");
		WiscessDialect wiscessDialect=new WiscessDialect();
		configure(wiscessDialect);
		wiscessDialect.addProcessor(new TreeInputProcessor(wiscessDialect.getPrefix()));
		return wiscessDialect;
	}
	
}
