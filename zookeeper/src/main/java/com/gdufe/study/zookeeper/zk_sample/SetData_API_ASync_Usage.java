package com.gdufe.study.zookeeper.zk_sample;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: laichengfeng
 * @Description: 使用异步更新数据
 * @Date: 2018/11/20 17:23
 */
public class SetData_API_ASync_Usage implements Watcher {

    private static final String ZOOKEEPER_IPS = "192.168.106.135:2181,192.168.106.136:2181,192.168.106.137:2181";

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    private static ZooKeeper zk = null;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        String path = "/zk-book";
        zk = new ZooKeeper(ZOOKEEPER_IPS, 5000, new SetData_API_ASync_Usage());
        countDownLatch.await();
        zk.create(path, "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        zk.getData(path, true, null);
        zk.setData(path, "123".getBytes(), -1, new IStatCallback(), null);
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            countDownLatch.countDown();
        }
    }

    static class IStatCallback implements AsyncCallback.StatCallback {

        @Override
        public void processResult(int i, String s, Object o, Stat stat) {
            if(i == 0) {
                System.out.println("SUCCESS");
            }
        }
    }
}
