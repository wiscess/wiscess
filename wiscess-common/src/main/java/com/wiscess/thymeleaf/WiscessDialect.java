package com.wiscess.thymeleaf;

import java.util.LinkedHashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

public class WiscessDialect extends AbstractProcessorDialect  {

    public static final String DIALECT_NAME = "App Dialect";//定义方言名称
	
    public static final String DIALECT_PREFIX = "app";//定义方言前缀

	protected WiscessDialect() {
		// 我们将设置此方言与“方言处理器”优先级相同
        // 标准方言, 以便处理器执行交错。
        super(DIALECT_NAME, DIALECT_PREFIX, StandardDialect.PROCESSOR_PRECEDENCE);
	}

	private final Set<IProcessor> processors = new LinkedHashSet<IProcessor>();
	
	/**
	 * 返回自定义标签
	 */
	@Override
	public Set<IProcessor> getProcessors(String dialectPrefix) {
		return processors;
	}

	/**
	 * 添加自定义标签
	 * @param processor
	 */
	public void addProcessor(IProcessor processor) {
		processors.add(processor);
	}
}
