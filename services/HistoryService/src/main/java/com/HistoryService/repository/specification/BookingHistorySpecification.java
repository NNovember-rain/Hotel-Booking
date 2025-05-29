package com.HistoryService.repository.specification;

import com.HistoryService.entity.BookingHistory;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingHistorySpecification {
    public static Specification<BookingHistory> filter(Long bookingId, Long profileId, Long hotelId, Long roomId, LocalDate checkInDateStart, LocalDate checkOutDateEnd) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (bookingId != null) {
                predicates.add(cb.equal(root.get("bookingId"), bookingId));
            }
            if (profileId != null) {
                predicates.add(cb.equal(root.get("profileId"), profileId));
            }
            if (hotelId != null) {
                predicates.add(cb.equal(root.get("hotelId"), hotelId));
            }
            if (roomId != null) {
                predicates.add(cb.equal(root.get("roomId"), roomId));
            }
            if (checkInDateStart != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("checkInDate"), checkInDateStart));
            }
            if (checkOutDateEnd != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("checkOutDate"), checkOutDateEnd));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
