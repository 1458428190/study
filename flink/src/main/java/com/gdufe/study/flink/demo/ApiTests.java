/**
 * @(#)ApiTests.java, 2018/10/24.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.gdufe.study.flink.demo;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.FoldFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.AllWindowedStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.IterativeStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.Window;
import org.apache.flink.util.Collector;
import org.junit.Test;

/**
 * @author: laichengfeng (laichengfeng @ corp.netease.com)
 * @description:
 * @Date: 2018/10/24 9:30
 */
public class ApiTests {
    public static void main(String[] args) {

    }

    @Test
    public void testDataStream() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<Long> dataStream = env.generateSequence(0, 1000);

        // DataStream -> DataStream, Map-一个元素转为一个元素
        DataStream<Long> mapDataStream = dataStream.map(new MapFunction<Long, Long>() {
            @Override
            public Long map(Long aLong) throws Exception {
                return 2 * aLong;
            }
        });

        // DataStream -> DataStream, FlatMap-一个元素转换为0,1或多个元素
        DataStream<Long> flatMapDataStream = dataStream.flatMap(new FlatMapFunction<Long, Long>() {
            @Override
            public void flatMap(Long aLong, Collector<Long> collector) throws Exception {
                // 取出每个位数
                while(aLong > 0) {
                    collector.collect(aLong % 10);
                    aLong /= 10;
                }
            }
        });

        // DataStream -> DataStream, Filter-保留返回真的元素
        DataStream<Long> filterDataStream = dataStream.filter(new FilterFunction<Long>() {
            @Override
            public boolean filter(Long aLong) throws Exception {
                // 过滤偶数
                return (aLong & 1) > 0;
            }
        });

        // DataStream -> KeyedStream, keyBy-通过Hash分出不相交的分区
        KeyedStream<Long, Tuple> someKey = dataStream.keyBy("someKey");
        KeyedStream<Long, Tuple> longTupleKeyedStream = dataStream.keyBy(0);

        // KeyedStream -> DataStream, reduce-在分区中进行减少操作
        // TODO 将当前元素与上一个reduce后的值进行合并,返回新的合并的值？
        DataStream<Long> reduceDataStream = someKey.reduce(new ReduceFunction<Long>() {
            @Override
            public Long reduce(Long aLong, Long t1) throws Exception {
                return aLong + t1;
            }
        });

        // KeyedStream -> DataStream, fold-已过时
        // TODO 在一个KeyedStream上基于初始值不断进行变换操作，将当前值与上一个变换后的值进行变换，再返回新变换的值。?
        DataStream<String> foldDataStream = someKey.fold("start", new FoldFunction<Long, String>() {
            @Override
            public String fold(String s, Long o) throws Exception {
                return s + "-" + o;
            }
        });

        // KeyedStream -> DataStream, 聚合
        someKey.sum(0);  // 返回第0个字段的总和（数值型）
        someKey.sum("key");       // 返回key字段的总和
        someKey.min(0);   // 返回第0个字段的最小值
        someKey.min("key");        // 返回key字段的最小值
        someKey.max(0);    // 最大
        someKey.max("key");         // 最大
        someKey.minBy(0);  // 最小值的所有元素
        someKey.maxBy(0);
        someKey.maxBy("key");

        // KeyedStream -> WindowedStream
        // Windows可定义在已分区的KeyedStreams上。Windows会在每个key对应的数据上根据一些特征（例如，在最近5秒内到达的数据）进行分组
        WindowedStream windowedStream = someKey.window(TumblingEventTimeWindows.of(Time.seconds(5)));

        // DataStream -> AllWindowedStream
        AllWindowedStream allWindowedStream = dataStream.windowAll(TumblingEventTimeWindows.of(Time.seconds(5)));

        // WindowedStream -> DataStream AllWindowedStream -> DataStream
        windowedStream.apply(new WindowFunction<Tuple2<String, Integer>, Integer, Tuple, Window>() {
            @Override
            public void apply(Tuple tuple, Window window, Iterable<Tuple2<String, Integer>> input, Collector<Integer> out) throws Exception {
                int sum = 0;
                for(Tuple2<String, Integer> t: input) {
                    sum += t.f1;
                }
                out.collect(sum);
            }
        });

        allWindowedStream.apply(new AllWindowFunction<Tuple2<String,Integer>, Integer, Window>() {
            @Override
            public void apply(Window window, Iterable<Tuple2<String, Integer>> values, Collector<Integer> out) throws Exception {
                int sum = 0;
                for(Tuple2<String, Integer> t: values) {
                    sum += t.f1;
                }
                out.collect(sum);
            }
        });

        

    }
}