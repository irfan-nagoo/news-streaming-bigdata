package org.example.newsstreaming.repository;

import org.example.newsstreaming.entity.NewsSentiment;
import org.example.newsstreaming.entity.NewsSentimentPK;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * @author irfan.nagoo
 */

@Repository
public interface NewsSentimentRepository extends CassandraRepository<NewsSentiment, NewsSentimentPK> {

    Slice<NewsSentiment> findByKeyPartDateIn(List<LocalDate> partDateRange, Pageable pageable);

    List<NewsSentiment> findByKeyPartDateIn(List<LocalDate> partDateRange);

    Slice<NewsSentiment> findByTitleContaining(String title, Pageable pageable);

    Slice<NewsSentiment> findBySentiment(String sentiment, Pageable pageable);

    void deleteByKeyPartDate(LocalDate partDate);

}
