package com.wiscess.redis.lock;

import com.wiscess.redis.pojo.LockModel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by liuBo
 * 2020/2/20.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisLock {
    /**
     * 锁的模式:如果不设置,自动模式,当参数只有一个.使用 REENTRANT 参数多个 MULTIPLE
     */
    LockModel lockModel() default LockModel.FAIR;
    /**
     * 作为key的参数
     * @return
     */
    int index() default -1;

    /**
     * key的前缀，为空时，默认前缀是类名+方法名
     * @return
     */
    String prefix() default "";

    /**
     * 锁超时时间,默认30000毫秒(可在配置文件全局设置)
     * @return
     */
    long lockWatchdogTimeout() default 0;

    /**
     * 等待加锁超时时间,默认10000毫秒 -1 则表示一直等待(可在配置文件全局设置)
     * @return
     */
    long attemptTimeout() default 0;
}
