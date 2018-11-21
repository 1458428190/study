package com.gdufe.study.zookeeper.zk_sample;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: laichengfeng
 * @Description: 使用异步获取节点数据内容
 * @Date: 2018/11/20 16:59
 */
public class GetData_API_ASyn_Usage implements Watcher {

    private static final String ZOOKEEPER_IPS = "192.168.106.135:2181,192.168.106.136:2181,192.168.106.137:2181";

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    private static ZooKeeper zk = null;

    private static Stat stat = new Stat();

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        String path = "/zk-book";
        zk = new ZooKeeper(ZOOKEEPER_IPS, 5000, new GetData_API_ASyn_Usage());
        countDownLatch.await();
        zk.create(path, "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        zk.getData(path, true, new IDataCallBack(), null);
        zk.setData(path, "123".getBytes(), -1);
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            if (Event.EventType.None == watchedEvent.getType()) {
                countDownLatch.countDown();
            } else if (Event.EventType.NodeDataChanged == watchedEvent.getType()) {
                zk.getData(watchedEvent.getPath(), true, new IDataCallBack(), null);
            }
        }
    }

    static class IDataCallBack implements AsyncCallback.DataCallback {

        @Override
        public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
            System.out.println(i + ", " + s + ", " + new String(bytes));
            System.out.println(stat.getCzxid() + ", " + stat.getMzxid() + ", " + stat.getVersion());
        }
    }
}
