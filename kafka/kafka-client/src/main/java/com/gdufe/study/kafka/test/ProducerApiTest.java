package com.gdufe.study.kafka.test;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.record.InvalidRecordException;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.utils.Utils;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @Author: laichengfeng
 * @Description: Kafka生产者API
 * @Date: 2018/12/16 15:15
 */
public class ProducerApiTest {

    private Producer producer;

    @Before
    public void setUp() {
        // kafka生产者 配置参数
        Properties kafkaProps = new Properties();
        // 服务端集群地址,以逗号','分隔
        kafkaProps.put("bootstrap.servers", "192.168.106.135:9092");
        // 序列化器
        kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<String, String>(kafkaProps);
    }

    @Test
    /**
     * 创建生产者
     */
    public void testCreateProducer() {
//        // kafka生产者 配置参数
//        Properties kafkaProps = new Properties();
//        // 服务端集群地址,以逗号','分隔
//        kafkaProps.put("bootstrap.servers", "192.168.106.135:9092");
//        // 序列化器
//        kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        kafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//
//        producer = new KafkaProducer<String, String>(kafkaProps);
    }

    @Test
    /**
     * 消息发送
     */
    public void testSendMessage() {
        ProducerRecord<String, String> record = new ProducerRecord<>("test1",
                "Precision Products", "France");
        try {
            // 有可能产生异常

            // 最简单的发送方式，一般用于发送不太重要的数据
//            producer.send(record);

            // 同步发送方式
            producer.send(record).get();

            // 异步发送方式
//            class DemoProducerCallback implements Callback{
//                @Override
//                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
//                    if(e != null) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            producer.send(record, new DemoProducerCallback());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    /**
     * 自定义序列化器,不过一般不这么做
     * 一般使用 已有的序列化器: JSON,Avro,Thrift,Protobuf,String
     */
    public void testMySerialization() {

        // Customer序列化器
        class CustomerSerializer implements Serializer<Customer> {

            @Override
            public void configure(Map<String, ?> map, boolean b) {
                // 不做任何配置
            }

            @Override
            /**
             * Customer对象被序列化成:
             * 表示customerID的4字节整数
             * 表示customerName长度的4字节整数(如果customerName为空,则长度为0)
             * 表示customerName的N个字节
             */
            public byte[] serialize(String s, Customer customer) {
                try {
                    byte[] serializedName;
                    int stringSize;
                    if(customer != null) {
                        return null;
                    } else {
                        if (customer.getCustomerName() != null) {
                            serializedName = customer.getCustomerName().getBytes("utf-8");
                            stringSize = serializedName.length;
                        } else {
                            serializedName = new byte[0];
                            stringSize = 0;
                        }
                    }
                    ByteBuffer byteBuffer = ByteBuffer.allocate(4 + 4 + stringSize);
                    byteBuffer.putInt(customer.getCustomerId());
                    byteBuffer.putInt(stringSize);
                    byteBuffer.put(serializedName);
                    return byteBuffer.array();
                } catch (Exception e) {
                    throw new SerializationException("Error when serializing Customer to byte[] " + e);
                }
            }

            @Override
            public void close() {
                // 不需要关闭什么
            }
        }
    }

    @Test
    /**
     * AvroSerializer的使用, 需要借助Schema注册表
     */
    public void testAvroSerializer() {
        Properties prop = new Properties();
        prop.put("bootstrap.servers", "localhost:9092");
        prop.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        prop.put("valule.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        // schemaUrl指向schema存储的地方
        prop.put("schema.registry.url", "schemaUrl");
        String topic = "customerContacts";
        Producer<String, Customer> producer = new KafkaProducer<>(prop);
        // 不断生成事件
        while(true) {
            Customer customer = new Customer(1, "xxx");
            System.out.println("Generated customer " + customer.toString());
            ProducerRecord<String, Customer> record = new ProducerRecord<>(topic, customer.getCustomerId() + "", customer);
            producer.send(record);
        }


        // 使用一般的Avro对象而非生成的Avro对象
//        String schemaString = "*******************";
//        Producer<String, Customer> customerProducer = new KafkaProducer<>(prop);
//        Schema.Parser parser = new Schema.Parser();
//        Schema schema = parser.parse(schemaString);
//        for(int nCustomer = 0; nCustomer < 20; nCustomer++) {
//            String name = "exampleCustomer " + nCustomer;
//            String email = "xxx@163.com";
//            GenericRecord customer = new GenericData.Record(schema);
//        }
    }

    @Test
    /**
     * 自定义分区器
     */
    public void genericPartitioner() {
        class BananaPartitioner implements Partitioner {
            @Override
            public int partition(String s, Object o, byte[] bytes, Object o1, byte[] bytes1, Cluster cluster) {
                List<PartitionInfo> partitionInfoList = cluster.partitionsForTopic(s);
                int numPartitions = partitionInfoList.size();
                if ((bytes1 == null) || (!(o instanceof String))) {
                    throw new InvalidRecordException("We expect all messages to have customer name as key");
                }
                // Banana总是分配到最后一个分区
                // TODO 一般不直接编码客户的名字,而是通过configure
                if (((String) o).equals("Banana")) {
                    return numPartitions;
                }
                // 其他记录散列到其他分区
                return Math.abs(Utils.murmur2(bytes)) % (numPartitions - 1);
            }

            @Override
            public void close() {

            }

            @Override
            public void configure(Map<String, ?> map) {

            }
        }
    }

     static class Customer {
        private int customerId;
        private String customerName;

        public Customer(int customerId, String customerName) {
            this.customerId = customerId;
            this.customerName = customerName;
        }

        public int getCustomerId() {
            return customerId;
        }

        public void setCustomerId(int customerId) {
            this.customerId = customerId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }
    }

}
