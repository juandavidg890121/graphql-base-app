package aem.example.springboot.graphqlbaseapp.infrastructure.service.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import aem.example.springboot.graphqlbaseapp.infrastructure.config.security.TokenProvider;
import aem.example.springboot.graphqlbaseapp.infrastructure.dba.model.Authority;
import aem.example.springboot.graphqlbaseapp.infrastructure.dba.model.User;
import aem.example.springboot.graphqlbaseapp.infrastructure.service.UserService;
import aem.example.springboot.graphqlbaseapp.infrastructure.web.dto.Payload;

@Service
public class AuthService {
	private final TokenProvider tokenProvider;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final TokenStore tokenStore;
	private final UserService userService;

	public AuthService(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder,
			TokenStore tokenStore, UserService userService) {
		this.tokenProvider = tokenProvider;
		this.authenticationManagerBuilder = authenticationManagerBuilder;
		this.tokenStore = tokenStore;
		this.userService = userService;
	}

	/**
	 * Authenticate a user
	 * 
	 * @param username User identifier
	 * @param password User password
	 * @return {@link Payload} with generated credentials
	 */
	public Payload authenticateUser(String username, String password) {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
				password);
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String[] roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.map(String::toUpperCase).distinct().toArray(String[]::new);
		return this.generateUserCredentials(authentication.getName(), roles);

	}

	/**
	 * Refresh user credentials.
	 * 
	 * @param refreshToken The provided refreshToken
	 * @return User {@link Payload}
	 * @throws BadCredentialsException if any problem with token
	 */
	public Payload refreshToken(String refreshToken) {
		if (tokenProvider.validateRefreshToken(refreshToken)) {
			String username = tokenProvider.getSubject(refreshToken);
			if (tokenStore.existsRefreshToken(username, refreshToken)) {
				User user = userService.getUser(username);
				if (user != null) {
					String[] roles = user.getAuthorities().stream().map(Authority::getName).toArray(String[]::new);
					Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), null,
							AuthorityUtils.createAuthorityList(roles));
					SecurityContextHolder.getContext().setAuthentication(authentication);
					return this.generateUserCredentials(username, roles);
				}
			}
		}
		throw new BadCredentialsException("No valid refresh token");
	}

	/**
	 * Generate and store credentials on {@link TokenStore}
	 * 
	 * @param username User identity
	 * @param roles    User roles
	 * @return {@link Payload} with user credentials
	 */
	public Payload generateUserCredentials(String username, String[] roles) {
		String accessToken = tokenProvider.generateToken(username, roles);
		String refreshToken = tokenProvider.generateRefreshToken(username);
		tokenStore.storeTokens(username, accessToken, refreshToken);
		return new Payload(accessToken, refreshToken);
	}
}
