package org.crow.movie.user.common.plugin.redis;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;

import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;

@Configuration
public class RedisConfig {

	@Autowired
    private RedisProperties redisProperties;
	
	/**
	 * redis连接工厂
	 * @return
	 */
	@Bean
	public RedisConnectionFactory redisCF() {
		
		/**
		 * 因为时单机，集群设置暂时注释掉，否则会报错
		 */
		//List<String> clusterNodes = redisProperties.getCluster().getNodes();
        //Set<RedisNode> nodes = new HashSet<RedisNode>();
        //clusterNodes.forEach(address -> nodes.add(new RedisNode(address.split(":")[0].trim(), Integer.valueOf(address.split(":")[1]))));
        //RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
        //clusterConfiguration.setClusterNodes(nodes);
        //clusterConfiguration.setPassword(RedisPassword.of(redisProperties.getPassword()));
        //clusterConfiguration.setMaxRedirects(redisProperties.getCluster().getMaxRedirects());
	    //LettuceConnectionFactory rcf = new LettuceConnectionFactory(clusterConfiguration, getLettuceClientConfiguration());
        
		LettuceConnectionFactory rcf = new LettuceConnectionFactory();
	    return rcf;
	}
	
	@Bean
	public RedisTemplate<String, Object> redisTemplate(@Qualifier("redisCF")RedisConnectionFactory factory) {
	    RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
	    template.setConnectionFactory(factory);
	    return template;
	}
	
    private LettuceClientConfiguration getLettuceClientConfiguration() {
    	
    	/**
    	 * 连接池设置
    	 */
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxIdle(redisProperties.getLettuce().getPool().getMaxIdle());
        poolConfig.setMinIdle(redisProperties.getLettuce().getPool().getMinIdle());
        poolConfig.setMaxTotal(redisProperties.getLettuce().getPool().getMaxActive());
        /**
         * ClusterTopologyRefreshOptions配置用于开启自适应刷新和定时刷新。如自适应刷新不开启，Redis集群变更时将会导致连接异常！
         */
        ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                //开启自适应刷新
                //.enableAdaptiveRefreshTrigger(ClusterTopologyRefreshOptions.RefreshTrigger.MOVED_REDIRECT, ClusterTopologyRefreshOptions.RefreshTrigger.PERSISTENT_RECONNECTS)
                //开启所有自适应刷新，MOVED，ASK，PERSISTENT都会触发
                .enableAllAdaptiveRefreshTriggers()
                // 自适应刷新超时时间(默认30秒)
                .adaptiveRefreshTriggersTimeout(Duration.ofSeconds(30*5)) //默认关闭开启后时间为30秒
                // 开周期刷新
                .enablePeriodicRefresh(Duration.ofSeconds(60*5))  // 默认关闭开启后时间为60秒 ClusterTopologyRefreshOptions.DEFAULT_REFRESH_PERIOD 60  .enablePeriodicRefresh(Duration.ofSeconds(2)) = .enablePeriodicRefresh().refreshPeriod(Duration.ofSeconds(2))
                .build();
        return LettucePoolingClientConfiguration.builder()
                .clientOptions(ClusterClientOptions.builder().topologyRefreshOptions(topologyRefreshOptions).build())
                .poolConfig(poolConfig)
                //将appID传入连接，方便Redis监控中查看
                //.clientName("_lettuce")
                .build();
    }
}
