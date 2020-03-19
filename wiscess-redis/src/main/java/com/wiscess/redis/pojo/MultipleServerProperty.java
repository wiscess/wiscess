package com.wiscess.redis.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.redisson.config.ReadMode;
import org.redisson.config.SubscriptionMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuBo
 * 2020/2/20.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MultipleServerProperty {
    @Builder.Default
    private String loadBalancer = "org.redisson.connection.balancer.RoundRobinLoadBalancer";
    @Builder.Default
    private Integer slaveConnectionMinimumIdleSize = 32;
    @Builder.Default
    private Integer slaveConnectionPoolSize = 64;
    @Builder.Default
    private Integer failedSlaveReconnectionInterval = 3000;
    @Builder.Default
    private Integer failedSlaveCheckInterval = 180000;
    @Builder.Default
    private Integer masterConnectionMinimumIdleSize = 32;
    @Builder.Default
    private Integer masterConnectionPoolSize = 64;
    @Builder.Default
    private ReadMode readMode= ReadMode.SLAVE;
    @Builder.Default
    private SubscriptionMode subscriptionMode=SubscriptionMode.SLAVE;
    @Builder.Default
    private Integer subscriptionConnectionMinimumIdleSize=1;
    @Builder.Default
    private Integer subscriptionConnectionPoolSize=50;
    @Builder.Default
    private Long dnsMonitoringInterval=5000L;
    @Builder.Default
    private List<String> nodeAddresses = new ArrayList<>();
    //集群,哨兵,云托管
    @Builder.Default
    private Integer scanInterval = 1000;
    //哨兵模式,云托管,主从
    @Builder.Default
    private Integer database = 0;
    //哨兵模式
    private String masterName;
}
