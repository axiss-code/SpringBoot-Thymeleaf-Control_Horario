package com.ilerna.wr.repository;

import com.ilerna.wr.entity.Area;
import com.ilerna.wr.entity.User;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IAreaRepository extends JpaRepository<Area, Long> {
    
    Optional<Area> findByName(String name);
    
    @Query("select usr from User usr where usr.id=:id")
    Optional<User> verify (Long id);
    
    @Query(value = "SELECT areas.name FROM areas, users_areas WHERE areas.id = users_areas.area_id AND users_areas.user_id = :id",
            nativeQuery = true)
    Set<String> userAreas (Long id);
    
    @Query(value= "SELECT area_id FROM users_areas WHERE area_id = :id LIMIT 1", nativeQuery = true)
    Optional<Long> checkUser(Long id);
    
    @Modifying
    @Query(value= "DELETE FROM users_areas WHERE area_id = :id", nativeQuery = true)
    void deleteId (Long id);
    
}
