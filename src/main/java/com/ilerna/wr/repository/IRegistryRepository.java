package com.ilerna.wr.repository;

import com.ilerna.wr.entity.Registry;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IRegistryRepository extends JpaRepository<Registry, Long> {
    @Query(value= "SELECT * FROM registries WHERE user_id = :userId", nativeQuery = true)
    List<Registry> findAllByUser (Long userId);
    
    @Query(value= "SELECT * FROM registries WHERE date = :date and user_id = :userId", nativeQuery = true)
    List<Registry> findByDate(LocalDate date, Long userId);
    
    @Query(value= "SELECT * FROM registries WHERE user_id = :userId ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<Registry> getLastEntry(Long userId);
    
}
