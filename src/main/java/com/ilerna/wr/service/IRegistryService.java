package com.ilerna.wr.service;

import com.ilerna.wr.entity.Registry;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IRegistryService {
    Registry create (Registry registry);
    Registry update (Long id, Registry registry);
    Registry getById (Long id);
    void deleteById (Long id);
    List<Registry> getAll();
    List<Registry> getAllByUser(Long userId);
    Optional<Registry> findById (Long id);
    List<Registry> findByDate (LocalDate date, Long userId);
    Optional<Registry> getLastEntry (Long id);
}
