# Kafka

### 学习书籍
- 《Kafka权威指南》

### Kafka使用笔记
    - 1. 安装Zookeeper
        Kafka broker的元数据是存在Zookeeper里的, 本例安装在(192.168.106.[135,136,137])
    - 2. 安装Kafka
        本例安装在192.168.106.135
    - 3. Kafka客户端的API使用 (kafka-client)
    - 4. Kafka-connect的使用 (kafka-connect-elasticsearch, kafka-connect-mysql等)
        可以使用https://www.confluent.io/hub/的连接器, 使用confluent-hub进行安装
    - 5. Kafka-Stream流式处理框架
    
### 问题：
    - 1. 从主题上读取消息(--zookeeper)命令错误
        版本问题，低版本是--zookeeper，高版本是--bootstrap-server
        完整命令 bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning
    - 2. Confluent和Kafka的关系, 以及confluent的使用
    - 3. kafka（java客户端）消费者取不到消息，生产者消息也没发送成功
        https://blog.csdn.net/zhaominpro/article/details/79068141
    
