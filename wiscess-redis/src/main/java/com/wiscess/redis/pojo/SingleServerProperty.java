package com.wiscess.redis.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by liuBo
 * 2020/2/20.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SingleServerProperty {
    private String address;
    @Builder.Default
    private Integer subscriptionConnectionMinimumIdleSize = 1;
    @Builder.Default
    private Integer subscriptionConnectionPoolSize = 50;
    @Builder.Default
    private Integer connectionMinimumIdleSize = 32;
    @Builder.Default
    private Integer connectionPoolSize = 64;
    @Builder.Default
    private Integer database = 0;
    @Builder.Default
    private Long dnsMonitoringInterval = 5000L;
}
