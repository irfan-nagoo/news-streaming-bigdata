package org.example.newsstreaming.schedule;

import org.example.newsstreaming.service.NewsSchedulerAuditService;
import org.example.newsstreaming.service.NewsSentimentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsDataPurgerTaskTest {

    @Mock
    private NewsSentimentService newsSentimentService;

    @Mock
    private NewsSchedulerAuditService auditService;

    @InjectMocks
    private NewsDataPurgerTask dataPurgerTask;


    @Test
    void run() {
        dataPurgerTask.setPurgeDays("30");
        doNothing().when(newsSentimentService).deleteByPartitionDate(any());
        doNothing().when(auditService).save(anyString(), any(), any(), anyString());

        assertDoesNotThrow(dataPurgerTask::run);
    }
}