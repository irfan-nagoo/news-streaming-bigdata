package org.example.newsstreaming.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.newsstreaming.domain.NewsObject;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author irfan.nagoo
 */

@RequiredArgsConstructor
@Slf4j
public class KafkaMessageProducer implements AsyncMessageProducer<NewsObject> {

    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final String topic;

    @Override
    public void sentMessage(NewsObject news) {
        log.info("Sending message with title [{}]", news.getTitle());
        kafkaTemplate.send(topic, news);
        log.info("Message sent successfully");
    }
}
