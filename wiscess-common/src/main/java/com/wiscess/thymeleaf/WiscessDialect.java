package com.wiscess.thymeleaf;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

public class WiscessDialect extends AbstractDialect {
	static final String DIALECT_PREFIX_WISCESS    = "app";

	private String prefix=DIALECT_PREFIX_WISCESS;
	
	private final Set<IProcessor> processors = new HashSet<IProcessor>();
	
	@Override
	public String getPrefix() {
		return prefix;
	}

	@Override
	public Set<IProcessor> getProcessors() {
        return processors;
	}

	public void prefix(String prefix) {
		this.prefix=prefix;
	}
	
}
