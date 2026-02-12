package ru.savvy.soldo.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.dto.UserDTO;
import ru.savvy.soldo.mapper.UserMapper;
import ru.savvy.soldo.model.User;
import ru.savvy.soldo.service.impl.UserServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserServiceImpl service;
    @Autowired
    private UserMapper mapper;

    @Autowired
    public UserController(UserServiceImpl service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<User> getAllUsers() {
        return service.getAllUsers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{username}")
    public User findUser(@PathVariable("username") String username) {
        return service.findByUsername(username);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/grant-admin-role/{username}")
    public User grantAdminRole(@PathVariable("username") String username) {
        return service.grantAdminRole(username);
    }
}
