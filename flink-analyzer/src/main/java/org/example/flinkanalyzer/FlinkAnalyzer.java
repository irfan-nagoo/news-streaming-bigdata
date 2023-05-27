package org.example.flinkanalyzer;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.example.flinkanalyzer.analyzer.SentimentAnalyzer;
import org.example.flinkanalyzer.consumer.Consumer;
import org.example.flinkanalyzer.consumer.KafkaConsumer;
import org.example.flinkanalyzer.persister.CassandraPersister;
import org.example.flinkanalyzer.persister.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author irfan.nagoo
 */

public class FlinkAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlinkAnalyzer.class);

    public static void main(String[] args) {
        LOGGER.info("Initializing NewsAnalyzer");
        // connect to Flink cluster
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .inStreamingMode()
                .build();
        StreamExecutionEnvironment sExecutionEnvironment= StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment sTableEnvironment = StreamTableEnvironment.create(sExecutionEnvironment, settings);

        // consume json messages from Kafka topic
        Consumer<Table> kafkaConsumer = new KafkaConsumer(sExecutionEnvironment, sTableEnvironment);
        Table table = kafkaConsumer.consume();

        // enrich data with realtime Sentiment analysis of news content
        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer(sTableEnvironment);
        Table finalTable = sentimentAnalyzer.analyze(table);

        // persist final data set to Cassandra data sink
        Persister<Table> cassandraPersister = new CassandraPersister(sExecutionEnvironment, sTableEnvironment);
        cassandraPersister.persist(finalTable);
        LOGGER.info("Initialization Finished!");
    }

}
