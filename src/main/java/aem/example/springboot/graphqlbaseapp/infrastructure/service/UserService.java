package aem.example.springboot.graphqlbaseapp.infrastructure.service;

import aem.example.springboot.graphqlbaseapp.infrastructure.dba.model.Authority;
import aem.example.springboot.graphqlbaseapp.infrastructure.dba.model.User;
import aem.example.springboot.graphqlbaseapp.infrastructure.dba.repository.AuthorityRepository;
import aem.example.springboot.graphqlbaseapp.infrastructure.dba.repository.UserRepository;
import aem.example.springboot.graphqlbaseapp.infrastructure.web.dto.UserInput;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, AuthorityRepository authorityRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(UserInput input) {
        User user = new User();
        user.setUsername(input.getUsername());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setEmail(input.getEmail());
        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        Set<Authority> authorities = input.getRoles().stream().map(authorityRepository::findById).map(Optional::get)
                .collect(Collectors.toSet());
        user.setAuthorities(authorities);
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User getUser(String username) {
        return userRepository.findOneWithAuthoritiesByUsername(username).orElse(null);
    }

}
