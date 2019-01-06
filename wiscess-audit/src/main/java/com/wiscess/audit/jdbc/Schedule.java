package com.wiscess.audit.jdbc;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.wiscess.audit.autoconfig.properties.AuditProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * 定时任务
 *
 * 定时设置说明：
 * fixedRate：不管任务是否完成，到间隔时间就进入任务队列，直到上一个任务完成，如果上个任务运行时间超过间隔时间，
 * 则任务队列中会越来越多
 * initialDelay和fixedDelay：从启动开始计算，initialDelay时间后开始执行第一次，当任务完成后，间隔fixedDelay时间再执行下一个任务
 * cron：表示定时启动，"50 16 20 * * ?"为每天20点16分50秒启动
 * @author wh
 *
 */
@Component
@Slf4j
@PropertySource(value = "classpath:application.yml")
public class Schedule {
	@Autowired
	private AuditProperties properties;
	@Autowired
	private AuditService auditService;
	//第一次延迟10秒执行，当执行完后60秒再执行
    @Scheduled(initialDelay = 10000, fixedDelay = 30*1000)
    @Transactional
    public void saveAuditLog() {
    	if(!properties.isEnable())
    		return;
    	log.debug("batchSaveAuditLog.");
    	//执行保存方法
    	auditService.batchSave();
    }
}
