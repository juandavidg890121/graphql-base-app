package aem.example.springboot.graphqlbaseapp.infrastructure.web.dto;


public class Payload {
    private String accessToken;

    public Payload(String accessToken) {
        this.accessToken = accessToken;
    }

    public Payload() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Payload setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }
}
