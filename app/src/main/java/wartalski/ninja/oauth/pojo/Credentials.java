package wartalski.ninja.oauth.pojo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by kuba on 07.05.16.
 */
public class Credentials {

    private String authorization;
    private String password;
    private String userName;
    private String grantType;
    private String scope;
    private String clientSecret;
    private String clientId;

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        try {
            this.scope = URLEncoder.encode(scope, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            this.scope= "";
        }
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public static class Builder {

        private Credentials credentials;

        public Builder() {
            credentials = new Credentials();
            credentials.setAuthorization("bearer");
            credentials.setClientId("clientapp");
            credentials.setClientSecret("123456");
            credentials.setGrantType("password");
            credentials.setScope("read write");
        }

        public Builder setLogin(String login) {
            credentials.setUserName(login);
            return this;
        }

        public Builder setPassword(String password) {
            credentials.setPassword(password);
            return this;
        }

        public Builder setClientId(String clientId) {
            credentials.setClientId(clientId);
            return this;
        }

        public Builder setClientSercret(String clientSercret) {
            credentials.setClientSecret(clientSercret);
            return this;
        }

        public Credentials build() {
            return credentials;
        }
    }

}
