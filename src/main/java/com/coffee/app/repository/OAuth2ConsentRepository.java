package com.coffee.app.repository;

import com.coffee.app.domain.OAuth2Consent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OAuth2ConsentRepository extends JpaRepository<OAuth2Consent, String> {
    Optional<OAuth2Consent> findByUserUuidAndClientId(String userUuid, String clientId);
    
    List<OAuth2Consent> findByUserUuid(String userUuid);
    
    List<OAuth2Consent> findByClientId(String clientId);
}

