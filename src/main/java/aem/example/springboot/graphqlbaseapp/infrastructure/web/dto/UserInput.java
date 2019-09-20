package aem.example.springboot.graphqlbaseapp.infrastructure.web.dto;

import java.util.HashSet;
import java.util.Set;

public class UserInput {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Set<String> roles = new HashSet<>();

    public Long getId() {
        return id;
    }

    public UserInput setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserInput setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserInput setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public UserInput setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public UserInput setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public UserInput setRoles(Set<String> roles) {
        this.roles = roles;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserInput userInput = (UserInput) o;

        if (id != null ? !id.equals(userInput.id) : userInput.id != null) return false;
        if (username != null ? !username.equals(userInput.username) : userInput.username != null) return false;
        if (email != null ? !email.equals(userInput.email) : userInput.email != null) return false;
        if (firstName != null ? !firstName.equals(userInput.firstName) : userInput.firstName != null) return false;
        if (lastName != null ? !lastName.equals(userInput.lastName) : userInput.lastName != null) return false;
        return roles != null ? roles.equals(userInput.roles) : userInput.roles == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (roles != null ? roles.hashCode() : 0);
        return result;
    }
}
