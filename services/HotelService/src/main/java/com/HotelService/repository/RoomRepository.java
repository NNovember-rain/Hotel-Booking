package com.HotelService.repository;

import com.HotelService.entity.Room;
import com.HotelService.enums.RoomStatus;
import com.HotelService.enums.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {
    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelIdParam AND " + // Điều kiện bắt buộc theo hotelId
            "(:typeParam IS NULL OR r.type = :typeParam) AND " +
            "(:priceFromParam IS NULL OR r.price >= :priceFromParam) AND " +
            "(:priceToParam IS NULL OR r.price <= :priceToParam) AND " +
            "(:statusParam IS NULL OR r.status = :statusParam)")
    Page<Room> findRoomsByHotelAndOptionalCriteria(
            @Param("hotelIdParam") Long hotelId, // Tham số mới cho hotelId
            @Param("typeParam") RoomType type,
            @Param("priceFromParam") Double priceFrom,
            @Param("priceToParam") Double priceTo,
            @Param("statusParam") RoomStatus status,
            Pageable pageable);
}
