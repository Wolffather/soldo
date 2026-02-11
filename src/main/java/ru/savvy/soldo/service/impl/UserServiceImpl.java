package ru.savvy.soldo.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.savvy.soldo.enums.UserRole;
import ru.savvy.soldo.exception.NotFoundException;
import ru.savvy.soldo.exception.RoleAlreadyExistsException;
import ru.savvy.soldo.model.User;
import ru.savvy.soldo.repository.UserRepository;
import ru.savvy.soldo.service.UserService;

import java.util.List;

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
               .orElseThrow(() -> new NotFoundException(String.format("Не найден пользователь с username=%s", username)));
       if (user.hasRole(UserRole.ROLE_ADMIN)) {
           throw new RoleAlreadyExistsException(String.format("Уже выданы права админа пользователю с username=%s", username));
       } else {
           user.addRole(UserRole.ROLE_ADMIN);
           return repository.save(user);
       }
    }

    @Override
    public User createUser(User user) {
        return repository.save(user);
    }
}
