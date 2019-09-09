package aem.example.springboot.graphqlbaseapp.infrastructure.web.resolver;

import aem.example.springboot.graphqlbaseapp.infrastructure.config.Constants;
import aem.example.springboot.graphqlbaseapp.infrastructure.config.security.annotation.Public;
import aem.example.springboot.graphqlbaseapp.infrastructure.dba.model.User;
import aem.example.springboot.graphqlbaseapp.infrastructure.exception.UsernameOrEmailInUseException;
import aem.example.springboot.graphqlbaseapp.infrastructure.service.UserService;
import aem.example.springboot.graphqlbaseapp.infrastructure.web.dto.UserInput;
import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@SuppressWarnings({"unused"})
public class UserResource implements GraphQLMutationResolver, GraphQLQueryResolver {

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @Public
    public User addUser(UserInput input) throws UsernameOrEmailInUseException {
        return userService.createUser(input);
    }

    @PreAuthorize("hasRole(\"" + Constants.ROLE_ADMIN + "\")")
    public List<User> list() {
        return userService.findAll();
    }

    @PreAuthorize("hasRole(\"" + Constants.ROLE_ADMIN + "\")")
    public User getUser(String username) {
        return userService.getUser(username);
    }

    @PreAuthorize("hasRole(\"" + Constants.ROLE_ADMIN + "\")")
    public User editUser(UserInput input) throws UsernameOrEmailInUseException {
        return userService.updateUser(input);
    }

    @PreAuthorize("hasRole(\"" + Constants.ROLE_ADMIN + "\")")
    public boolean deleteUser(Long id) {
        return userService.deleteUser(id);
    }
}
