package ru.savvy.soldo.tenant.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "tenant_subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PlanType plan = PlanType.FREE;

    @Column(name = "max_events")
    private Integer maxEvents;

    @Column(name = "max_bookings_per_month")
    private Integer maxBookingsPerMonth;

    @Column(name = "max_admin_users", nullable = false)
    @Builder.Default
    private Integer maxAdminUsers = 1;

    @Column(name = "custom_domain_enabled", nullable = false)
    @Builder.Default
    private Boolean customDomainEnabled = false;

    @Column(name = "api_access_enabled", nullable = false)
    @Builder.Default
    private Boolean apiAccessEnabled = false;

    @Column(name = "period_start")
    private Instant periodStart;

    @Column(name = "period_end")
    private Instant periodEnd;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    public boolean isApiAccessEnabled() {
        return apiAccessEnabled;
    }

    public boolean isCustomDomainEnabled() {
        return customDomainEnabled;
    }
}
