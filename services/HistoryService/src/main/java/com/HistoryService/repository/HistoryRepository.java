package com.HistoryService.repository;

import com.HistoryService.entity.BookingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface HistoryRepository extends JpaRepository<BookingHistory, Long>, JpaSpecificationExecutor<BookingHistory> {
}
