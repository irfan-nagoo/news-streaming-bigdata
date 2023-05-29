package org.example.flinkanalyzer.persister;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.extras.codecs.jdk8.LocalDateCodec;
import com.datastax.driver.extras.codecs.jdk8.LocalDateTimeCodec;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.cassandra.CassandraSink;
import org.apache.flink.streaming.connectors.cassandra.ClusterBuilder;
import org.example.flinkanalyzer.entity.NewsSentiment;
import org.example.flinkanalyzer.helper.PropertyLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @author irfan.nagoo
 */
public class CassandraPersister implements Persister<DataStream<NewsSentiment>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraPersister.class);

    private final String host;

    private final int port;

    public CassandraPersister() {
        this(PropertyLoader.INSTANCE.getProperty("org.example.flink-analyzer.cassandra.host"),
                PropertyLoader.INSTANCE.getProperty("org.example.flink-analyzer.cassandra.port"));
    }

    public CassandraPersister(String host, String port) {
        this.host = host;
        this.port = Integer.parseInt(port);
    }

    @Override
    public void persist(DataStream<NewsSentiment> newsSentimentDS) {
        LOGGER.info("Initializing CassandraPersister");
        try {
            // sink to cassandra db
            CassandraSink
                    .addSink(newsSentimentDS)
                    .setClusterBuilder(new FlinkCassandraCluster(host, port))
                    .build();
        } catch (Exception e) {
            LOGGER.error("Error occurred while persisting the record: ", e);
        }
    }

    private static class FlinkCassandraCluster extends ClusterBuilder implements Serializable {
        private final String host;
        private final int port;

        private FlinkCassandraCluster(String host, int port) {
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
