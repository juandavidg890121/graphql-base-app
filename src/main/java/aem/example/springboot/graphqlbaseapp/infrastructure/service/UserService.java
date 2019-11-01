package aem.example.springboot.graphqlbaseapp.infrastructure.service;

import aem.example.springboot.graphqlbaseapp.infrastructure.dba.model.Authority;
import aem.example.springboot.graphqlbaseapp.infrastructure.dba.model.User;
import aem.example.springboot.graphqlbaseapp.infrastructure.dba.repository.AuthorityRepository;
import aem.example.springboot.graphqlbaseapp.infrastructure.dba.repository.UserRepository;
import aem.example.springboot.graphqlbaseapp.infrastructure.exception.UserNotActivatedException;
import aem.example.springboot.graphqlbaseapp.infrastructure.exception.UsernameOrEmailInUseException;
import aem.example.springboot.graphqlbaseapp.infrastructure.service.mapper.UserMapper;
import aem.example.springboot.graphqlbaseapp.infrastructure.service.util.RandomUtil;
import aem.example.springboot.graphqlbaseapp.infrastructure.web.dto.ActivateUserInput;
import aem.example.springboot.graphqlbaseapp.infrastructure.web.dto.UserInput;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.*;

import static aem.example.springboot.graphqlbaseapp.infrastructure.config.Constants.*;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final CacheManager cacheManager;
    private final MessageSource messageSource;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;

    public UserService(UserRepository userRepository, CacheManager cacheManager, MessageSource messageSource,
                       UserMapper userMapper, PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.cacheManager = cacheManager;
        this.messageSource = messageSource;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
    }

    @Caching(put = {
            @CachePut(value = USERS_BY_LOGIN_CACHE, key = "#input.username"),
            @CachePut(value = USERS_BY_EMAIL_CACHE, key = "#input.email")
    })
    public User createUser(UserInput input) throws UsernameOrEmailInUseException {
        if (userRepository.findOneByEmailIgnoreCase(input.getEmail()).isPresent())
            throw new UsernameOrEmailInUseException("email", input.getEmail());
        if (userRepository.findOneByUsername(input.getUsername()).isPresent())
            throw new UsernameOrEmailInUseException("username", input.getUsername());
        User user = userMapper.toEntity(input);
        Set<Authority> authorities = new HashSet<>(authorityRepository.findAllById(input.getRoles()));
        user.setAuthorities(authorities);
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setRegisterKey(RandomUtil.generateActivationKey());
        // todo implement send email to user
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = ALL_USERS_WITH_AUTHORITIES_CACHE)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUser(String username) {
        return userRepository.findOneWithAuthoritiesByUsername(username).orElse(null);
    }

    @Caching(evict = {
            @CacheEvict(value = USERS_BY_LOGIN_CACHE, key = "#input.username"),
            @CacheEvict(value = USERS_BY_EMAIL_CACHE, key = "#input.email"),
            @CacheEvict(value = ALL_USERS_WITH_AUTHORITIES_CACHE, allEntries = true)
    })
    public User updateUser(UserInput input) throws UsernameOrEmailInUseException {
        if (input.getId() == null)
            throw new EntityNotFoundException(messageSource.getMessage("user.no_exists", null, LocaleContextHolder.getLocale()));
        Optional<User> user = userRepository.findOneWithAuthoritiesByEmailIgnoreCase(input.getEmail());
        if (user.isPresent() && (!user.get().getId().equals(input.getId())))
            throw new UsernameOrEmailInUseException("email", input.getEmail());
        user = userRepository.findOneByUsername(input.getUsername());
        if (user.isPresent() && !user.get().getId().equals(input.getId()))
            throw new UsernameOrEmailInUseException("username", input.getUsername());
        User userExists = userRepository.findById(input.getId())
                .map(user1 -> userMapper.toEntity(input))
                .orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("user.no_exists", null, LocaleContextHolder.getLocale())));
        return userRepository.save(userExists);
    }

    private void clearUserCaches(User user) {
        if (user.getUsername() != null)
            Objects.requireNonNull(cacheManager.getCache(USERS_BY_LOGIN_CACHE)).evict(user.getUsername());
        if (user.getEmail() != null)
            Objects.requireNonNull(cacheManager.getCache(USERS_BY_EMAIL_CACHE)).evict(user.getEmail());
        Objects.requireNonNull(cacheManager.getCache(ALL_USERS_WITH_AUTHORITIES_CACHE)).clear();
    }

    public boolean deleteUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            userRepository.delete(user);
            this.clearUserCaches(user);
        });
        return true;
    }

    public User activateUser(ActivateUserInput input) {
        Optional<User> existingUser = userRepository.findOneWithAuthoritiesByUsernameAndRegisterKey(input.getUsername(), input.getActivationKey());
        return existingUser
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(input.getPassword()));
                    user.setRegisterKey(null);
                    user.setActivated(true);
                    userRepository.save(user);
                    this.clearUserCaches(user);
                    return user;
                })
                .orElseThrow(() -> new UserNotActivatedException(messageSource.getMessage("user.no_activated", new String[]{input.getUsername()}, LocaleContextHolder.getLocale())));
    }
}
