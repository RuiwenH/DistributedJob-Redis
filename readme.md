# 项目
* 分布式锁Jedis实现

#  测试
* 测试类：DemoApplicationTests.java 模拟账户余额修改场景（部分请求减1，部分线程加1）
* 测试用例：未使用分布式锁，用多个线程模拟并发执行账户余额修改，发现余额不准确。
* 测试用例：使用分布式锁，用多个线程模拟并发执行账户余额修改，能准确加减余额。
* 测试用例：使用分布式锁，用多个线程模拟并发执行账户余额修改，业务处理出现异常，验证锁是否死锁。
* 测试用例：使用分布式锁，用多个线程模拟并发执行账户余额修改，sleep时间超过锁的时间，验证锁是否死锁。————不会死锁，但余额不正确，需要注意超时的设置和处理，当然本场景使用数据库乐观锁更安全。使用哪种方案也要考虑业务的逻辑。


# 参考文档
* https://blog.csdn.net/weixin_37882382/article/details/83378959 ——完整版
* Redis分布式锁的正确实现方式（Java版）https://wudashan.cn/2017/10/23/Redis-Distributed-Lock-Implement/ ——详细