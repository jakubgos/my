package wartalski.ninja.oauth.client;


import android.content.Context;
import android.support.annotation.NonNull;

import rx.Observable;
import wartalski.ninja.oauth.pojo.Credentials;
import wartalski.ninja.oauth.pojo.OAuth2;
import wartalski.ninja.oauth.pojo.User;

/**
 * Created by kuba on 07.05.16.
 */
public class TokenClient extends Client<TokenApi> {

    public TokenClient(String path, Context context) {
        super(TokenApi.class, path, context);
    }

    public Observable<OAuth2> getToken(Credentials credentials) {
        return apiClient.getOAuth2(
                getAuthorization(credentials),
                credentials.getPassword(),
                credentials.getUserName(),
                credentials.getGrantType(),
                credentials.getScope(),
                credentials.getClientSecret(),
                credentials.getClientId()
        );
    }

    @NonNull
    private String getAuthorization(Credentials credentials) {
        return okhttp3.Credentials.basic(credentials.getClientId(), credentials.getClientSecret());
    }

    public Observable<User> getUser() {
        return apiClient.getUser();
    }
}
