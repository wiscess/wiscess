package com.wiscess.redis.configuration;

import com.wiscess.redis.aop.LockAspect;
import com.wiscess.redis.pojo.MultipleServerProperty;
import com.wiscess.redis.pojo.SingleServerProperty;
import com.wiscess.redis.pojo.WisRedisProperty;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.connection.balancer.LoadBalancer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

/**
 * Created by liuBo
 * 2020/2/20.
 */
@Slf4j
@Configuration
@Import({LockAspect.class})
@ConditionalOnExpression("'${wis.redisson.multipleServerProperty.nodeAddresses[0]:}'.length()>0 or '${wis.redisson.singleServerProperty.address:}'.length()>0 ")
public class WisRedissionConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "wis.redisson")
    public WisRedisProperty wisRedisProperty(){
        log.debug("Redisson configuration inited.");
        return WisRedisProperty.builder().build();
    }

    @Bean
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redissonClient() {
        log.debug("redisonClient configuration inited.");
        WisRedisProperty redisProperty = wisRedisProperty();
        Config config=new Config();
        try {
            //设置编码方式
            config.setCodec((Codec) Class.forName(redisProperty.getCodec()).newInstance());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        config.setTransportMode(redisProperty.getTransportMode());
        if(redisProperty.getThreads()!=null){
            config.setThreads(redisProperty.getThreads());
        }
        if(redisProperty.getNettyThreads()!=null){
            config.setNettyThreads(redisProperty.getNettyThreads());
        }
        config.setReferenceEnabled(redisProperty.getReferenceEnabled());
        config.setLockWatchdogTimeout(redisProperty.getLockWatchdogTimeout());
        config.setKeepPubSubOrder(redisProperty.getKeepPubSubOrder());
        config.setDecodeInExecutor(redisProperty.getDecodeInExecutor());
        config.setUseScriptCache(redisProperty.getUseScriptCache());
        config.setMinCleanUpDelay(redisProperty.getMinCleanUpDelay());
        config.setMaxCleanUpDelay(redisProperty.getMaxCleanUpDelay());

        switch (redisProperty.getRedisModel()){
            case SINGLE:
                //单例的配置
                SingleServerConfig singleServerConfig = config.useSingleServer();
                SingleServerProperty singleServer = redisProperty.getSingleServerProperty();
                singleServerConfig.setAddress(prefixAddress(singleServer.getAddress()));
                singleServerConfig.setConnectionMinimumIdleSize(singleServer.getConnectionMinimumIdleSize());
                singleServerConfig.setConnectionPoolSize(singleServer.getConnectionPoolSize());
                singleServerConfig.setDatabase(singleServer.getDatabase());
                singleServerConfig.setDnsMonitoringInterval(singleServer.getDnsMonitoringInterval());
                singleServerConfig.setSubscriptionConnectionMinimumIdleSize(singleServer.getSubscriptionConnectionMinimumIdleSize());
                singleServerConfig.setSubscriptionConnectionPoolSize(singleServer.getSubscriptionConnectionPoolSize());
                singleServerConfig.setClientName(redisProperty.getClientName());
                singleServerConfig.setConnectTimeout(redisProperty.getConnectTimeout());
                singleServerConfig.setIdleConnectionTimeout(redisProperty.getIdleConnectionTimeout());
                singleServerConfig.setKeepAlive(redisProperty.getKeepAlive());
                singleServerConfig.setPassword(redisProperty.getPassword());
                singleServerConfig.setPingConnectionInterval(redisProperty.getPingConnectionInterval());
                singleServerConfig.setRetryAttempts(redisProperty.getRetryAttempts());
                singleServerConfig.setRetryInterval(redisProperty.getRetryInterval());
                singleServerConfig.setSslEnableEndpointIdentification(redisProperty.getSslEnableEndpointIdentification());
                try{
                    if (redisProperty.getSslKeystore()!=null){
                        singleServerConfig.setSslKeystore(redisProperty.getSslKeystore().toURL());
                    }
                    if (redisProperty.getSslTruststore()!=null){
                        singleServerConfig.setSslTruststore(redisProperty.getSslTruststore().toURL());
                    }
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
                singleServerConfig.setSslKeystorePassword(redisProperty.getSslKeystorePassword());
                singleServerConfig.setSslProvider(redisProperty.getSslProvider());
                singleServerConfig.setSslTruststorePassword(redisProperty.getSslTruststorePassword());
                singleServerConfig.setSubscriptionsPerConnection(redisProperty.getSubscriptionsPerConnection());
                singleServerConfig.setTcpNoDelay(redisProperty.getTcpNoDelay());
                singleServerConfig.setTimeout(redisProperty.getTimeout());
                break;
            case CLUSTER:
                //集群的配置
                ClusterServersConfig clusterServersConfig = config.useClusterServers();
                MultipleServerProperty multipleServer = redisProperty.getMultipleServerProperty();
                clusterServersConfig.setScanInterval(multipleServer.getScanInterval());
                clusterServersConfig.setSlaveConnectionMinimumIdleSize(multipleServer.getSlaveConnectionMinimumIdleSize());
                clusterServersConfig.setSlaveConnectionPoolSize(multipleServer.getSlaveConnectionPoolSize());
                clusterServersConfig.setFailedSlaveReconnectionInterval(multipleServer.getFailedSlaveReconnectionInterval());
                clusterServersConfig.setFailedSlaveCheckInterval(multipleServer.getFailedSlaveCheckInterval());
                clusterServersConfig.setMasterConnectionMinimumIdleSize(multipleServer.getMasterConnectionMinimumIdleSize());
                clusterServersConfig.setMasterConnectionPoolSize(multipleServer.getMasterConnectionPoolSize());
                clusterServersConfig.setReadMode(multipleServer.getReadMode());
                clusterServersConfig.setSubscriptionMode(multipleServer.getSubscriptionMode());
                clusterServersConfig.setSubscriptionConnectionMinimumIdleSize(multipleServer.getSubscriptionConnectionMinimumIdleSize());
                clusterServersConfig.setSubscriptionConnectionPoolSize(multipleServer.getSubscriptionConnectionPoolSize());
                clusterServersConfig.setDnsMonitoringInterval(multipleServer.getDnsMonitoringInterval());
                try {
                    clusterServersConfig.setLoadBalancer((LoadBalancer) Class.forName(multipleServer.getLoadBalancer()).newInstance());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                for (String nodeAddress : multipleServer.getNodeAddresses()) {
                    clusterServersConfig.addNodeAddress(prefixAddress(nodeAddress));
                }
                clusterServersConfig.setClientName(redisProperty.getClientName());
                clusterServersConfig.setConnectTimeout(redisProperty.getConnectTimeout());
                clusterServersConfig.setIdleConnectionTimeout(redisProperty.getIdleConnectionTimeout());
                clusterServersConfig.setKeepAlive(redisProperty.getKeepAlive());
                clusterServersConfig.setPassword(redisProperty.getPassword());
                clusterServersConfig.setPingConnectionInterval(redisProperty.getPingConnectionInterval());
                clusterServersConfig.setRetryAttempts(redisProperty.getRetryAttempts());
                clusterServersConfig.setRetryInterval(redisProperty.getRetryInterval());
                clusterServersConfig.setSslEnableEndpointIdentification(redisProperty.getSslEnableEndpointIdentification());
                try{
                    if (redisProperty.getSslKeystore()!=null){
                        clusterServersConfig.setSslKeystore(redisProperty.getSslKeystore().toURL());
                    }
                    if (redisProperty.getSslTruststore()!=null){
                        clusterServersConfig.setSslTruststore(redisProperty.getSslTruststore().toURL());
                    }
                }catch (Exception e){
                    throw new RuntimeException(e);
                }


                clusterServersConfig.setSslKeystorePassword(redisProperty.getSslKeystorePassword());
                clusterServersConfig.setSslProvider(redisProperty.getSslProvider());
                clusterServersConfig.setSslTruststorePassword(redisProperty.getSslTruststorePassword());
                clusterServersConfig.setSubscriptionsPerConnection(redisProperty.getSubscriptionsPerConnection());
                clusterServersConfig.setTcpNoDelay(redisProperty.getTcpNoDelay());
                clusterServersConfig.setTimeout(redisProperty.getTimeout());
                break;
        }
        return Redisson.create(config);
    }

    private String prefixAddress(String address){
        if(!StringUtils.isEmpty(address)&&!address.startsWith("redis")){
            return "redis://"+address;
        }
        return address;
    }

}
