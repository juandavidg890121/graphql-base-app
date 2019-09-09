package aem.example.springboot.graphqlbaseapp.infrastructure.service;

import aem.example.springboot.graphqlbaseapp.infrastructure.dba.model.User;
import aem.example.springboot.graphqlbaseapp.infrastructure.dba.repository.UserRepository;
import aem.example.springboot.graphqlbaseapp.infrastructure.exception.UsernameOrEmailInUseException;
import aem.example.springboot.graphqlbaseapp.infrastructure.service.mapper.UserMapper;
import aem.example.springboot.graphqlbaseapp.infrastructure.web.dto.UserInput;
import org.springframework.cache.CacheManager;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CacheManager cacheManager;
    private final MessageSource messageSource;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, CacheManager cacheManager, MessageSource messageSource, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.cacheManager = cacheManager;
        this.messageSource = messageSource;
        this.userMapper = userMapper;
    }

    public User createUser(UserInput input) throws UsernameOrEmailInUseException {
        if (userRepository.findOneByEmailIgnoreCase(input.getEmail()).isPresent())
            throw new UsernameOrEmailInUseException("email", input.getEmail());
        if (userRepository.findOneByUsername(input.getUsername()).isPresent())
            throw new UsernameOrEmailInUseException("username", input.getUsername());
        User user = userMapper.toEntity(input);
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
            throw new EntityNotFoundException(messageSource.getMessage("user.no_exists", null, LocaleContextHolder.getLocale()));
        Optional<User> user = userRepository.findOneWithAuthoritiesByEmailIgnoreCase(input.getEmail());
        if (user.isPresent() && (!user.get().getId().equals(input.getId())))
            throw new UsernameOrEmailInUseException("email", input.getEmail());
        user = userRepository.findOneByUsername(input.getUsername());
        if (user.isPresent() && !user.get().getId().equals(input.getId()))
            throw new UsernameOrEmailInUseException("username", input.getUsername());
        User userExists = userRepository.findById(input.getId())
                .map(user1 -> {
                    this.clearUserCaches(user1);
                    user1 = userMapper.toEntity(input);
                    this.clearUserCaches(user1);
                    return user1;
                })
                .orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("user.no_exists", null, LocaleContextHolder.getLocale())));
        return userRepository.save(userExists);
    }

    private void clearUserCaches(User user) {
        if (user.getUsername() != null)
            Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)).evict(user.getUsername());
        if (user.getEmail() != null)
            Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evict(user.getEmail());
    }

    public boolean deleteUser(Long id) {
        try {
            userRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return true;
        }
    }
}
