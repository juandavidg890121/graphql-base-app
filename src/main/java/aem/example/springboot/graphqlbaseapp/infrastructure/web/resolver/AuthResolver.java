package aem.example.springboot.graphqlbaseapp.infrastructure.web.resolver;

import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;

import aem.example.springboot.graphqlbaseapp.infrastructure.config.security.annotation.Public;
import aem.example.springboot.graphqlbaseapp.infrastructure.service.security.AuthService;
import aem.example.springboot.graphqlbaseapp.infrastructure.web.dto.Credentials;
import aem.example.springboot.graphqlbaseapp.infrastructure.web.dto.Payload;

@Component
@SuppressWarnings({ "unused" })
public class AuthResolver implements GraphQLMutationResolver {

	private final AuthService authService;

	public AuthResolver(AuthService authService) {
		this.authService = authService;
	}

	@Public
	public Payload login(Credentials credentials) {
		return authService.authenticateUser(credentials.getUsername(), credentials.getPassword());
	}

	@Public
	public Payload refreshToken(String refreshToken) {
		return authService.refreshToken(refreshToken);
	}
}
