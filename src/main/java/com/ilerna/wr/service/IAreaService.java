package com.ilerna.wr.service;

import com.ilerna.wr.entity.Area;
import com.ilerna.wr.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IAreaService {
    Area create (Area area);
    Area update (Long id, Area area);
    Area getById (Long id);
    void deleteById (Long id);
    List<Area> getAll();
    Optional<Area> findById (Long id);
    Optional<Area> findByName(String name);
    
    Optional<User> verify (Long userId);
    boolean associateUser (Long userId, Long areaId);
    boolean removeUser (Long userId, Long areaId);
    Set<String> userAreas (Long id);
}
