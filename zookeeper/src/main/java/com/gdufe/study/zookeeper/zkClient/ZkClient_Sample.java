package com.gdufe.study.zookeeper.zkClient;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.junit.Test;

import java.util.List;

/**
 * @Author: laichengfeng
 * @Description: ZkClient开源客户端的使用示例
 * @Date: 2018/11/20 19:12
 */
public class ZkClient_Sample {

    private static final String ZOOKEEPER_IPS = "192.168.106.135:2181,192.168.106.136:2181,192.168.106.137:2181";

    private static int sessionTimeout = 5000;

    public static void main(String[] args) {

    }

    /**
     * 创建一个Zookeeper会话
     */
    @Test
    public void test_createSession() {
        ZkClient zkClient = new ZkClient(ZOOKEEPER_IPS, sessionTimeout);
        System.out.println("ZooKeeper session established.");
    }

    /**
     * 创建节点
     */
    @Test
    public void test_create_Node_Sample() {
        ZkClient zkClient = new ZkClient(ZOOKEEPER_IPS, sessionTimeout);
        String path = "/zk-book/c1";
        // true 递归创建父节点
        zkClient.createPersistent(path, true);
    }

    /**
     * 读取数据
     */
    @Test
    public void test_get_children() throws InterruptedException {
        String path = "/zk-book";
        ZkClient zkClient = new ZkClient(ZOOKEEPER_IPS, sessionTimeout);
        zkClient.subscribeChildChanges(path, (parentPath, currentChilds) -> System.out.println(parentPath + " ' s child changed, currentChilds:" + currentChilds));

        zkClient.createPersistent(path);
        Thread.sleep(1000);
        System.out.println(zkClient.getChildren(path));
        Thread.sleep(1000);
        zkClient.createPersistent(path + "/c1");
        Thread.sleep(1000);
        zkClient.delete(path + "/c1");
        Thread.sleep(1000);
        zkClient.delete(path);
        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * 获取节点数据
     */
    @Test
    public void test_get_data() throws InterruptedException {
        String path = "/zk-book";
        ZkClient zkClient = new ZkClient(ZOOKEEPER_IPS, sessionTimeout);
        zkClient.createEphemeral(path, "123");
        zkClient.subscribeDataChanges(path, new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                System.out.println("Node " + dataPath + " changed.  data : " + data);
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println("Node " + dataPath + " deleted.");
            }
        });

        System.out.println((String) zkClient.readData(path));
        zkClient.writeData(path, "456");
        Thread.sleep(1000);
        zkClient.delete(path);
        Thread.sleep(Integer.MAX_VALUE);
    }
}
