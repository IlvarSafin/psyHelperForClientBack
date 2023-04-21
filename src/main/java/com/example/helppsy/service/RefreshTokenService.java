package com.example.helppsy.service;

import com.example.helppsy.entity.RefreshToken;
import com.example.helppsy.repository.ClientRepository;
import com.example.helppsy.repository.RefreshTokenRepository;
import com.example.helppsy.security.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Transactional
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken createRefreshToken(int clientId) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setClient(clientRepository.findById(clientId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(SecurityConstants.JWT_REFRESH_EXPIRATION_MS));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Transactional
    public int deleteByUserId(int clientId) {
        return refreshTokenRepository.deleteByClient(clientRepository.findById(clientId).get());
    }
}
