package ru.savvy.soldo.tenant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.savvy.soldo.tenant.model.TenantConfig;

public interface TenantConfigRepository extends JpaRepository<TenantConfig, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE TenantConfig c SET c.telegramBotToken = :token, c.telegramBotUsername = null, " +
           "c.telegramWebhookSecret = CASE WHEN c.telegramWebhookSecret IS NULL THEN :secret ELSE c.telegramWebhookSecret END " +
           "WHERE c.tenantId = :tenantId")
    void updateTelegramBot(@Param("tenantId") Long tenantId,
                           @Param("token") String token,
                           @Param("secret") String secret);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE TenantConfig c SET c.telegramBotToken = null, c.telegramBotUsername = null WHERE c.tenantId = :tenantId")
    void clearTelegramBot(@Param("tenantId") Long tenantId);
}
