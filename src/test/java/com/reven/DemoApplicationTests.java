package com.reven;

import java.util.Scanner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.reven.thread.StockQty;
import com.reven.util.RedisLockTool;

import redis.clients.jedis.JedisCluster;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    @Autowired
    private RedisLockTool redisLockTool;
    @Autowired
    private JedisCluster jedis;

    @Test
    public void testUnlock() throws InterruptedException {
        // 初始化库存stock=100
        jedis.set("test_lock_stock", "100");

        // 启动100个线程同时请求，模拟扣库存
        for (int i = 0; i < 100; i++) {

            String threadName = "t";
            if (i < 10) {
                threadName = threadName + "0" + i;
            } else {
                threadName = threadName + i;
            }
            Thread mTh2 = new Thread(new StockQty(redisLockTool, jedis, threadName, false));
            mTh2.start();
        }
        Thread.sleep(30000);
    }

    @Test
    public void testLock() throws InterruptedException {
        for (int j = 0; j < 20; j++) {
            System.out.println("=====================" + j + "==============================");
            // 初始化库存stock=100
            jedis.set("test_lock_stock", "100");

            // 启动100个线程同时请求，模拟扣库存
            for (int i = 0; i < 100; i++) {

                String threadName = "t";
                if (i < 10) {
                    threadName = threadName + "0" + i;
                } else {
                    threadName = threadName + i;
                }
                Thread mTh2 = new Thread(new StockQty(redisLockTool, jedis, threadName, true));
                mTh2.start();
            }
            Thread.sleep(5000);
        }
    }

}
