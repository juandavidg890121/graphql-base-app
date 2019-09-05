package aem.example.springboot.graphqlbaseapp.infrastructure.exception;

public class UsernameOrEmailInUseException extends Exception {

    public UsernameOrEmailInUseException(String key, String value) {
        super(String.format("The [%s] with value [%s] already in use", key, value));
    }
}
