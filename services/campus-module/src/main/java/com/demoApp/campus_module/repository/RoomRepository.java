package com.demoApp.campus_module.repository;

import com.demoApp.campus_module.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
    List<Room> findByBuildingId(Long buildingId);
    
    List<Room> findByBuildingIdAndActive(Long buildingId, boolean active);
    
    List<Room> findByBuildingIdAndRoomType(Long buildingId, Room.RoomType roomType);
    
    List<Room> findByBuildingIdAndStatus(Long buildingId, Room.RoomStatus status);
    
    List<Room> findByRoomType(Room.RoomType roomType);
    
    List<Room> findByStatus(Room.RoomStatus status);
    
    List<Room> findByMessId(Long messId);
    
    @Query("SELECT r FROM Room r WHERE r.building.campus.id = :campusId")
    List<Room> findByCampusId(Long campusId);
    
    @Query("SELECT r FROM Room r WHERE r.building.campus.id = :campusId AND r.roomType = :roomType")
    List<Room> findByCampusIdAndRoomType(Long campusId, Room.RoomType roomType);
    
    @Query("SELECT r FROM Room r WHERE r.building.id = :buildingId ORDER BY r.floor ASC, r.number ASC")
    List<Room> findByBuildingIdOrderByFloorAndNumberAsc(Long buildingId);
    
    @Query("SELECT DISTINCT r.roomType FROM Room r WHERE r.building.id = :buildingId")
    List<Room.RoomType> findDistinctRoomTypesByBuildingId(Long buildingId);
    
    @Query("SELECT DISTINCT r.floor FROM Room r WHERE r.building.id = :buildingId ORDER BY r.floor ASC")
    List<Integer> findDistinctFloorsByBuildingId(Long buildingId);
    
    boolean existsByBuildingIdAndNumber(Long buildingId, String number);
    
    @Query("SELECT COUNT(r) FROM Room r WHERE r.building.id = :buildingId")
    Long countByBuildingId(Long buildingId);
    
    @Query("SELECT COUNT(r) FROM Room r WHERE r.building.campus.id = :campusId")
    Long countByCampusId(Long campusId);
    
    @Query("SELECT COUNT(r) FROM Room r WHERE r.roomType = :roomType AND r.building.campus.id = :campusId")
    Long countByRoomTypeAndCampusId(Room.RoomType roomType, Long campusId);
} 