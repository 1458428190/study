package com.gdufe.study.kafka.stream;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @Author: laichengfeng
 * @Description: 使用Streams做字数统计
 * @Date: 2018/12/20 11:28
 */
public class WordCountExample {
    public static void main(String[] args) {
        Properties props = new Properties();
        // 应用ID, kafka集群中必须唯一
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.106.135:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        // 创建拓扑流程
        StreamsBuilder builder = new StreamsBuilder();
        // 指向输入主题
        KStream<String, String> source = builder.stream("wordcount-input");
        // 正则表达式,匹配[^a-zA-Z0-9_]
        final Pattern pattern = Pattern.compile("\\W+");

        KStream counts = source
                // 分隔成单词
                .flatMapValues(value-> Arrays.asList(pattern.split(value.toLowerCase())))
                // 映射成map
                .map((key, value) -> new KeyValue<Object, Object>(value, value))
                // 过滤单词the
                .filter((key, value) -> (!value.equals("the")))
                // 根据key分组
                .groupByKey()
                // 统计key
                .count()
                // 转换value成long
                .mapValues(value->Long.toString(value)).toStream();
        // 把结果写回到Kafka
        counts.to("wordcount-output");

        // 运行这个流程 (旧版本的API)
//        KafkaStreams streams = new KafkaStreams(builder, props);
//        streams.start();
        // 一般情况下, Stream会一直运行下去
//        Thread.sleep(5000L);
//        streams.close();

    }
}
