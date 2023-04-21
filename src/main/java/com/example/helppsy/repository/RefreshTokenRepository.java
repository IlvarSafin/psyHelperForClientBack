package com.example.helppsy.repository;

import java.util.Optional;

import com.example.helppsy.entity.Client;
import com.example.helppsy.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByClient(Client client);

    @Modifying
    int deleteByClient(Client client);
}
