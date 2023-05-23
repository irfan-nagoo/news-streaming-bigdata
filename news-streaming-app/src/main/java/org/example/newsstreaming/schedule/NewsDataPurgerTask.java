package org.example.newsstreaming.schedule;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.newsstreaming.constants.ExecutionStatusType;
import org.example.newsstreaming.service.NewsSchedulerAuditService;
import org.example.newsstreaming.service.NewsSentimentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.example.newsstreaming.constants.NewsStreamingConstants.NEWS_DATA_PURGER_TASK;

/**
 * @author irfan.nagoo
 */

@Component
@RequiredArgsConstructor
@Setter
@Slf4j
public class NewsDataPurgerTask implements ScheduledTask {

    @Value("${org.example.news.streaming.scheduler.purgeDays}")
    private String purgeDays;

    private final NewsSentimentService newsSentimentService;
    private final NewsSchedulerAuditService auditService;

    @Override
    public void run() {
        log.info("Starting NewsDataPurgerTask...");
        LocalDateTime startTimestamp = LocalDateTime.now();
        ExecutionStatusType status = ExecutionStatusType.SUCCESS;
        try {
            newsSentimentService.deleteByPartitionDate(LocalDate.now()
                    .minusDays(Integer.parseInt(purgeDays)));
        } catch (RuntimeException e) {
            log.error("Error occurred while task execution: ", e);
            status = ExecutionStatusType.ERROR;
        }
        // audit this execution
        auditService.save(NEWS_DATA_PURGER_TASK, startTimestamp, LocalDateTime.now(), status.toString());
        log.info("NewsDataPurgerTask Finished!");
    }
}
