package com.gdufe.study.kafka.test;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.Deserializer;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @Author: laichengfeng
 * @Description: Kafka消费者API
 * @Date: 2018/12/17 15:36
 */
public class ConsumerApiTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    KafkaConsumer<String, String> consumer;

    Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    /**
     * 创建kafka消费者
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        // kafka消费者 配置参数
        Properties kafkaProps = new Properties();
        // 服务端集群地址,以逗号','分隔
        kafkaProps.put("bootstrap.servers", "192.168.106.135:9092");
        // 反序列化器
        kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.StringDeserializer");
        // 消费者群组id
        kafkaProps.put("group.id", "CountryCounter");
        consumer = new KafkaConsumer<>(kafkaProps);
    }

    @Test
    /**
     * 订阅主题
     */
    public void testSubscibe() {
        // 订阅单个主题
        consumer.subscribe(Collections.singletonList("customerCountries"));
        // 使用正则表达式订阅多个主题
        consumer.subscribe(Pattern.compile("test.*"));
    }

    @Test
    /**
     * 轮询
     */
    public void testPolling() {
        Map<String, Object> custCountryMap = new HashMap<>();
        try {
            while(true) {
                ConsumerRecords<String, String> records = consumer.poll(100);
                for(ConsumerRecord<String, String> record: records) {
                    logger.debug("topic = %s, partition = %s, offset = %s, customer = %s, country = %s\n",
                            record.topic(), record.partition(), record.offset(), record.key(), record.value());
                    int updateCount = 1;
                    if (custCountryMap.containsValue(record.value())) {
                        updateCount = (int)custCountryMap.get(record.value()) + 1;
                    }
                    custCountryMap.put(record.value(), updateCount);
                    JSONObject json = new JSONObject(custCountryMap);
                    System.out.println(json.toString());
                }
            }
        } catch (Exception e) {
            consumer.close();
        }
    }

    @Test
    /**
     * 关闭自动提交偏移量, 手动提交偏移量
     */
    public void testCommit() {
        while(true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record: records) {
                // 处理数据的逻辑
                System.out.printf("topic = %s, partition = %s, offset = %s, customer = %s, country = %s\r\n",
                        record.topic(), record.partition(), record.offset(), record.key(), record.value());
                // TODO 如果期间发生再均衡, (是否会重复处理该数据) -- 貌似会
                try {
                    // 提交偏移量, 同步方式, 成功提交或碰到无法恢复的错误之前会一直重试,
                    // 加大了阻塞,减小了应用程序的吞吐量, 但出现再均衡时, 重复消息少
                    consumer.commitSync();
                    // 异步方式, 支持回调, 不需要try catch, 不会重试(防止出现提交偏移量为2000的在3000之后, 导致消息重复),
                    // 加大了吞吐量, 但出现再均衡时, 重复消息多
//                    consumer.commitAsync();
                    consumer.commitAsync(new OffsetCommitCallback() {
                        @Override
                        /**
                         * 处理回调逻辑, (可用来做重试, 但一定要注意顺序)
                         */
                        public void onComplete(Map<TopicPartition, OffsetAndMetadata> map, Exception e) {
                            if(e!=null) {
                                logger.error("Commit failed for offsets {}", map, e);
                            }
                        }
                    });
                } catch (CommitFailedException e) {
                    logger.error("commit failed", e);
                }
            }
        }
    }

    @Test
    /**
     * 同步和异步提交同时使用
     * (发生关闭消费者或者再均衡前的最后一次提交)保证最后一次提交成功
     */
    public void testCommitAsyncAndSync() {
        try {
            while(true) {
                ConsumerRecords<String, String> records = consumer.poll(100);
                for(ConsumerRecord<String, String> record: records) {
                    System.out.printf("topic = %s, partition = %s, offset = %d, customer = %s, country = %s\n",
                            record.topic(), record.partition(), record.offset(), record.key(), record.value());
                }
                consumer.commitAsync();
            }
        } catch (Exception e) {
            logger.error("Unexcepted error", e);
        } finally {
            try {
                // 会重试,直到提交成功,或者发生无法恢复的错误
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }

    @Test
    /**
     * 提交特定的偏移量
     */
    public void testCommitOffset() {
        int count = 0;
        while(true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for(ConsumerRecord<String, String> record : records) {
                System.out.printf("topic = %s, partition = %s, offset = %d, customer = %s, country = %s\n",
                        record.topic(), record.partition(), record.offset(), record.key(), record.value());
                // 提交的偏移量是下一次要读的偏移量,所以+1
                currentOffsets.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(
                        record.offset()+1, "no Metadata"));
                if(count % 1000 == 0) {
                    consumer.commitAsync(currentOffsets, null);
                }
                count ++;
            }
        }
    }

    @Test
    /**
     * 再均衡监听器
     */
    public void testListener() {
        String topics = "customerCountries";
        class HandleRebalance implements ConsumerRebalanceListener {

            @Override
            /**
             * 发生再均衡之前和消费者停止读取消息时调用
             */
            public void onPartitionsRevoked(Collection<TopicPartition> collection) {
                System.out.println("Lost partitions in rebalance. Commiting current offsets: " + currentOffsets);
                consumer.commitSync(currentOffsets);
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> collection) {

            }
        }
        try {
            consumer.subscribe(Collections.singleton(topics), new HandleRebalance());
            while(true) {
                ConsumerRecords<String, String> records = consumer.poll(100);
                for(ConsumerRecord<String, String> record : records) {
                    System.out.printf("topic = %s, partition = %s, offset = %d, customer = %s, country = %s\n",
                            record.topic(), record.partition(), record.offset(), record.key(), record.value());
                    // 提交的偏移量是下一次要读的偏移量,所以+1
                    currentOffsets.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(
                            record.offset()+1, "no Metadata"));
                    }
                consumer.commitAsync(currentOffsets, null);
            }
        } catch (WakeupException e) {
            // 忽略异常,正在关闭消费者
        } catch (Exception e) {
            logger.error("Unexcepted error", e);
        } finally {
            try {
                consumer.commitSync(currentOffsets);
            } finally {
                consumer.close();
                System.out.println("Closed consumer and we are done");
            }
        }
    }

    @Test
    /**
     * 安全的退出循环
     */
    public void testExit() {
        Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Starting exit ...");
            // 关闭消费者, 抛出WakeupException
            consumer.wakeup();
            // 防止主线程关闭, 导致没有关闭消费者
            try {
                mainThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
        try {
            while(true) {
                ConsumerRecords<String, String> records = consumer.poll(1000);
                System.out.println(System.currentTimeMillis() + "-- waitting for data..." );
                for(ConsumerRecord<String, String> record : records) {
                    System.out.printf("topic = %s, partition = %s, offset = %d, customer = %s, country = %s\n",
                            record.topic(), record.partition(), record.offset(), record.key(), record.value());
                }
                for(TopicPartition tp: consumer.assignment()) {
                    System.out.println("Commiting offset at position : " + consumer.position(tp));
                }
                consumer.commitSync();
            }
        } catch (WakeupException e) {
            // 忽略关闭异常
        } finally {
            consumer.close();
            System.out.println("Closed consumer and we are done");
        }
    }

    @Test
    /**
     * 反序列化器, 一般不建议使用自定义序列化器, 耦合太强
     */
    public void testDeserializer() {

        //反序列化器的使用
        Properties props = new Properties();
        // 服务端集群地址,以逗号','分隔
        props.put("bootstrap.servers", "192.168.106.135:9092");
        // 反序列化器
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.serializer", "com.gdufe.study.kafka.test.ConsumerApiTest.CustomerDeserializer");
        // 消费者群组id
        props.put("group.id", "CountryCounter");
        KafkaConsumer<String, ProducerApiTest.Customer> testConsumer = new KafkaConsumer<>(props);
        testConsumer.subscribe(Collections.singleton("consumerCountries"));
        while(true) {
            ConsumerRecords<String, ProducerApiTest.Customer> records = testConsumer.poll(100);
            for(ConsumerRecord<String, ProducerApiTest.Customer> record : records) {
                System.out.println("current customer id : " + record.value().getCustomerId() +
                "current customer name : " + record.value().getCustomerName());
            }
        }
    }

    @Test
    /**
     * 消费者指定分区, 不加入群组, 不会出现再均衡,也不需要手动查找分区
     * TODO 注意: 当主题添加了新分区,消费者不会收到通知, 如有需要, 自己添加一些逻辑
     */
    public void genericPartition() {
        List<PartitionInfo> partitionInfos = consumer.partitionsFor("topic");
        List<TopicPartition> topicPartitions = new ArrayList<>();
        if(partitionInfos != null) {
            for(PartitionInfo info : partitionInfos) {
                topicPartitions.add(new TopicPartition(info.topic(), info.partition()));
            }
            // 指定分区
            consumer.assign(topicPartitions);
            while(true) {
                ConsumerRecords<String, String> records = consumer.poll(100);
                for(ConsumerRecord<String, String> record : records) {
                    System.out.println("-------------------------");
                }
                consumer.commitSync();
            }
        }
    }

    /**
     * 自定义反序列化器, 与生产者的CustomerSerializer对应
     */
    class CustomerDeserializer implements Deserializer<ProducerApiTest.Customer> {

        @Override
        public void configure(Map<String, ?> map, boolean b) {
            // 不做任何配置
        }

        @Override
        public ProducerApiTest.Customer deserialize(String s, byte[] bytes) {
            int id;
            int nameSize;
            String name;
            try {
                if(bytes == null) {
                    return null;
                }
                if(bytes.length < 8) {
                    throw new SerializationException("Size of data received by IntegerDeserializer is shorter than " +
                            "expected");
                }
                ByteBuffer buffer = ByteBuffer.wrap(bytes);
                id = buffer.getInt();
                nameSize = buffer.getInt();
                byte[] nameBytes = new byte[nameSize];
                buffer.get(nameBytes);
                name = new String(nameBytes, "utf-8");
                return new ProducerApiTest.Customer(id, name);
            } catch (UnsupportedEncodingException e) {
                throw new SerializationException("Error when serializing Customer to byte[] " + e);
            }
        }

        @Override
        public void close() {
            // 不需要关闭任何东西
        }
    }

}
