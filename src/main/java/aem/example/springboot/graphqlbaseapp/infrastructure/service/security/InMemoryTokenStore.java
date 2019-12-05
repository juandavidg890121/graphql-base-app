package aem.example.springboot.graphqlbaseapp.infrastructure.service.security;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import aem.example.springboot.graphqlbaseapp.infrastructure.web.dto.Payload;

public class InMemoryTokenStore implements TokenStore {
	private final ConcurrentHashMap<String, String> accessTokens = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, String> refreshTokens = new ConcurrentHashMap<>();

	@Override
	public void storeAccessToken(String username, String accessToken) {
		accessTokens.put(username, accessToken);
	}

	@Override
	public void storeRefreshToken(String username, String refreshToken) {
		refreshTokens.put(username, refreshToken);
	}

	@Override
	public void storeTokens(String username, String accessToken, String refreshToken) {
		storeAccessToken(username, accessToken);
		storeRefreshToken(username, refreshToken);
	}

	@Override
	public Optional<Payload> getUserPayload(String username) {
		if (accessTokens.containsKey(username) && refreshTokens.containsKey(username))
			return Optional.of(new Payload(accessTokens.get(username), refreshTokens.get(username)));
		return Optional.empty();
	}

	@Override
	public boolean existsRefreshToken(String username, String refreshToken) {
		return refreshTokens.containsKey(username) && refreshTokens.get(username).equalsIgnoreCase(refreshToken);
	}

}
