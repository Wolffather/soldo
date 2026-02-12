package ru.savvy.soldo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.savvy.soldo.enums.UserRole;

import java.util.List;
import java.util.stream.Collectors;


@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "roles")
    private List<String> roles;

    public boolean hasRole(UserRole userRole) {
        return this.getRoles().contains(userRole);
    }

    public void addRole(UserRole userRole) {
        this.getRoles().add(userRole);
    }

    public List<UserRole> getRoles() {
        return roles.stream()
                .map(roleStr -> {
                    try {
                        return UserRole.valueOf(roleStr);
                    } catch (IllegalArgumentException e) {
                        // Можно логировать или игнорировать некорректные значения
                        return null; // или исключение
                    }
                })
                .filter(role -> role != null) // фильтрация ошибок
                .collect(Collectors.toList());
    }
}
