package aem.example.springboot.graphqlbaseapp.infrastructure.service;

import aem.example.springboot.graphqlbaseapp.infrastructure.dba.model.Authority;
import aem.example.springboot.graphqlbaseapp.infrastructure.dba.model.User;
import aem.example.springboot.graphqlbaseapp.infrastructure.dba.repository.AuthorityRepository;
import aem.example.springboot.graphqlbaseapp.infrastructure.dba.repository.UserRepository;
import aem.example.springboot.graphqlbaseapp.infrastructure.exception.UsernameOrEmailInUseException;
import aem.example.springboot.graphqlbaseapp.infrastructure.web.dto.UserInput;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final CacheManager cacheManager;

    public UserService(UserRepository userRepository, AuthorityRepository authorityRepository,
                       PasswordEncoder passwordEncoder, CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
        this.cacheManager = cacheManager;
    }

    public User createUser(UserInput input) throws UsernameOrEmailInUseException {
        if (userRepository.findOneByEmailIgnoreCase(input.getEmail()).isPresent())
            throw new UsernameOrEmailInUseException("email", input.getEmail());
        if (userRepository.findOneByUsername(input.getUsername()).isPresent())
            throw new UsernameOrEmailInUseException("username", input.getUsername());
        User user = new User();
        user.setUsername(input.getUsername());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setEmail(input.getEmail());
        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        Set<Authority> authorities = input.getRoles().stream()
                .map(authorityRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
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

    public User updateUser(UserInput input) throws UsernameOrEmailInUseException {
        if (input.getId() == null)
            throw new EntityNotFoundException("The user no exists");
        Optional<User> user = userRepository.findOneWithAuthoritiesByEmailIgnoreCase(input.getEmail());
        if (user.isPresent() && (!user.get().getId().equals(input.getId())))
            throw new UsernameOrEmailInUseException("email", input.getEmail());
        user = userRepository.findOneByUsername(input.getUsername());
        if (user.isPresent() && !user.get().getId().equals(input.getId()))
            throw new UsernameOrEmailInUseException("username", input.getUsername());
        User userExists = userRepository.findById(input.getId())
                .map(user1 -> {
                    this.clearUserCaches(user1);
                    user1.setActivated(input.isActivated());
                    if (input.getEmail() != null)
                        user1.setEmail(input.getEmail());
                    if (input.getFirstName() != null)
                        user1.setFirstName(input.getFirstName());
                    if (input.getLastName() != null)
                        user1.setLastName(input.getLastName());
                    if (input.getUsername() != null)
                        user1.setUsername(input.getUsername());
                    if (input.getPassword() != null)
                        user1.setPassword(passwordEncoder.encode(input.getPassword()));
                    if (input.getRoles() != null && !input.getRoles().isEmpty()) {
                        Set<Authority> authorities = input.getRoles().stream()
                                .distinct()
                                .map(authorityRepository::findById)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .collect(Collectors.toSet());
                        user1.setAuthorities(authorities);
                    }
                    this.clearUserCaches(user1);
                    return user1;
                })
                .orElseThrow(() -> new EntityNotFoundException("The user no exists"));
        return userRepository.save(userExists);
    }

    private void clearUserCaches(User user) {
        if (user.getUsername() != null)
            Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)).evict(user.getUsername());
        if (user.getEmail() != null)
            Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evict(user.getEmail());
    }
}
