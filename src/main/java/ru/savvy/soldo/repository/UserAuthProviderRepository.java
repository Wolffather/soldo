package ru.savvy.soldo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.savvy.soldo.model.UserAuthProvider;
import ru.savvy.soldo.model.enums.AuthProviderType;

import java.util.List;
import java.util.Optional;

public interface UserAuthProviderRepository extends JpaRepository<UserAuthProvider, Long> {

    Optional<UserAuthProvider> findByProviderAndProviderUserId(AuthProviderType provider, String providerUserId);

    List<UserAuthProvider> findByUserId(Long userId);
}
