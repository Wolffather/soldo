package ru.savvy.soldo.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.savvy.soldo.dto.UserDTO;
import ru.savvy.soldo.exception.NotFoundException;
import ru.savvy.soldo.mapper.UserMapper;
import ru.savvy.soldo.model.User;
import ru.savvy.soldo.service.impl.UserServiceImpl;
import ru.savvy.soldo.utils.JwtUtil;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthorizationController {

    private final UserServiceImpl service;
    @Autowired
    private UserMapper mapper;

    @Autowired
    public AuthorizationController(UserServiceImpl service) {
        this.service = service;
    }

    @PostMapping("/register")
    public Map<String, String> register(@Valid @RequestBody UserDTO userDTO) {
        User user = mapper.dtoToEntity(userDTO);

        User userSaved = service.createUser(user);

        String token = JwtUtil.generateToken(userSaved.getId().toString(), userSaved.getRole());
        return Map.of("token", "Bearer " + token);

    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody String username) {
        User user = service.findByUsername(username);

        String token = JwtUtil.generateToken(username, user.getRole());
        return Map.of("token", token);
    }


}
