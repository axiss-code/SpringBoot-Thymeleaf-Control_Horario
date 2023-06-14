package com.ilerna.wr.service.impl;

import com.ilerna.wr.dto.UserWrapper;
import com.ilerna.wr.entity.Roles;
import com.ilerna.wr.entity.User;
import com.ilerna.wr.exception.ResourceNotFoundException;
import com.ilerna.wr.repository.IUserRepository;
import com.ilerna.wr.service.IUserService;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
//@Slf4j
public class UserServiceImpl implements IUserService, UserDetailsService{
    
    @Autowired
    private IUserRepository userRepository;
    
    @Autowired
    HttpSession session;
    
    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(Long id, User user) {
        User isUser = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(id));
    	isUser.setEmail(user.getEmail());
    	isUser.setPassword(user.getPassword());
        isUser.setRole(user.getRole());
        isUser.setName(user.getName());
        isUser.setIsActive(user.getIsActive());
        return userRepository.save(isUser);
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(id));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(id));
        if (userRepository.checkArea(id).isPresent()) {
            userRepository.deleteId(id);
        }
    	userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);  
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> optUser = findByEmail(email);
        if (optUser.isPresent()) {
            User u = optUser.get();
            setSessionParams (u);
            //log.info("*-*-*-*-* loadUserByUsername: {}", u);
            return new UserWrapper (u);
        } else {
            throw new UsernameNotFoundException("Usuario no encontrado " + email);
        }
    }
    
    @Override
    public Optional<String> checkEmail(String email) {
        return userRepository.checkEmail(email);
    }
    
    private void setSessionParams (User u) {
        String sessionRole = u.getRole().toString().equals("ROLE_ANONYMOUS") ? 
                             "Invitado" : Roles.getNames().get(u.getRole());
        session.setAttribute("user_role", sessionRole);
        session.setAttribute("user_id", u.getId());
        session.setAttribute("user_name", u.getName());
    }

}
