package org.example.newsstreaming.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.newsstreaming.domain.NewsObject;
import org.example.newsstreaming.producer.AsyncMessageProducer;
import org.example.newsstreaming.producer.KafkaMessageProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author irfan.nagoo
 */

@Configuration
public class KafkaConfig {

    @Value("${org.example.news.streaming.kafka.bootstrapServers}")
    private String bootstrapServers;
    @Value("${org.example.news.streaming.kafka.topic}")
    private String newsTopic;

    @Bean
    public ProducerFactory<Object, Object> producerFactory() {
        Map<String, Object> kafkaProperties = new HashMap<>();
        kafkaProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        kafkaProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        kafkaProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(kafkaProperties);
    }

    @Bean
    public KafkaTemplate<Object, Object> kafkaTemplate(ProducerFactory<Object, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public AsyncMessageProducer<NewsObject> newsProducer(KafkaTemplate<Object, Object> kafkaTemplate) {
        return new KafkaMessageProducer(kafkaTemplate, newsTopic);
    }

}
