package com.reven.thread;

import java.util.UUID;

import com.reven.util.RedisLockTool;

import redis.clients.jedis.JedisCluster;

public class StockQty implements Runnable {

    private RedisLockTool redisLockTool;

    private JedisCluster jedis;

    private String threadName;

    private boolean useLock;

    public StockQty(RedisLockTool redisLockTool, JedisCluster jedis, String threadName, boolean useLock) {
        this.redisLockTool = redisLockTool;
        this.jedis = jedis;
        this.threadName = threadName;
        this.useLock = useLock;
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
//            System.out.println("threadName=" + threadName + ",in.");
            String requestId = UUID.randomUUID().toString();
            boolean flag = true;
            if (useLock) {
                flag = redisLockTool.tryGetDistributedLock("lockKey_test", requestId, 10);
            }
            if (flag) {
                String stock = jedis.get("test_lock_stock");
                System.out.println(
                        "threadName=" + threadName + ",stock=" + stock + ",当前时间=" + System.currentTimeMillis());
                try {
                    // 增加错误几率
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                }
                int stockInt = Integer.parseInt(stock);
                if (stockInt > 0) {
                    jedis.set("test_lock_stock", stockInt - 1 + "");
                }
                if (useLock) {
                    redisLockTool.releaseDistributedLock("lockKey_test", requestId);
                }
            } else {
//                System.out.println("threadName=" + threadName + ",并发请求没有拿到锁。当前时间=" + System.currentTimeMillis());
            }
        }
    }

}