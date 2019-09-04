package aem.example.springboot.graphqlbaseapp.infrastructure.web.resolver;

import aem.example.springboot.graphqlbaseapp.infrastructure.config.security.TokenProvider;
import aem.example.springboot.graphqlbaseapp.infrastructure.config.security.annotation.Public;
import aem.example.springboot.graphqlbaseapp.infrastructure.web.dto.Credentials;
import aem.example.springboot.graphqlbaseapp.infrastructure.web.dto.Payload;
import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthResolver implements GraphQLMutationResolver {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthResolver(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @Public
    public Payload login(Credentials credentials) {
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return new Payload(jwt);
    }
}
