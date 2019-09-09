package aem.example.springboot.graphqlbaseapp.infrastructure.config;

public final class Constants {
    public static final String USERNAME_REGEXP = "^[_.a-zA-Z0-9]*$";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String USERS_BY_LOGIN_CACHE = "usersByLogin";
    public static final String USERS_BY_EMAIL_CACHE = "usersByEmail";

    private Constants() {
    }
}
