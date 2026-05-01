package ru.savvy.soldo.tenant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.savvy.soldo.tenant.model.TenantConfig;

public interface TenantConfigRepository extends JpaRepository<TenantConfig, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE tenant_configs SET telegram_bot_token = :token, telegram_bot_username = NULL, " +
                   "telegram_webhook_secret = COALESCE(telegram_webhook_secret, :secret) " +
                   "WHERE tenant_id = :tenantId",
           nativeQuery = true)
    void updateTelegramBot(@Param("tenantId") Long tenantId,
                           @Param("token") String token,
                           @Param("secret") String secret);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE tenant_configs SET telegram_bot_token = NULL, telegram_bot_username = NULL " +
                   "WHERE tenant_id = :tenantId",
           nativeQuery = true)
    void clearTelegramBot(@Param("tenantId") Long tenantId);
}
