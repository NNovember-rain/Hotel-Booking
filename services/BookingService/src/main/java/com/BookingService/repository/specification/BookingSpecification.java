package com.BookingService.repository.specification;


import com.BookingService.entity.Booking;
import com.BookingService.enums.BookingStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingSpecification {
    public static Specification<Booking> filter(String userId, Long hotelId, Long roomId, LocalDate checkInDateStart, LocalDate checkOutDateEnd, BookingStatus status, Double minPrice, Double maxPrice) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (userId != null) {
                predicates.add(cb.equal(root.get("userId"), userId));
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
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
