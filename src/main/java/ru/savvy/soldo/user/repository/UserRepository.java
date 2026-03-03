package ru.savvy.soldo.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.savvy.soldo.user.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<User> findByFirstNameAndLastNameAndUsername(
            String firstName, String lastName, String username);
}
