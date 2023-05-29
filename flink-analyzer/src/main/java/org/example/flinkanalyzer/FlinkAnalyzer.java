package org.example.flinkanalyzer;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.example.flinkanalyzer.analyzer.SentimentAnalyzer;
import org.example.flinkanalyzer.consumer.Consumer;
import org.example.flinkanalyzer.consumer.KafkaConsumer;
import org.example.flinkanalyzer.domain.NewsObject;
import org.example.flinkanalyzer.entity.NewsSentiment;
import org.example.flinkanalyzer.helper.PropertyLoader;
import org.example.flinkanalyzer.persister.CassandraPersister;
import org.example.flinkanalyzer.persister.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author irfan.nagoo
 */

public class FlinkAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlinkAnalyzer.class);

    public static void main(String[] args) throws Exception {
        LOGGER.info("Initializing NewsAnalyzer");
        // connect to Flink cluster
        StreamExecutionEnvironment sExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();

        // consume json messages from Kafka topic
        Consumer<DataStream<NewsObject>> kafkaConsumer = new KafkaConsumer(sExecutionEnvironment);
        DataStream<NewsObject> newsDS = kafkaConsumer.consume();

        // enrich data with realtime Sentiment analysis of news content
        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();
        DataStream<NewsSentiment> newsSentimentDS = sentimentAnalyzer.analyze(newsDS);

        // persist final data set to Cassandra data sink
        Persister<DataStream<NewsSentiment>> cassandraPersister = new CassandraPersister();
        cassandraPersister.persist(newsSentimentDS);

        sExecutionEnvironment.execute(PropertyLoader.INSTANCE.getProperty("org.example.flink-analyzer.appName"));
        LOGGER.info("Initialization Finished!");
    }

}
