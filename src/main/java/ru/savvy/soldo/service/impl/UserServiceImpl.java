package ru.savvy.soldo.service.impl;

import jakarta.transaction.Transactional;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.savvy.soldo.enums.UserRole;
import ru.savvy.soldo.exception.DataDuplicationException;
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
        return repository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(String.format("Не найден пользователь с username=%s", username)));
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
       if (user.hasRole(UserRole.ADMIN)) {
           throw new RoleAlreadyExistsException(String.format("Уже выданы права админа пользователю с username=%s", username));
       } else {
           user.addRole(UserRole.ADMIN);
           return repository.save(user);
       }
    }

    @Override
    public User createUser(User user) {
        try {
            return repository.save(user);
        } catch (DataIntegrityViolationException e) {
            Throwable cause = e.getRootCause();
            if (cause instanceof PSQLException psqlEx) {
                if (psqlEx.getMessage().contains("violates unique constraint")) {
                    throw new DataDuplicationException("Пользователь уже создан");
                }
            }
            return null;
        }
    }
}
