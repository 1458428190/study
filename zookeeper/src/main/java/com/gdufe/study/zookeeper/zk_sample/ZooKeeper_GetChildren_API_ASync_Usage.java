package com.gdufe.study.zookeeper.zk_sample;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: laichengfeng
 * @Description: 使用异步获取子节点列表
 * @Date: 2018/11/20 16:25
 */
public class ZooKeeper_GetChildren_API_ASync_Usage implements Watcher {

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    private static final String ZOOKEEPER_IPS = "192.168.106.135:2181,192.168.106.136:2181,192.168.106.137:2181";

    private static ZooKeeper zk = null;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        String path = "/zk-book";
        zk = new ZooKeeper(ZOOKEEPER_IPS, 5000, new ZooKeeper_GetChildren_API_ASync_Usage());
        countDownLatch.await();
        zk.getChildren(path, true, new IChildren2Callback(), null);

        zk.create(path+"/c3", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            if (Event.EventType.None == watchedEvent.getType()) {
                countDownLatch.countDown();
            } else if (Event.EventType.NodeChildrenChanged == watchedEvent.getType()) {
                try {
                    System.out.println("ReGet Child:" + zk.getChildren(watchedEvent.getPath(), true));
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class IChildren2Callback implements AsyncCallback.Children2Callback {

        @Override
        public void processResult(int i, String s, Object o, List<String> list, Stat stat) {
            System.out.println("Get Children znode result: [response code: " + i + ", param path：" + s + ", ctx: " + o
            + ", children list: " + list + ", stat: " + stat);
        }
    }
}
