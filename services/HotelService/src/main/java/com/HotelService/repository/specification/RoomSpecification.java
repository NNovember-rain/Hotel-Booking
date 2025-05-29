package com.HotelService.repository.specification;

import com.HotelService.entity.Room;
import com.HotelService.enums.RoomStatus;
import com.HotelService.enums.RoomType;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;


public class RoomSpecification {
    public static Specification<Room> filter(String roomNumber, RoomType type, Double priceFrom,
                                             Double priceTo, RoomStatus status, Long hotelId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (roomNumber != null && !roomNumber.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("roomNumber")), "%" + roomNumber.toLowerCase() + "%"));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (priceFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), priceFrom));
            }
            if (priceTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), priceTo));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (hotelId != null) {
                predicates.add(cb.equal(root.get("hotel").get("id"), hotelId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
