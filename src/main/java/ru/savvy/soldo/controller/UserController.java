package ru.savvy.soldo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.dto.UserDTO;
import ru.savvy.soldo.mapper.UserMapper;
import ru.savvy.soldo.model.Event;
import ru.savvy.soldo.model.User;
import ru.savvy.soldo.service.impl.UserServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserServiceImpl service;
    @Autowired
    private UserMapper mapper;

    @GetMapping
    public List<User> getAllUsers() {
        return service.getAllUsers();
    }

    @PostMapping("/create")
    public User create(@RequestBody UserDTO userDTO) {
        User user = mapper.dtoToEntity(userDTO);
        return service.createUser(user);
    }

    @PutMapping("/grant-admin-role")
    public User grantAdminRole(@RequestBody String username) {
        return service.grantAdminRole(username);
    }
}
