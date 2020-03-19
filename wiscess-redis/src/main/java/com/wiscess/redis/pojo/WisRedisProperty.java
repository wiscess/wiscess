package com.wiscess.redis.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.redisson.config.SslProvider;
import org.redisson.config.TransportMode;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.net.URI;

/**
 * Created by liuBo
 * 2020/2/20.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WisRedisProperty {
    //集群模式:SINGLE(单例),SENTINEL(哨兵),MASTERSLAVE(主从),CLUSTER(集群),REPLICATED(云托管)
    @Builder.Default
    private RedisModel redisModel = RedisModel.SINGLE;
    //Redisson的对象编码类是用于将对象进行序列化和反序列化，以实现对该对象在Redis里的读取和存储
    @Builder.Default
    private String codec="org.redisson.codec.JsonJacksonCodec";
    //默认当前处理核数量 * 2	 这个线程池数量被所有RTopic对象监听器，RRemoteService调用者和RExecutorService任务共同共享。
    private Integer threads;
    //默认当前处理核数量 * 2	 这个线程池数量是在一个Redisson实例内，被其创建的所有分布式数据类型和服务，以及底层客户端所一同共享的线程池里保存的线程数量。
    private Integer nettyThreads;
    //如果当前连接池里的连接数量超过了最小空闲连接数，而同时有连接空闲时间超过了该数值，那么这些连接将会自动被关闭，并从连接池里去掉。时间单位是毫秒
    @Builder.Default
    private Integer idleConnectionTimeout = 10000;
    //同任何节点建立连接时的等待超时。时间单位是毫秒。
    @Builder.Default
    private Integer connectTimeout = 10000;
    //等待节点回复命令的时间。该时间从命令发送成功时开始计时。
    @Builder.Default
    private Integer timeout = 3000;
    //如果尝试达到 retryAttempts（命令失败重试次数） 仍然不能将命令发送至某个指定的节点时，将抛出错误。如果尝试在此限制之内发送成功，则开始启用 timeout（命令等待超时） 计时。
    @Builder.Default
    private Integer retryAttempts = 3;
    @Builder.Default
    private Integer retryInterval = 1500;
    //用于节点身份验证的密码。
    private String password;
    @Builder.Default
    private Integer subscriptionsPerConnection = 5;
    @Builder.Default
    private Boolean keepAlive=false;
    @Builder.Default
    private Boolean tcpNoDelay=false;
    @Builder.Default
    private Boolean referenceEnabled = true;
    //监控锁的看门狗超时时间单位为毫秒。该参数只适用于分布式锁的加锁请求中未明确使用leaseTimeout参数的情况。如果该看门口未使用lockWatchdogTimeout去重新调整一个分布式锁的lockWatchdogTimeout超时，那么这个锁将变为失效状态。这个参数可以用来避免由Redisson客户端节点宕机或其他原因造成死锁的情况。
    @Builder.Default
    private Long lockWatchdogTimeout=30000L;
    //通过该参数来修改是否按订阅发布消息的接收顺序出来消息，如果选否将对消息实行并行处理，该参数只适用于订阅发布消息的情况。
    @Builder.Default
    private Boolean keepPubSubOrder=true;
    @Builder.Default
    private Boolean decodeInExecutor=false;
    @Builder.Default
    private Boolean useScriptCache=false;
    @Builder.Default
    private Integer minCleanUpDelay=5;
    @Builder.Default
    private Integer maxCleanUpDelay=1800;
    //在Redis节点里显示的客户端名称。
    private String clientName;
    //开启SSL终端识别能力。
    @Builder.Default
    private Boolean sslEnableEndpointIdentification = true;
    //确定采用哪种方式（JDK或OPENSSL）来实现SSL连接。
    @Builder.Default
    private SslProvider sslProvider=SslProvider.JDK;
    //指定SSL信任证书库的路径。
    private URI sslTruststore;
    //指定SSL信任证书库的密码。
    private String sslTruststorePassword;
    //指定SSL钥匙库的路径。
    private URI sslKeystore;
    //指定SSL钥匙库的密码。
    private String sslKeystorePassword;
    //默认就是NIO TransportMode.NIO,TransportMode.EPOLL - 需要依赖里有netty-transport-native-epoll包（Linux） TransportMode.KQUEUE - 需要依赖里有 netty-transport-native-kqueue包（macOS）
    @Builder.Default
    private TransportMode transportMode=TransportMode.NIO;
    //单个key默认可重入锁多个key默认联锁	 锁的模式.如果不设置, REENTRANT(可重入锁),FAIR(公平锁),MULTIPLE(联锁),REDLOCK(红锁),READ(读锁), WRITE(写锁)
    private LockModel lockModel;
    @Builder.Default
    private Integer pingConnectionInterval=0;
    //等待加锁超时时间 -1一直等待
    @Builder.Default
    private Long attemptTimeout= 10000L;
    //数据缓存时间 默认30分钟
    @Builder.Default
    private Long dataValidTime=1000*60* 30L;
    @NestedConfigurationProperty
    private SingleServerProperty singleServerProperty;
    @NestedConfigurationProperty
    private MultipleServerProperty multipleServerProperty;
}
