package aem.example.springboot.graphqlbaseapp.infrastructure.service.mapper;

import aem.example.springboot.graphqlbaseapp.infrastructure.dba.model.Authority;
import aem.example.springboot.graphqlbaseapp.infrastructure.dba.model.User;
import aem.example.springboot.graphqlbaseapp.infrastructure.web.dto.UserInput;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserMapper implements EntityMapper<UserInput, User> {

    public UserMapper() {
    }

    @Override
    public User toEntity(UserInput input) {
        User user1 = new User();
        if (input.getEmail() != null)
            user1.setEmail(input.getEmail());
        if (input.getFirstName() != null)
            user1.setFirstName(input.getFirstName());
        if (input.getLastName() != null)
            user1.setLastName(input.getLastName());
        if (input.getUsername() != null)
            user1.setUsername(input.getUsername());
        if (input.getRoles() != null && !input.getRoles().isEmpty()) {
            Set<Authority> authorities = input.getRoles().stream()
                    .distinct()
                    .map(Authority::new)
                    .collect(Collectors.toSet());
            user1.setAuthorities(authorities);
        }
        return user1;
    }

    @Override
    public UserInput toDto(User entity) {
        UserInput userInput = new UserInput();
        userInput.setEmail(entity.getEmail());
        userInput.setFirstName(entity.getFirstName());
        userInput.setLastName(entity.getLastName());
        userInput.setId(entity.getId());
        userInput.setUsername(entity.getUsername());
        userInput.setRoles(entity.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet()));
        return userInput;
    }

    @Override
    public List<User> toEntity(List<UserInput> dtoList) {
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }

    @Override
    public List<UserInput> toDto(List<User> entityList) {
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }
}
