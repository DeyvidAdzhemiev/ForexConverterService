package com.forex.repository;

import com.forex.entity.ConversionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ConversionRepository extends JpaRepository<ConversionEntity, String> {
    
    Page<ConversionEntity> findByTransactionId(String transactionId, Pageable pageable);
    
    Page<ConversionEntity> findByTimestampBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}
