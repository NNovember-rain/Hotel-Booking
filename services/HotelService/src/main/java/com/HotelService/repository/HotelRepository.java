package com.HotelService.repository;

import com.HotelService.entity.Hotel;
import com.HotelService.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HotelRepository extends JpaRepository<Hotel, Long>, JpaSpecificationExecutor<Hotel> {
    @Query("SELECT h FROM Hotel h WHERE " +
            "(:name IS NULL OR lower(h.name) LIKE lower(concat('%', :name, '%'))) " +
            "AND " +
            "(:address IS NULL OR lower(h.address) LIKE lower(concat('%', :address, '%')))")
    Page<Hotel> findByNameAndAddress(
            @Param("name") String name,
            @Param("address") String address,
            Pageable pageable);
}
