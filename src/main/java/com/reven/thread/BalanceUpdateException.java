package com.reven.thread;

import java.util.UUID;

import com.reven.util.RedisLockTool;

import redis.clients.jedis.JedisCluster;

public class BalanceUpdateException implements Runnable {

    private RedisLockTool redisLockTool;

    private JedisCluster jedis;

    private String threadName;

    private Integer cost;

    private boolean useLock;

    public BalanceUpdateException(RedisLockTool redisLockTool, JedisCluster jedis, String threadName, int cost,
            boolean useLock) {
        this.redisLockTool = redisLockTool;
        this.jedis = jedis;
        this.threadName = threadName;
        this.cost = cost;
        this.useLock = useLock;
    }

    @Override
    public void run() {
        String requestId = UUID.randomUUID().toString();
        boolean flag = true;
        if (useLock) {
            flag = redisLockTool.tryGetDistributedLock("lockKey_test", requestId, 10);
        }
        if (flag) {
            String balance = jedis.get("balance_test");
            System.out.println("threadName=" + threadName + ",balance=" + balance + ",cost=" + cost + ",当前时间="
                    + System.currentTimeMillis());
            Integer.parseInt("模拟异常——查看是否释放锁");
            int balanceInt = Integer.parseInt(balance);
            if (balanceInt > 0) {
                jedis.set("balance_test", balanceInt - cost + "");
            }
            if (useLock) {
                boolean releaseDistributedLock = redisLockTool.releaseDistributedLock("lockKey_test", requestId);
                System.out.println("releaseDistributedLock="+releaseDistributedLock);
            }
        } else {
//                System.out.println("threadName=" + threadName + ",并发请求没有拿到锁。当前时间=" + System.currentTimeMillis());
        }
    }

}