package com.reven.config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@Slf4j
public class RedisConfig {

    @Value("#{'${spring.redis.cluster.nodes}'.split(',')}")
    private List<String> nodes;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.lettuce.pool.max-active}")
    private int maxTotal;

    @Value("${spring.redis.lettuce.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.lettuce.pool.max-wait}")
    private long maxWaitMillis;

    @Value("${spring.redis.password:}")
    private String password;

    @Bean
    public JedisCluster jedisClusterFactory() throws Exception {
        Set<HostAndPort> nodeSets = new HashSet<>();
        for (int i = 0; i < nodes.size(); i++) {
            String[] node = nodes.get(i).split(":");
            nodeSets.add(new HostAndPort(node[0], Integer.parseInt(node[1])));
        }
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        log.info("maxTotal={},maxIdle={},maxWaitMillis={}", maxTotal, maxIdle, maxWaitMillis);
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        // 连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
        jedisPoolConfig.setBlockWhenExhausted(true);
        // 是否启用pool的jmx管理功能, 默认true
        jedisPoolConfig.setJmxEnabled(true);
        JedisCluster jedisCluster = new JedisCluster(nodeSets, timeout, timeout, 1, password, jedisPoolConfig);
        return jedisCluster;
    }

}