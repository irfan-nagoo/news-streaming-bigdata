package org.example.sparkanalyzer;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.example.sparkanalyzer.analyzer.SentimentAnalyzer;
import org.example.sparkanalyzer.consumer.KafkaConsumer;
import org.example.sparkanalyzer.consumer.Consumer;
import org.example.sparkanalyzer.helper.PropertyLoader;
import org.example.sparkanalyzer.persister.CassandraPersister;
import org.example.sparkanalyzer.persister.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author irfan.nagoo
 */

public class SparkAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparkAnalyzer.class);

    public static void main(String[] args) {
        String appName = PropertyLoader.INSTANCE.getProperty("org.example.spark-analyzer.appName");
        String master = PropertyLoader.INSTANCE.getProperty("org.example.spark-analyzer.master");
        LOGGER.info("Initializing {}", appName);
        // connect to Spark cluster
        SparkSession sparkSession = SparkSession.builder()
                .master(master)
                .appName(appName)
                .getOrCreate();

        // consume json messages from Kafka topic
        Consumer<Dataset<Row>> kafkaConsumer = new KafkaConsumer(sparkSession);
        Dataset<Row> newsDataset = kafkaConsumer.consume();

        // enrich data with realtime Sentiment analysis of news content
        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer(sparkSession);
        Dataset<Row> analyzedDataset = sentimentAnalyzer.analyze(newsDataset);

        // persist final data set to Cassandra data sink
        Persister<Dataset<Row>> cassandraPersister = new CassandraPersister(sparkSession);
        cassandraPersister.persist(analyzedDataset);
        LOGGER.info("Initialization Finished!");
    }

}
