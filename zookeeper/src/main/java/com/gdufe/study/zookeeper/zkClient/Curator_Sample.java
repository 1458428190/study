package com.gdufe.study.zookeeper.zkClient;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.framework.recipes.leader.CancelLeadershipException;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.test.TestingCluster;
import org.apache.curator.test.TestingServer;
import org.apache.curator.test.TestingZooKeeperServer;
import org.apache.curator.utils.EnsurePath;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

/**
 * @Author: laichengfeng
 * @Description: Curator客户端使用示例
 * @Date: 2018/11/20 20:14
 */
public class Curator_Sample {

    private static final String ZOOKEEPER_IPS = "192.168.106.135:2181,192.168.106.136:2181,192.168.106.137:2181";

    private static int sessionTimeout = 5000;

    // 使用JDK自带的CyclicBarrier,但只适合在用个JVM中
    private static CyclicBarrier barrier = new CyclicBarrier(4);

    // 分布式Barrier
    static DistributedBarrier distributedBarrier;

    /**
     * 创建会话
     * @throws InterruptedException
     */
    @Test
    public void test_create_session() throws InterruptedException {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(ZOOKEEPER_IPS, sessionTimeout,
                3000, retryPolicy);
        client.start();
        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * 使用Fluent风格创建会话,(建造者模式)
     */
    @Test
    public void test_create_fluent_session() throws InterruptedException {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(ZOOKEEPER_IPS)
                .sessionTimeoutMs(sessionTimeout)
                .retryPolicy(retryPolicy)
                .build();
        client.start();
        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * 创建节点
     */
    @Test
    public void test_create_node() throws Exception {
        String path = "/zk-book/c1";
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(ZOOKEEPER_IPS)
                .sessionTimeoutMs(sessionTimeout)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();
        // 创建/zk-book/c1临时节点，并赋值init, 当父节点不存在时，会自动创建(值是空)
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path, "init".getBytes());
    }

    /**
     * 删除节点， 读取节点数据
     */
    @Test
    public void test_delete_node() throws Exception {
        String path = "/zk-book/c1";
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(ZOOKEEPER_IPS)
                .sessionTimeoutMs(sessionTimeout)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
                .forPath(path, "init".getBytes());
        Stat stat = new Stat();
        // 获取数据， 并将信息存至stat中
        System.out.println(new String(client.getData().storingStatIn(stat).forPath(path)));
        // 删除节点
        client.delete().deletingChildrenIfNeeded().withVersion(stat.getVersion()).forPath(path);
        System.out.println(stat);
    }

    /**
     * 更新节点
     */
    @Test
    public void test_update_node() throws Exception {
        String path = "/zk-book";
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(ZOOKEEPER_IPS)
                .sessionTimeoutMs(sessionTimeout)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();
        client.create().creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path, "init".getBytes());
        Stat stat = new Stat();
        client.getData().storingStatIn(stat).forPath(path);
        Stat newStat = client.setData().withVersion(stat.getVersion()).forPath(path, "123".getBytes());
        System.out.println("Success set node for : " + path + " , new version : " + newStat.getVersion());
        try {
            client.setData().withVersion(stat.getVersion()).forPath(path, "456".getBytes());
        } catch (Exception e) {
            System.out.println("Failed set node for : " + path);
        }
    }

    /**
     * 异步化API使用示例
     */
    @Test
    public void test_async_API() throws Exception {
        String path = "/zk-book";
        CountDownLatch semaphore = new CountDownLatch(2);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(ZOOKEEPER_IPS)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .sessionTimeoutMs(sessionTimeout)
                .build();
        client.start();
        System.out.println("Main thread: " + Thread.currentThread().getName());
        // 此处传入自定义的Executor
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground((curatorFramework, curatorEvent) -> {
            System.out.println("event[code: " + curatorEvent.getResultCode() + ", type: " + curatorEvent.getType() + "]");
            System.out.println("Thread of processResult: " + Thread.currentThread().getName());
            semaphore.countDown();
        }, executorService).forPath(path, "init".getBytes());

        // 此处不传自定义的Executor
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground(((curatorFramework, curatorEvent) -> {
            System.out.println("event[code: " + curatorEvent.getResultCode() + ", type: " + curatorEvent.getType() + "]");
            System.out.println("Thread of processResult: " + Thread.currentThread().getName());
            semaphore.countDown();
        })).forPath(path, "init".getBytes());

        semaphore.await();
        executorService.shutdown();
    }

    /**
     * 事件监听
     */
    @Test
    public void test_node_cache() throws Exception {
        String path = "/zk-book/nodecache";
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(ZOOKEEPER_IPS)
                .sessionTimeoutMs(sessionTimeout)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();

        client.start();
        // 创建也会触发
        client.create().creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path, "init".getBytes());
        NodeCache cache = new NodeCache(client, path, false);
        cache.start(true);
        cache.getListenable().addListener(() ->
                System.out.println("Node data update, new data : " + new String(cache.getCurrentData().getData())));
        client.setData().forPath(path, "u".getBytes());
        Thread.sleep(1000);
        // 删除不会触发
        client.delete().deletingChildrenIfNeeded().forPath(path);
        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * pathChildrenCache示例,监听一级子节点增删改
     */
    @Test
    public void test_path_children_cache() throws Exception {
        String path = "/zk-book";
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(ZOOKEEPER_IPS)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .sessionTimeoutMs(sessionTimeout)
                .build();

        client.start();
        PathChildrenCache cache = new PathChildrenCache(client, path, true);
        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        cache.getListenable().addListener((curatorFramework, pathChildrenCacheEvent) -> {
            switch (pathChildrenCacheEvent.getType()) {
                case CHILD_ADDED:
                    System.out.println("CHILD_ADDED, " + pathChildrenCacheEvent.getData().getPath());
                    break;
                case CHILD_UPDATED:
                    System.out.println("CHILD_UPDATED, " + pathChildrenCacheEvent.getData().getPath());
                    break;
                case CHILD_REMOVED:
                    System.out.println("CHILD_REMOVED, " + pathChildrenCacheEvent.getData().getPath());
                    break;
                default:
                    break;
            }
        });
        client.create().withMode(CreateMode.PERSISTENT).forPath(path);
        Thread.sleep(1000);
        client.create().withMode(CreateMode.EPHEMERAL)
                .forPath(path + "/c1", "c1".getBytes());
        Thread.sleep(1000);
        client.delete().forPath(path + "/c1");
        Thread.sleep(1000);
        // 不指定数据版本， 默认-1
        client.delete().forPath(path);
        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * Master选举
     */
    @Test
    public void test_master() throws InterruptedException {
        String masterPath = "/curator_recipes_master_path";
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(ZOOKEEPER_IPS)
                .sessionTimeoutMs(sessionTimeout)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();
        LeaderSelector leaderSelector = new LeaderSelector(client, masterPath, new LeaderSelectorListener() {
            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                if((connectionState == ConnectionState.SUSPENDED) || (connectionState == ConnectionState.LOST)) {
                    throw new CancelLeadershipException();
                }
            }

            @Override
            public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
                // 成为Master后的业务逻辑
                System.out.println("成为Master角色");
                Thread.sleep(3000);
                System.out.println("完成Master操作， 释放Master权利");
            }

        });
        leaderSelector.autoRequeue();
        leaderSelector.start();
        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * 模拟并发出现订单号重复
     */
    @Test
    public void test_recipes_noLock() throws InterruptedException {
        // 模拟并发出现订单号重复场景
        CountDownLatch countDownLatch = new CountDownLatch(1);
        for(int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                }
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
                String orderNo = sdf.format(new Date());
                System.out.println("生成的订单号是：" + orderNo);
            }).start();
        }
        countDownLatch.countDown();
        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * 分布式锁, 生成30个订单号
     */
    @Test
    public void test_recipes_lock() throws InterruptedException {
        String path = "/curator-recipes_lock_path";
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(ZOOKEEPER_IPS)
                .sessionTimeoutMs(sessionTimeout)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();
        // 分布式锁
        InterProcessMutex lock = new InterProcessMutex(client, path);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        for(int i = 0; i < 30; i++) {
            new Thread(() -> {
                try {
                    countDownLatch.await();
                    // 获取锁
                    lock.acquire();
                } catch (InterruptedException e) {
                } catch (Exception e) {
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss|SSS");
                String orderNo = simpleDateFormat.format(new Date());
                System.out.println("生成订单号：" + orderNo);
                // 释放锁
                try {
                    lock.release();
                } catch (Exception e) {
                }
            }).start();
        }
        countDownLatch.countDown();
        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * 分布式计数器
     */
    @Test
    public void test_recipes_distAtomicInt() throws Exception {
        String path = "/curator_recipes_distAtomicInt_path";
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(ZOOKEEPER_IPS)
                .sessionTimeoutMs(sessionTimeout)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();
        DistributedAtomicInteger atomicInteger = new DistributedAtomicInteger(client, path, new RetryNTimes(3, 1000));
        AtomicValue<Integer> rc = atomicInteger.add(8);
        System.out.println("Result : " + rc.succeeded());
        System.out.println(rc.getStats());
        System.out.println(rc.preValue());
        System.out.println(rc.postValue());
    }

    /**
     * 分布式Barrier
     */
    @Test
    public void test_recipes_cycliBarrier() throws Exception {
        // 使用本地的JVM
//        ExecutorService executorService = Executors.newFixedThreadPool(4);
//        executorService.submit(new Runner("1号选手"));
//        executorService.submit(new Runner("2号选手"));
//        executorService.submit(new Runner("3号选手"));
//        executorService.submit(new Runner("4号选手"));
//        executorService.shutdown();
        // 使用分布式Barrier, 第一种方式，TODO 对于distributeBarrier是否需要在分布式环境下共用
        String barrier_path = "/curator_recipes_barrier_path";
//        for(int i = 0; i < 5; i++) {
//            new Thread(() -> {
//                try {
//                    CuratorFramework client = CuratorFrameworkFactory.builder()
//                            .connectString(ZOOKEEPER_IPS)
//                            .sessionTimeoutMs(sessionTimeout)
//                            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
//                            .build();
//                    client.start();
//                    distributedBarrier = new DistributedBarrier(client, barrier_path);
//                    System.out.println(Thread.currentThread().getName() + "号barrier设置");
//                    distributedBarrier.setBarrier();
//                    distributedBarrier.waitOnBarrier();
//                    System.out.println("启动...");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }).start();
//        }
//        // 先阻塞2秒，等待5个线程都进入waitOnBarrier阶段
//        Thread.sleep(2000);
//        // 然后释放Barrier，然后等待5个线程同时在waitOnBarrier处开始执行
//        distributedBarrier.removeBarrier();
//        Thread.sleep(Integer.MAX_VALUE);

        // 分布式Barrier，第二种方式，可控制同时进入，同时离开，相当于2层barrier
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    CuratorFramework client = CuratorFrameworkFactory.builder()
                            .connectString(ZOOKEEPER_IPS)
                            .sessionTimeoutMs(sessionTimeout)
                            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                            .build();
                    client.start();
                    DistributedDoubleBarrier doubleBarrier = new DistributedDoubleBarrier(client, barrier_path, 5);
                    Thread.sleep(Math.round(Math.random() * 3000));
                    System.out.println(Thread.currentThread().getName() + "号进入barrier");
                    doubleBarrier.enter();
                    System.out.println("启动....");
                    Thread.sleep(Math.round(Math.random()) * 3000);
                    doubleBarrier.leave();
                    System.out.println("退出...");
                } catch (Exception e) {

                }
            }).start();
        }
        Thread.sleep(Integer.MAX_VALUE);
    }


    /**
     * 工具ZKPaths的使用，多用于构建ZNode路径，递归创建和删除节点等
     */
    @Test
    public void test_ZKPaths() throws Exception {
        String path = "/curator_zkpath_sample";
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(ZOOKEEPER_IPS)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .sessionTimeoutMs(sessionTimeout)
                .build();
        client.start();
        ZooKeeper zooKeeper = client.getZookeeperClient().getZooKeeper();
        // 拼接路径
        System.out.println(ZKPaths.fixForNamespace(path, "sub"));
        System.out.println(ZKPaths.makePath(path, "sub"));
        // 获取最高级节点
        System.out.println(ZKPaths.getNodeFromPath("/curator_zkpath_sample/sub1"));
        // 获取父路径和节点
        ZKPaths.PathAndNode pn = ZKPaths.getPathAndNode("/curator_zkpath_sample/sub1");
        System.out.println(pn.getPath());
        System.out.println(pn.getNode());

        String dir1 = path + "/child1";
        String dir2 = path + "/child2";
        // 创建节点
        ZKPaths.mkdirs(zooKeeper, dir1);
        ZKPaths.mkdirs(zooKeeper, dir2);
        // 子节点排序情况
        System.out.println(ZKPaths.getSortedChildren(zooKeeper, path));
        // 删除所有子节点以及自身节点
        ZKPaths.deleteChildren(zooKeeper, path, true);
    }

    /**
     * 工具EnsurePath的使用，提供了一种能够确保数据节点存在的机制，解决一些琐碎的重复性操作
     */
    @Test
    public void test_EnsurePath() throws Exception {
        String path = "/zk-book/c1";
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(ZOOKEEPER_IPS)
                .sessionTimeoutMs(sessionTimeout)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();
        client.usingNamespace("zk-book");
        EnsurePath ensurePath = new EnsurePath(path);
        // 创建path节点
        ensurePath.ensure(client.getZookeeperClient());
        // 再次创建不会报异常，自动检查是否存在
        ensurePath.ensure(client.getZookeeperClient());

        EnsurePath ensurePath1 = client.newNamespaceAwareEnsurePath("/c1");
        // 创建/c1节点
        ensurePath1.ensure(client.getZookeeperClient());

        System.out.println(ensurePath.getPath());
        System.out.println(ensurePath1.getPath());
    }

    /**
     * TestingServer的使用,方便开发人员对ZooKeeper的开发与测试，（启动一个ZooKeeper服务器）
     * TODO zk-book-data文件内容？
     */
    @Test
    public void test_testingServer() throws Exception {
        String path = "/zookeeper";
        TestingServer server = new TestingServer(2181, new File("E:\\studyword\\zookeeper\\zk-book-data"));
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(server.getConnectString())
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .sessionTimeoutMs(sessionTimeout)
                .build();
        client.start();
        System.out.println(client.getChildren().forPath(path));
        server.close();
    }

    /**
     * TestingCluster使用示例，启动ZooKeeper服务器集群
     * TODO 如何与ZooKeeper连接
     */
    @Test
    public void test_testingCluster() throws Exception {
        TestingCluster cluster = new TestingCluster(3);
        cluster.start();

        Thread.sleep(2000);
        TestingZooKeeperServer leader = null;
        for(TestingZooKeeperServer zs : cluster.getServers()) {
            System.out.println(zs.getInstanceSpec().getServerId() + "-");
            System.out.println(zs.getQuorumPeer().getServerState()+"-");
            System.out.println(zs.getInstanceSpec().getDataDirectory().getAbsolutePath());
            if(zs.getQuorumPeer().getServerState().equals("leading")) {
                leader = zs;
            }
        }
        leader.kill();
        System.out.println("-- After leader kill:");
        for(TestingZooKeeperServer zs : cluster.getServers()) {
            System.out.println(zs.getInstanceSpec().getServerId() + "-");
            System.out.println(zs.getQuorumPeer().getServerState()+"-");
            System.out.println(zs.getInstanceSpec().getDataDirectory().getAbsolutePath());
        }
        cluster.stop();
    }

    class Runner implements Runnable {

        private String name;

        public Runner(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            System.out.println(name + " 准备好了");
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.println(name + " 起跑");
        }
    }

}
