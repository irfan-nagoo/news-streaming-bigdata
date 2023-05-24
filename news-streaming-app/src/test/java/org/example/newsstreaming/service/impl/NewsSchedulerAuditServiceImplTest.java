package org.example.newsstreaming.service.impl;

import org.example.newsstreaming.entity.NewsSchedulerAudit;
import org.example.newsstreaming.repository.NewsSchedulerAuditRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsSchedulerAuditServiceImplTest {

    @Mock
    private NewsSchedulerAuditRepository auditRepository;

    @InjectMocks
    private NewsSchedulerAuditServiceImpl auditService;

    @Test
    void save_Success() {
        NewsSchedulerAudit audit = new NewsSchedulerAudit(UUID.randomUUID(), "TaskTask",
                LocalDateTime.now(), LocalDateTime.now(), "SUCCESS");
        when(auditRepository.findByTaskName(any())).thenReturn(audit);
        assertDoesNotThrow(() -> auditService.save("TestTask", LocalDateTime.now().minusDays(1), LocalDateTime.now(),
                "SUCCESS"));
    }

    @Test
    void save_Exception() {
        when(auditRepository.findByTaskName(any())).thenReturn(null);
        when(auditRepository.save(any(NewsSchedulerAudit.class))).thenThrow(new RuntimeException("Test Error"));
        assertDoesNotThrow(() -> auditService.save("TestTask", LocalDateTime.now().minusDays(1), LocalDateTime.now(),
                "SUCCESS"));

    }
}