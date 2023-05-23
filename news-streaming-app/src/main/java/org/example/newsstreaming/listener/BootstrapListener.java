package org.example.newsstreaming.listener;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.newsstreaming.schedule.NewsDataPurgerTask;
import org.example.newsstreaming.schedule.NewsDataRetrieverTask;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author irfan.nagoo
 */

@Component
@Setter
@Slf4j
public class BootstrapListener implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${org.example.news.streaming.scheduler.enable}")
    private String enable;
    @Value("${org.example.news.streaming.scheduler.retrieveInterval}")
    private String retrieveInterval;
    @Value("${org.example.news.streaming.scheduler.purgeInterval}")
    private String purgeInterval;

    private boolean isInitialized;
    private final ScheduledExecutorService executorService;
    private final NewsDataRetrieverTask newDataRetrieverTask;
    private final NewsDataPurgerTask newsDataPurgerTask;

    public BootstrapListener(NewsDataRetrieverTask newDataRetrieverTask, NewsDataPurgerTask newsDataPurgerTask) {
        this.newDataRetrieverTask = newDataRetrieverTask;
        this.newsDataPurgerTask = newsDataPurgerTask;
        this.executorService = Executors.newScheduledThreadPool(2);
    }

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        if (Boolean.parseBoolean(enable) && !isInitialized) {
            this.executorService.scheduleAtFixedRate(newDataRetrieverTask, 1, Integer.parseInt(retrieveInterval), TimeUnit.MINUTES);
            this.executorService.scheduleAtFixedRate(newsDataPurgerTask, 1, Integer.parseInt(purgeInterval), TimeUnit.HOURS);
            isInitialized = true;
            log.info("Scheduled Tasks Initialized Successfully");
        }
    }
}
