package aem.example.springboot.graphqlbaseapp.infrastructure.config.security;

import org.springframework.security.core.AuthenticationException;

public class UserNotActivatedException extends AuthenticationException {
    public UserNotActivatedException(String message) {
        super(message);
    }
}
