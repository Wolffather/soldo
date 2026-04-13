package ru.savvy.soldo.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;
import ru.savvy.soldo.user.model.AuthProviderType;

import java.time.LocalDateTime;

@Entity
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Table(
    name = "user_auth_providers",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_provider_user_id",
        columnNames = {"provider", "provider_user_id"}
    )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAuthProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProviderType provider;

    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
