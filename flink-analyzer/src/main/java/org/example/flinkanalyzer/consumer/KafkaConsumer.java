package org.example.flinkanalyzer.consumer;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.example.flinkanalyzer.deserializer.JsonDeserializationSchema;
import org.example.flinkanalyzer.domain.NewsObject;
import org.example.flinkanalyzer.helper.PropertyLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author irfan.nagoo
 */

public class KafkaConsumer implements Consumer<DataStream<NewsObject>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    private final StreamExecutionEnvironment sExecutionEnvironment;

    public KafkaConsumer(StreamExecutionEnvironment sExecutionEnvironment) {
        this.sExecutionEnvironment = sExecutionEnvironment;

    }

    @Override
    public DataStream<NewsObject> consume() {
        LOGGER.info("Initializing KafkaConsumer");
        // setup kafka data source topic
        KafkaSource<NewsObject> kafkaSource = KafkaSource.<NewsObject>builder()
                .setBootstrapServers(PropertyLoader.INSTANCE.getProperty("org.example.flink-analyzer.kafka.brokerUrl"))
                .setTopics(PropertyLoader.INSTANCE.getProperty("org.example.flink-analyzer.kafka.topic"))
                .setGroupId(PropertyLoader.INSTANCE.getProperty("org.example.flink-analyzer.kafka.group"))
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new JsonDeserializationSchema())
                .build();

        // consume new object data stream messages
        return sExecutionEnvironment
                .fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "Kafka");
    }

}
