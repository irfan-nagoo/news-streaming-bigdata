package org.example.flinkanalyzer.persister;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.extras.codecs.jdk8.LocalDateCodec;
import com.datastax.driver.extras.codecs.jdk8.LocalDateTimeCodec;
import com.datastax.driver.mapping.Mapper;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.cassandra.CassandraSink;
import org.apache.flink.streaming.connectors.cassandra.ClusterBuilder;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.example.flinkanalyzer.entity.NewsSentiment;
import org.example.flinkanalyzer.helper.PropertyLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @author irfan.nagoo
 */
public class CassandraPersister implements Persister<Table> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraPersister.class);

    private final StreamExecutionEnvironment sExecutionEnvironment;
    private final StreamTableEnvironment sTableEnvironment;
    private final String host;

    private final int port;

    public CassandraPersister(StreamExecutionEnvironment sExecutionEnvironment, StreamTableEnvironment sTableEnvironment) {
        this(sExecutionEnvironment, sTableEnvironment, PropertyLoader.INSTANCE.getProperty("org.example.flink-analyzer.cassandra.host"),
                PropertyLoader.INSTANCE.getProperty("org.example.flink-analyzer.cassandra.port"));
    }

    public CassandraPersister(StreamExecutionEnvironment sExecutionEnvironment, StreamTableEnvironment sTableEnvironment
            , String host, String port) {
        this.sExecutionEnvironment = sExecutionEnvironment;
        this.sTableEnvironment = sTableEnvironment;
        this.host = host;
        this.port = Integer.parseInt(port);
    }

    @Override
    public void persist(Table finalTable) {
        LOGGER.info("Initializing CassandraPersister");
        try {
            // convert from table to data stream of new sentiment objects
            DataStream<NewsSentiment> newsSentimentDs = sTableEnvironment.toDataStream(finalTable, NewsSentiment.class);
            // sink to cassandra db
            new CassandraSink.CassandraPojoSinkBuilder<>(newsSentimentDs, newsSentimentDs.getType(),
                    newsSentimentDs.getType().createSerializer(newsSentimentDs.getExecutionEnvironment().getConfig()))
                    .setClusterBuilder(new FlinkClusterBuilder(host, port))
                    .setMapperOptions(() -> new Mapper.Option[]{Mapper.Option.saveNullFields(true)})
                    .build();

            sExecutionEnvironment.execute(PropertyLoader.INSTANCE.getProperty("org.example.flink-analyzer.appName"));
        } catch (Exception e) {
            LOGGER.error("Error occurred while persisting the record: ", e);
        }
    }

    private static class FlinkClusterBuilder extends ClusterBuilder implements Serializable {
        private final String host;
        private final int port;

        private FlinkClusterBuilder(String host, int port) {
            this.host = host;
            this.port = port;
        }

        @Override
        protected Cluster buildCluster(Cluster.Builder builder) {
            Cluster cluster = builder.addContactPoint(host)
                    .withPort(port)
                    .build();
            // these Codecs are required to convert to/from Java 8 LocalDate and LocalDateTime objects
            cluster.getConfiguration().getCodecRegistry().register(LocalDateTimeCodec.instance);
            cluster.getConfiguration().getCodecRegistry().register(LocalDateCodec.instance);
            return cluster;
        }
    }
}
