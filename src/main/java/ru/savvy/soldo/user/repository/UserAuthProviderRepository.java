package ru.savvy.soldo.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.savvy.soldo.user.model.UserAuthProvider;
import ru.savvy.soldo.user.model.AuthProviderType;

import java.util.List;
import java.util.Optional;

public interface UserAuthProviderRepository extends JpaRepository<UserAuthProvider, Long> {

    Optional<UserAuthProvider> findByProviderAndProviderUserId(AuthProviderType provider, String providerUserId);

    List<UserAuthProvider> findByUserId(Long userId);
}
