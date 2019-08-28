package com.reven;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.reven.thread.BalanceUpdate;
import com.reven.thread.BalanceUpdateException;
import com.reven.thread.BalanceUpdateTimeout;
import com.reven.util.RedisLockTool;

import redis.clients.jedis.JedisCluster;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LockTest {

    @Autowired
    private RedisLockTool redisLockTool;
    @Autowired
    private JedisCluster jedis;

    @Test
    public void testUnlock() throws InterruptedException {
        // 初始化库存stock=100
        jedis.set("balance_test", "100");

        // 启动100个线程同时请求，模拟扣库存
        for (int i = 0; i < 100; i++) {

            String threadName = "t";
            if (i < 10) {
                threadName = threadName + "0" + i;
            } else {
                threadName = threadName + i;
            }
            int cost = 0;
            if (i % 2 == 0) {
                cost = 1;
            } else {
                cost = -1000;
            }

            Thread mTh2 = new Thread(new BalanceUpdate(redisLockTool, jedis, threadName, cost, false));
            mTh2.start();
        }
        Thread.sleep(10000);
        String balance = jedis.get("balance_test");
        System.out.println("threadName=主线程,最终balance=" + balance + ",当前时间=" + System.currentTimeMillis());
    }

    @Test
    public void testLock() throws InterruptedException {
        // 模拟20次，增加发现出错概率
        for (int j = 0; j < 2; j++) {
            System.out.println("=====================" + j + "==============================");
            // 初始化库存stock=100
            jedis.set("balance_test", "100");

            // 启动100个线程同时请求，模拟扣库存
            for (int i = 0; i < 100; i++) {

                String threadName = "t";
                if (i < 10) {
                    threadName = threadName + "0" + i;
                } else {
                    threadName = threadName + i;
                }
                int cost = 0;
                if (i % 2 == 0) {
                    cost = 1;
                } else {
                    cost = -1;
                }
                Thread mTh2 = new Thread(new BalanceUpdate(redisLockTool, jedis, threadName, cost, true));
                mTh2.start();
            }
            Thread.sleep(10000);
            String balance = jedis.get("balance_test");
            System.out.println("threadName=主线程,最终balance=" + balance + ",当前时间=" + System.currentTimeMillis());
        }
    }

    @Test
    public void testLockTimeout() throws InterruptedException {
        for (int j = 0; j < 2; j++) {
            System.out.println("=====================" + j + "==============================");
            // 初始化库存stock=100
            jedis.set("balance_test", "100");
            // 重置清空
            jedis.del("lockKey_test");

            // 启动100个线程同时请求，模拟扣库存
            for (int i = 0; i < 100; i++) {
                Thread.sleep(1);
                String threadName = "t";
                if (i < 10) {
                    threadName = threadName + "0" + i;
                } else {
                    threadName = threadName + i;
                }
                int cost = 0;
                if (i % 2 == 0) {
                    cost = 1;
                } else {
                    cost = -1;
                }

                Thread mTh2 = new Thread(new BalanceUpdateTimeout(redisLockTool, jedis, threadName, cost, true));
                mTh2.start();
            }
            Thread.sleep(10000);
            String balance = jedis.get("balance_test");
            System.out.println("threadName=主线程,最终balance=" + balance + ",当前时间=" + System.currentTimeMillis());
        }
    }
    
    @Test
    public void testLockException() throws InterruptedException {
        for (int j = 0; j < 2; j++) {
            System.out.println("=====================" + j + "==============================");
            // 初始化库存stock=100
            jedis.set("balance_test", "100");
            // 重置清空
            jedis.del("lockKey_test");

            // 启动100个线程同时请求，模拟扣库存
            for (int i = 0; i < 100; i++) {

                String threadName = "t";
                if (i < 10) {
                    threadName = threadName + "0" + i;
                } else {
                    threadName = threadName + i;
                }
                int cost = 0;
                if (i % 2 == 0) {
                    cost = 1;
                    Thread mTh2 = new Thread(new BalanceUpdate(redisLockTool, jedis, threadName, cost, true));
                    mTh2.start();
                } else {
                    cost = -1;
                    Thread mTh2 = new Thread(new BalanceUpdateException(redisLockTool, jedis, threadName, cost, true));
                    mTh2.start();

                }

            }
            Thread.sleep(10000);
            String balance = jedis.get("balance_test");
            System.out.println("threadName=主线程,最终balance=" + balance + ",当前时间=" + System.currentTimeMillis());
        }
    }

}
