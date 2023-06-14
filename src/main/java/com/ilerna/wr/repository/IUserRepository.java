package com.ilerna.wr.repository;

import com.ilerna.wr.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    @Query(value= "SELECT email FROM users WHERE email = :email", nativeQuery = true)
    Optional<String> checkEmail(String email);
    
    @Query(value= "SELECT user_id FROM users_areas WHERE user_id = :id LIMIT 1", nativeQuery = true)
    Optional<Long> checkArea(Long id);
    
    @Modifying
    @Query(value= "DELETE FROM users_areas WHERE user_id = :id", nativeQuery = true)
    void deleteId (Long id);
}
