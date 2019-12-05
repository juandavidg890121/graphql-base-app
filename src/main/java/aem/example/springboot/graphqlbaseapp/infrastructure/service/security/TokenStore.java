package aem.example.springboot.graphqlbaseapp.infrastructure.service.security;

import java.util.Optional;

import aem.example.springboot.graphqlbaseapp.infrastructure.web.dto.Payload;

public interface TokenStore {

	void storeAccessToken(String username, String accessToken);
	void storeRefreshToken(String username, String refreshToken);
	void storeTokens(String username ,String accessToken, String refreshToken);	
	Optional<Payload> getUserPayload(String username);
	boolean existsRefreshToken(String username, String refreshToken);
}
