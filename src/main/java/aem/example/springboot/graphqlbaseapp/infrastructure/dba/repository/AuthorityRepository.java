package aem.example.springboot.graphqlbaseapp.infrastructure.dba.repository;

import aem.example.springboot.graphqlbaseapp.infrastructure.dba.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
