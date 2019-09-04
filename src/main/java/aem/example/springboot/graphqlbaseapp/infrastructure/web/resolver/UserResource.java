package aem.example.springboot.graphqlbaseapp.infrastructure.web.resolver;

import aem.example.springboot.graphqlbaseapp.infrastructure.config.security.annotation.Public;
import aem.example.springboot.graphqlbaseapp.infrastructure.dba.model.User;
import aem.example.springboot.graphqlbaseapp.infrastructure.service.UserService;
import aem.example.springboot.graphqlbaseapp.infrastructure.web.dto.UserInput;
import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserResource implements GraphQLMutationResolver, GraphQLQueryResolver {

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole(\"ROLE_ADMIN\")")
    public User addUser(UserInput input) {
        return userService.createUser(input);
    }

    @PreAuthorize("hasRole(\"ROLE_ADMIN\")")
    public List<User> list() {
        return userService.findAll();
    }

    @Public
    public User getUser(String username) {
        return userService.getUser(username);
    }
}
