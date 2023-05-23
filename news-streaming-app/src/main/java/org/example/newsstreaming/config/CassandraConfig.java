package org.example.newsstreaming.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

/**
 * @author irfan.nagoo
 */

@Configuration
@EnableCassandraRepositories(basePackages = "org.example.newsstreaming.repository")
public class CassandraConfig extends AbstractCassandraConfiguration {

    @Value("${org.example.news.streaming.cassandra.contact-points}")
    private String hosts;
    @Value("${org.example.news.streaming.cassandra.port}")
    private String port;
    @Value("${org.example.news.streaming.cassandra.keyspace-name}")
    private String keySpaceName;

    @Override
    protected String getContactPoints() {
        return hosts;
    }

    @Override
    protected int getPort() {
        return Integer.parseInt(port);
    }

    @Override
    protected String getKeyspaceName() {
        return keySpaceName;
    }
}
