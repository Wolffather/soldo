package ru.savvy.soldo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.savvy.soldo.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
