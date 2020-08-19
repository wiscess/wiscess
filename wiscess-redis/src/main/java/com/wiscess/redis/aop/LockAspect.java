package com.wiscess.redis.aop;

import com.wiscess.redis.lock.RedisLock;
import com.wiscess.redis.pojo.LockModel;
import com.wiscess.redis.pojo.WisRedisProperty;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by liuBo
 * 2020/2/20.
 */
@Aspect
@Component
@Order(1)
@Slf4j
public class LockAspect {
    @Autowired
    private WisRedisProperty wisRedisProperty;
    @Autowired
    private RedissonClient redissonClient;

    @Around("@annotation(redisLock)")
    public Object around(ProceedingJoinPoint joinPoint,RedisLock redisLock) throws Throwable{
        String key = "";
        //可以在参数中注入redisLock，也可以通过下面的方法获取注解
        //MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //RedisLock redisLock = signature.getMethod().getAnnotation(RedisLock.class);

        if ("".equals(redisLock.prefix())){
            //锁前缀为空时，默认使用类名和方法名
            key = joinPoint.getSignature().getDeclaringTypeName()+"-"+joinPoint.getSignature().getName();
        }else{
            key = redisLock.prefix();
        }
        //使用index的参数作为key，默认为-1，不使用参数
        if (redisLock.index()!=-1&&joinPoint.getArgs().length>redisLock.index()){
            key += joinPoint.getArgs()[redisLock.index()].toString();
        }
        //等待加锁超时时间
        long attemptTimeout = redisLock.attemptTimeout();
        if (attemptTimeout == 0) {
            attemptTimeout = wisRedisProperty.getAttemptTimeout();
        }
        //锁超时时间,默认30000毫秒
        long lockWatchdogTimeout = redisLock.lockWatchdogTimeout();
        if (lockWatchdogTimeout == 0) {
            lockWatchdogTimeout = wisRedisProperty.getLockWatchdogTimeout();
        }
        //锁模式
        LockModel lockModel = redisLock.lockModel();
        if (lockModel.equals(LockModel.AUTO)) {
            LockModel defaultLockModel = wisRedisProperty.getLockModel();
            if (defaultLockModel != null) {
                lockModel = defaultLockModel;
            } else {
                lockModel = LockModel.REENTRANT;
            }
        }
        log.debug("锁模式->{},等待锁定时间->{}秒.锁定最长时间->{}秒",lockModel.name(),attemptTimeout/1000,lockWatchdogTimeout/1000);
        boolean res = false;
        RLock rLock = null;
        //一直等待加锁.
        switch (lockModel) {
            case FAIR:
                rLock = redissonClient.getFairLock(key);
                break;
            case REDLOCK:
                List<RLock> rLocks=new ArrayList<>();
                rLocks.add(redissonClient.getLock(key));
                RLock[] locks=new RLock[rLocks.size()];
                int index=0;
                for (RLock r : rLocks) {
                    locks[index++]=r;
                }
                rLock = new RedissonRedLock(locks);
                break;
            case MULTIPLE:
                rLock= redissonClient.getLock(key);
                break;
            case REENTRANT:
                rLock= redissonClient.getLock(key);
                break;
            case READ:
                RReadWriteLock rwlock = redissonClient.getReadWriteLock(key);
                rLock = rwlock.readLock();
                break;
            case WRITE:
                RReadWriteLock rwlock1 = redissonClient.getReadWriteLock(key);
                rLock = rwlock1.writeLock();
                break;
        }
        //执行aop
        if(rLock!=null) {
            try {
                if (attemptTimeout == -1) {
                    res = true;
                    //一直等待加锁
                    rLock.lock(lockWatchdogTimeout, TimeUnit.MILLISECONDS);
                } else {
                    res = rLock.tryLock(attemptTimeout, lockWatchdogTimeout, TimeUnit.MILLISECONDS);
                }
                if (res) {
                    Object result = joinPoint.proceed();
                    return result;
                }else{
                    throw new RuntimeException("超时，获取锁失败。请合理调整attemptTimeout、lockWatchdogTimeout");
                }
            } finally {
                if (res) {
                    rLock.unlock();
                }
            }
        }
        throw new RuntimeException("获取锁失败");
    }

}
