package org.example.newsstreaming.service;

import java.time.LocalDateTime;

/**
 * @author irfan.nagoo
 */
public interface NewsSchedulerAuditService {

    void save(String taskName, LocalDateTime startTimestamp,
                    LocalDateTime endTimestamp, String status);
}
