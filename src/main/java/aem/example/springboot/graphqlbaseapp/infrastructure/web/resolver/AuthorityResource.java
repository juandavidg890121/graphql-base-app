package aem.example.springboot.graphqlbaseapp.infrastructure.web.resolver;

import aem.example.springboot.graphqlbaseapp.infrastructure.config.Constants;
import aem.example.springboot.graphqlbaseapp.infrastructure.dba.model.Authority;
import aem.example.springboot.graphqlbaseapp.infrastructure.dba.repository.AuthorityRepository;
import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.persistence.EntityExistsException;
import java.util.List;

@Component
public class AuthorityResource implements GraphQLQueryResolver, GraphQLMutationResolver {
    private final AuthorityRepository authorityRepository;
    private final MessageSource messageSource;

    public AuthorityResource(AuthorityRepository authorityRepository, MessageSource messageSource) {
        this.authorityRepository = authorityRepository;
        this.messageSource = messageSource;
    }

    @PreAuthorize("hasRole(\"" + Constants.ROLE_ADMIN + "\")")
    public List<Authority> authorities() {
        return authorityRepository.findAll();
    }

    @PreAuthorize("hasRole(\"" + Constants.ROLE_ADMIN + "\")")
    public Authority addAuthority(String name) {
        if (authorityRepository.findById(name).isPresent())
            throw new EntityExistsException(messageSource
                    .getMessage("entity.exists", new String[]{name}, LocaleContextHolder.getLocale()));
        return authorityRepository.save(new Authority(name));

    }
}
