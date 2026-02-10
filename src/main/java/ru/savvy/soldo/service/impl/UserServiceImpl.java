package ru.savvy.soldo.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.savvy.soldo.enums.UserRole;
import ru.savvy.soldo.model.User;
import ru.savvy.soldo.repository.UserRepository;
import ru.savvy.soldo.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Override
    public User findByUsername(String username) {
        return repository.findByUsername(username).orElseThrow();
    }

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public User grantAdminRole(String username) {
       User user =  repository.findByUsername(username)
               .orElseThrow(() -> new RuntimeException(String.format(String.format("Не найден пользователь с username=%s", username))));
       user.getRoles().add(UserRole.ROLE_ADMIN);
       return repository.save(user);
    }

    @Override
    public User createUser(User user) {
        return repository.save(user);
    }
}
