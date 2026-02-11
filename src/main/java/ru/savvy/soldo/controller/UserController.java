package ru.savvy.soldo.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping
    public List<User> getAllUsers() {
        return service.getAllUsers();
    }

    @PostMapping("/create")
    public User create(@RequestBody UserDTO userDTO) {
        User user = mapper.dtoToEntity(userDTO);
        return service.createUser(user);
    }

    @PatchMapping("/grant-admin-role/{username}")
    public User grantAdminRole(@PathVariable("username") String username) {
        return service.grantAdminRole(username);
    }
}
