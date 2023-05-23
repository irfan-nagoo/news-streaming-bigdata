package org.example.sparkanalyzer.consumer;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.ArrayType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.example.sparkanalyzer.helper.PropertyLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.from_json;

/**
 * @author irfan.nagoo
 */

public class KafkaConsumer implements Consumer<Dataset<Row>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    private final SparkSession sparkSession;

    private final StructType schema;

    public KafkaConsumer(SparkSession sparkSession) {
        this(sparkSession, getNewsSchema());
    }

    public KafkaConsumer(SparkSession sparkSession, StructType schema) {
        this.sparkSession = sparkSession;
        this.schema = schema;
    }

    @Override
    public Dataset<Row> consume() {
        LOGGER.info("Initializing KafkaConsumer");
        return sparkSession
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", PropertyLoader.INSTANCE.getProperty("org.example.spark-analyzer.kafka.brokerUrl"))
                .option("subscribe", PropertyLoader.INSTANCE.getProperty("org.example.spark-analyzer.kafka.topic"))
                .option("startingOffsets", PropertyLoader.INSTANCE.getProperty("org.example.spark-analyzer.kafka.startingOffsets"))
                .load()
                // read the input json
                .selectExpr("CAST(value AS STRING)")
                .select(from_json(col("value"), schema).as("news"))
                .select("news.*");
    }


    private static StructType getNewsSchema() {
        return new StructType()
                .add("title", DataTypes.StringType)
                .add("link", DataTypes.StringType)
                .add("keywords", new ArrayType(DataTypes.StringType, true))
                .add("creator", new ArrayType(DataTypes.StringType, true))
                .add("video_url", DataTypes.StringType)
                .add("description", DataTypes.StringType)
                .add("content", DataTypes.StringType)
                .add("pubDate", DataTypes.TimestampType)
                .add("image_url", DataTypes.StringType)
                .add("source_id", DataTypes.StringType)
                .add("category", new ArrayType(DataTypes.StringType, true))
                .add("country", new ArrayType(DataTypes.StringType, true))
                .add("language", DataTypes.StringType);
    }
}
