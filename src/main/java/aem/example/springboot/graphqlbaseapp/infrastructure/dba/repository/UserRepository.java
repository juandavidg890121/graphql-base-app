package aem.example.springboot.graphqlbaseapp.infrastructure.dba.repository;

import aem.example.springboot.graphqlbaseapp.infrastructure.dba.model.User;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static aem.example.springboot.graphqlbaseapp.infrastructure.config.Constants.*;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findOneByEmailIgnoreCase(String email);

    Optional<User> findOneByUsername(String username);

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesById(Long id);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = USERS_BY_LOGIN_CACHE)
    Optional<User> findOneWithAuthoritiesByUsername(String username);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = USERS_BY_EMAIL_CACHE)
    Optional<User> findOneWithAuthoritiesByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "authorities", type = EntityGraph.EntityGraphType.LOAD)
    @Cacheable(value = USERS_BY_LOGIN_CACHE, key = "#username")
    Optional<User> findOneWithAuthoritiesByUsernameAndRegisterKey(String username, String registerKey);
    
    @EntityGraph(attributePaths = "authorities", type = EntityGraph.EntityGraphType.LOAD)
    List<User> findAll();
}
