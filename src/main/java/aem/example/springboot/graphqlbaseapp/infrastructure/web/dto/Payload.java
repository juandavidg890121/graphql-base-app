package aem.example.springboot.graphqlbaseapp.infrastructure.web.dto;


public class Payload {
    private String accessToken;
    private String refreshToken;

    public Payload(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
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

	public String getRefreshToken() {
		return refreshToken;
	}

	public Payload setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
		return this;
	}    
}
