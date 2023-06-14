package com.ilerna.wr.service.impl;

import com.ilerna.wr.entity.Area;
import com.ilerna.wr.entity.User;
import com.ilerna.wr.exception.ResourceNotFoundException;
import com.ilerna.wr.repository.IAreaRepository;
import com.ilerna.wr.service.IAreaService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class AreaServiceImpl implements IAreaService {
    
    @Autowired
    private IAreaRepository areaRepository;

    @Override
    public Area create(Area area) {
        return areaRepository.save(area);
    }

    @Override
    public Area update(Long id, Area area) {
        Area isArea = areaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(id));
    	isArea.setName(area.getName());
        return areaRepository.save(isArea);
    }

    @Override
    @Transactional(readOnly = true)
    public Area getById(Long id) {
        return areaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(id));
    }

    @Override
    public void deleteById(Long id) {
        areaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(id));
        if(areaRepository.checkUser(id).isPresent()) {
            areaRepository.deleteId(id);
        }
    	areaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Area> getAll() {
        return areaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Area> findById(Long id) {
        return areaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Area> findByName(String name) {
        return areaRepository.findByName(name);
    }

    @Override
    public Optional<User> verify(Long userId) {
        return areaRepository.verify(userId);
    }
    
    @Override
    public boolean associateUser(Long userId, Long areaId) {
        Optional<Area> existeArea = areaRepository.findById(areaId);
        Optional<User> existeUser = areaRepository.verify(userId);
        if(existeArea.isPresent() && existeUser.isPresent()) {
            if (existeArea.get().getUsers().contains(existeUser.get())) return false;
            existeArea.get().addUser(existeUser.get());
            areaRepository.save(existeArea.get());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeUser(Long userId, Long areaId) {
        Optional<Area> existeArea = areaRepository.findById(areaId);
        Optional<User> existeUser = areaRepository.verify(userId);
        if(existeArea.isPresent() && existeUser.isPresent()) {
            if (!existeArea.get().getUsers().contains(existeUser.get())) return false;
            existeArea.get().removeUser(existeUser.get());
            areaRepository.save(existeArea.get());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Set<String> userAreas(Long id) {
        return areaRepository.userAreas(id);
    }

}
