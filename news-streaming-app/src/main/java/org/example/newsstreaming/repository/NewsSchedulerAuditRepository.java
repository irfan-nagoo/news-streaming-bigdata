package org.example.newsstreaming.repository;

import org.example.newsstreaming.entity.NewsSchedulerAudit;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @author irfan.nagoo
 */

@Repository
public interface NewsSchedulerAuditRepository extends CassandraRepository<NewsSchedulerAudit, UUID> {

    NewsSchedulerAudit findByTaskName(String name);
}
