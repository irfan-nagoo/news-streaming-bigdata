package org.example.newsstreaming.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author irfan.nagoo
 */

@Table("news_scheduler_audit")
@AllArgsConstructor
@Getter
@Setter
public class NewsSchedulerAudit {

    @PrimaryKey
    private UUID id;
    @Column("task_name")
    private String taskName;
    @Column("start_timestamp")
    private LocalDateTime startTimestamp;
    @Column("end_timestamp")
    private LocalDateTime endTimestamp;
    private String status;

}
