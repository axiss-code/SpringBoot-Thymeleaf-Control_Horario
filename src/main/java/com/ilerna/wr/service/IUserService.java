package com.ilerna.wr.service;

import com.ilerna.wr.entity.User;
import java.util.List;
import java.util.Optional;

public interface IUserService {
    User create (User user);
    User update (Long id, User user);
    User getById (Long id);
    void deleteById (Long id);
    List<User> getAll();
    Optional<User> findById (Long id);
    Optional<User> findByEmail(String email);
    Optional<String> checkEmail(String email);
}
