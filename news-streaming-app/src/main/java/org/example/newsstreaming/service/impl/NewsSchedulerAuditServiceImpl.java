package org.example.newsstreaming.service.impl;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.newsstreaming.entity.NewsSchedulerAudit;
import org.example.newsstreaming.repository.NewsSchedulerAuditRepository;
import org.example.newsstreaming.service.NewsSchedulerAuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author irfan.nagoo
 */

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NewsSchedulerAuditServiceImpl implements NewsSchedulerAuditService {

    private final NewsSchedulerAuditRepository auditRepository;

    @Override
    public void save(String taskName, LocalDateTime startTimestamp, LocalDateTime endTimestamp, String status) {
        log.info("Audit record request");
        try {
            NewsSchedulerAudit newsSchedulerAudit = auditRepository.findByTaskName(taskName);
            if (newsSchedulerAudit == null) {
                newsSchedulerAudit = new NewsSchedulerAudit(Uuids.timeBased(), taskName, startTimestamp, endTimestamp,
                        status);
            } else {
                newsSchedulerAudit.setStartTimestamp(startTimestamp);
                newsSchedulerAudit.setEndTimestamp(endTimestamp);
                newsSchedulerAudit.setStatus(status);
            }
            auditRepository.save(newsSchedulerAudit);
        } catch (RuntimeException e) {
            log.error("Error occurred while saving audit record: ", e);
        }
        log.info("Audit request completed!");
    }
}
