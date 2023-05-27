package org.example.flinkanalyzer.consumer;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.example.flinkanalyzer.deserializer.JsonDeserializationSchema;
import org.example.flinkanalyzer.domain.NewsObject;
import org.example.flinkanalyzer.helper.PropertyLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author irfan.nagoo
 */

public class KafkaConsumer implements Consumer<Table> {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    private final StreamExecutionEnvironment sExecutionEnvironment;
    private final StreamTableEnvironment sTableEnvironment;

    public KafkaConsumer(StreamExecutionEnvironment sExecutionEnvironment, StreamTableEnvironment sTableEnvironment) {
        this.sExecutionEnvironment = sExecutionEnvironment;
        this.sTableEnvironment = sTableEnvironment;
    }


    @Override
    public Table consume() {
        LOGGER.info("Initializing KafkaConsumer");
        // setup kafka data source topic
        KafkaSource<NewsObject> kafkaSource = KafkaSource.<NewsObject>builder()
                .setBootstrapServers(PropertyLoader.INSTANCE.getProperty("org.example.flink-analyzer.kafka.brokerUrl"))
                .setTopics(PropertyLoader.INSTANCE.getProperty("org.example.flink-analyzer.kafka.topic"))
                .setGroupId(PropertyLoader.INSTANCE.getProperty("org.example.flink-analyzer.kafka.group"))
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new JsonDeserializationSchema())
                .build();

        // convert to new object data stream
        DataStreamSource<NewsObject> dsSource = sExecutionEnvironment
                .fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "Kafka");

        // convert to Table format
        return sTableEnvironment.fromDataStream(dsSource);
    }

}
