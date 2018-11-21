package com.gdufe.study.zookeeper.zk_sample;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: laichengfeng
 * @Description:
 * @Date: 2018/11/20 18:56
 */
public class AuthSample {
    final static String PATH = "/zk-book-auth_test";

    private static final String ZOOKEEPER_IPS = "192.168.106.135:2181,192.168.106.136:2181,192.168.106.137:2181";

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        ZooKeeper zooKeeper = new ZooKeeper(ZOOKEEPER_IPS, 5000, null);
        // 采用digest模式，foo:true 类似于username:password
        zooKeeper.addAuthInfo("digest", "foo:true".getBytes());
        zooKeeper.create(PATH, "init".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);

        // 使用正确的acl验证
        ZooKeeper zk1 = new ZooKeeper(ZOOKEEPER_IPS, 5000, null);
        zk1.addAuthInfo("digest", "foo:true".getBytes());
        byte[] data = zk1.getData(PATH, false, null);
        System.out.println(new String(data));

        // 使用错误的acl验证
        ZooKeeper zk2 = new ZooKeeper(ZOOKEEPER_IPS, 5000, null);
        zk2.addAuthInfo("digest", "foo:false".getBytes());
        byte[] data2 = zk2.getData(PATH, false, null);
        System.out.println(new String(data2));
    }
}
