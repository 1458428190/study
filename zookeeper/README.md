# ZooKeeper

### 学习书籍
- 《从Paxos到ZooKeeper分布式一致性原理与实践》倪超 著

### 集群部署
- zoo.cfg
```text
# sending a request and getting an acknowledgement
syncLimit=5
# the directory where the snapshot is stored.
# do not use /tmp for storage, /tmp here is just 
# example sakes.
# 数据存储路径
dataDir=/var/lib/zookeeper/data
# 日志存储路径
dataLogDir=/var/lib/zookeeper/log
# the port at which the clients will connect
clientPort=2181
# the maximum number of client connections.
# increase this if you need to handle more clients
#maxClientCnxns=60
#
# Be sure to read the maintenance section of the 
# administrator guide before turning on autopurge.
#
# http://zookeeper.apache.org/doc/current/zookeeperAdmin.html#sc_maintenance
#
# The number of snapshots to retain in dataDir
#autopurge.snapRetainCount=3
# Purge task interval in hours
# Set to "0" to disable auto purge feature
#autopurge.purgeInterval=1

# 配置集群
server.1=192.168.106.135:2888:3888
server.2=192.168.106.136:2888:3888
server.3=192.168.106.137:2888:3888
```

- 问题

    - 1.1 集群启动失败
   
        - 检查zoo.cfg配置是否正确（ip:port，myid等）
        - 尝试删除data下除myid的文件，以及log目录
        - 检查集群配置的端口是否有开放
        ```text
          // Centos 7 相关防火墙命令
          // 添加端口， --zone域（默认public）， permanent（永久开放）
          firewall-cmd --zone=public --add-port=2181/tcp permanent
          // 查看端口列表
          firewall-cmd --zone=public --list-ports
          // 重启
          firewall-cmd --reload
          // 查看端口xxx占用情况
          netstat -lnp | grep xxx    
        ```
    - 1.2 虚拟机ip会动态变更
    
        - 虚拟机固定ip
        
        https://www.linuxidc.com/Linux/2017-12/149910.htm

### ZooKeeper客户端
- 原生API (代码稍长，不支持遍历删除，遍历创建，watch只能触发一次，多次需要反复注册)

- ZkClient (没人维护，2011年)

- Curator （有维护，建议用）
    - 问题
        - 1.1 版本兼容问题
        ```text
        org.apache.zookeeper.KeeperException$UnimplementedException: KeeperErrorCode = Unimplemented for /zk-book/c1
        遇到此错误，可以检查下Curator和ZooKeeper的版本是否兼容
        Curator2.xx - ZooKeeper 3.4.x 与 ZooKeeper 3.5.x
        Curator3.xx - ZooKeeper 3.5.x
        Curator4.xx - ZooKeeper 3.5.x
        ```
    - 好处
        - 1.1 长时间维护
        - 1.2 方便使用
        - 1.3 封装了多种常用场景使用
        ```text
        比如：Master选举， 分布式锁，分布式计数器，分布式Barrier， 
        ```
        - 1.4 提供了一些工具（ZKPaths、EnsurePath、TestingServer、TestingCluster）
