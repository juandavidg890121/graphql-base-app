package aem.example.springboot.graphqlbaseapp.infrastructure.web.resolver;

import aem.example.springboot.graphqlbaseapp.infrastructure.config.security.annotation.Public;
import aem.example.springboot.graphqlbaseapp.infrastructure.web.dto.UserInput;
import aem.example.springboot.graphqlbaseapp.infrastructure.dba.model.Authority;
import aem.example.springboot.graphqlbaseapp.infrastructure.dba.model.User;
import aem.example.springboot.graphqlbaseapp.infrastructure.dba.repository.AuthorityRepository;
import aem.example.springboot.graphqlbaseapp.infrastructure.dba.repository.UserRepository;
import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserResource implements GraphQLMutationResolver, GraphQLQueryResolver {
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResource(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
    }

    @PreAuthorize("hasRole(\"ROLE_ADMIN\")")
    public User addUser(UserInput input) {
        User user = new User();
        user.setUsername(input.getUsername());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setEmail(input.getEmail());
        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        Set<Authority> authorities = input.getRoles().stream().map(authorityRepository::findById).map(Optional::get).collect(Collectors.toSet());
        user.setAuthorities(authorities);
        return userRepository.save(user);
    }

    @PreAuthorize("hasRole(\"ROLE_ADMIN\")")
    public List<User> list() {
        return userRepository.findAll();
    }

    @Public
    public User getUser(String username) {
        return userRepository.findOneWithAuthoritiesByUsername(username).orElse(null);
    }
}
