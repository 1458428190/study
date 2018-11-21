package com.gdufe.study.zookeeper.zk_sample;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: laichengfeng
 * @Description: 使用sessionid和passwd 复用Zookeeper
 * @Date: 2018/11/20 15:02
 */
public class ZooKeeper_Constructor_Usage_With_SID_PASSWD implements Watcher {

    private static final String ZOOKEEPER_IPS = "192.168.106.135:2181,192.168.106.136:2181,192.168.106.137:2181";

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws IOException, InterruptedException {
        ZooKeeper zooKeeper = new ZooKeeper(ZOOKEEPER_IPS, 5000, new ZooKeeper_Constructor_Usage_With_SID_PASSWD());
        countDownLatch.await();
        long sessionId = zooKeeper.getSessionId();
        byte[] passwd = zooKeeper.getSessionPasswd();

        zooKeeper = new ZooKeeper(ZOOKEEPER_IPS, 5000, new ZooKeeper_Constructor_Usage_With_SID_PASSWD(),
                1l, "test".getBytes());

        zooKeeper = new ZooKeeper(ZOOKEEPER_IPS, 5000, new ZooKeeper_Constructor_Usage_With_SID_PASSWD(),
                sessionId, passwd);

        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("Receive watched event: " + watchedEvent);
        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            countDownLatch.countDown();
        }
    }
}
