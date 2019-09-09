package aem.example.springboot.graphqlbaseapp.infrastructure.web.resolver;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings({"unused"})
public class RootQuery implements GraphQLQueryResolver {

    @PreAuthorize("hasRole(\"ROLE_USER\")")
    public String hello() {
        return RootQuery.class.getName();
    }
}
