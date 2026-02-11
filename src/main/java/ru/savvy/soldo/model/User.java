package ru.savvy.soldo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.savvy.soldo.enums.UserRole;

import java.util.List;


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

    @Column(name = "password")
    private String password;

    @Column(name = "roles")
    private List<String> roles;

    public boolean hasRole(UserRole userRole) {
        return this.getRoles().contains(userRole.name());
    }

    public void addRole(UserRole userRole) {
        this.getRoles().add(userRole.name());
    }
}
