package org.example.newsstreaming.producer;

import org.example.newsstreaming.domain.NewsObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class KafkaMessageProducerTest {

    @Mock
    private KafkaTemplate<Object, Object> kafkaTemplate;

    private AsyncMessageProducer<NewsObject> producer;

    @Test
    void sentMessage() {
        NewsObject news = new NewsObject();
        news.setTitle("Test Title");
        news.setContent("Test content");
        news.setPubDate(LocalDateTime.now());
        producer = new KafkaMessageProducer(kafkaTemplate, "testTopic");
        assertDoesNotThrow(() -> producer.sentMessage(news));
    }
}