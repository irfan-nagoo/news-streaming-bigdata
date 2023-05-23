package org.example.sparkanalyzer.persister;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.example.sparkanalyzer.helper.PropertyLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeoutException;

/**
 * @author irfan.nagoo
 */
public class CassandraPersister implements Persister<Dataset<Row>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraPersister.class);

    public CassandraPersister(SparkSession sparkSession) {
        this(sparkSession, PropertyLoader.INSTANCE.getProperty("org.example.spark-analyzer.cassandra.host"),
                PropertyLoader.INSTANCE.getProperty("org.example.spark-analyzer.cassandra.port"));
    }

    public CassandraPersister(SparkSession sparkSession, String host, String port) {
        sparkSession.conf().set("spark.cassandra.connection.host", host);
        sparkSession.conf().set("spark.cassandra.connection.port", port);
    }

    @Override
    public void persist(Dataset<Row> finalDataset) {
        LOGGER.info("Initializing CassandraPersister");
        try {
            StreamingQuery query = finalDataset
                    .writeStream()
                    .format("org.apache.spark.sql.cassandra")
                    .option("checkpointLocation", "/tmp/checkpoint")
                    .option("keyspace", "news_analytics")
                    .option("table", "news_sentiment")
                    .outputMode(OutputMode.Append())
                    .start();

            query.awaitTermination();
        } catch (TimeoutException e) {
            LOGGER.error("Timeout error occurred while processing: ", e);
        } catch (StreamingQueryException e) {
            LOGGER.error("Query Error occurred while processing: ", e);
        }
    }
}
