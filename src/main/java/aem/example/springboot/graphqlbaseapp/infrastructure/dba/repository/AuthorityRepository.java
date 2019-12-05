package aem.example.springboot.graphqlbaseapp.infrastructure.dba.repository;

import aem.example.springboot.graphqlbaseapp.infrastructure.dba.model.Authority;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import static aem.example.springboot.graphqlbaseapp.infrastructure.config.Constants.AUTHORITIES_CACHE;

public interface AuthorityRepository extends JpaRepository<Authority, String> {

    @Cacheable(cacheNames = AUTHORITIES_CACHE)
    List<Authority> findAllById(Iterable<String> ids);
}
