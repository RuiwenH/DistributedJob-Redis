# 项目
* 分布式锁Jedis实现

#  测试
*  测试类：DemoApplicationTests.java 模拟扣库存场景（扣库存行为是先查询是否大于0，然后库存减1）
* 测试用例：未使用分布式锁，用多个线程模拟并发执行扣库存，发现库存扣减不准确。
* 测试用例：使用分布式锁，用多个线程模拟并发执行扣库存，能准确扣库存。

# 参考文档
* https://blog.csdn.net/weixin_37882382/article/details/83378959 ——完整版
* Redis分布式锁的正确实现方式（Java版）https://wudashan.cn/2017/10/23/Redis-Distributed-Lock-Implement/ ——详细