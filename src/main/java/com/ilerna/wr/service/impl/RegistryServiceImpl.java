package com.ilerna.wr.service.impl;

import com.ilerna.wr.entity.Registry;
import com.ilerna.wr.exception.ResourceNotFoundException;
import com.ilerna.wr.repository.IRegistryRepository;
import com.ilerna.wr.service.IRegistryService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class RegistryServiceImpl implements IRegistryService{
    
    @Autowired
    private IRegistryRepository registryRepository;

    @Override
    public Registry create(Registry registry) {
        return registryRepository.save(registry);
    }

    @Override
    public Registry update(Long id, Registry registry) {
        Registry isReg = registryRepository.findById(id).orElseThrow(
			() -> new ResourceNotFoundException(id));
        isReg.setDate(registry.getDate());
        isReg.setTimeIn(registry.getTimeIn());
        isReg.setTimeOut(registry.getTimeOut());
        isReg.setIsClosed(registry.getIsClosed());
        return registryRepository.save(isReg);
    }

    @Override
    @Transactional(readOnly = true)
    public Registry getById(Long id) {
        return registryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(id));
    }

    @Override
    public void deleteById(Long id) {
        registryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(id));
    	registryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Registry> getAll() {
        return registryRepository.findAll();
    }
    
    @Override
    public List<Registry> getAllByUser(Long userId) {
        return registryRepository.findAllByUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Registry> findById(Long id) {
        return registryRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Registry> findByDate(LocalDate date, Long userId) {
        return registryRepository.findByDate(date, userId);
    }

    @Override
    public Optional<Registry> getLastEntry(Long userId) {
        return registryRepository.getLastEntry(userId);
    }
    
}
