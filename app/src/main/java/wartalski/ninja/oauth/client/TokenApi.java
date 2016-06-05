package wartalski.ninja.oauth.client;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;
import wartalski.ninja.oauth.pojo.OAuth2;
import wartalski.ninja.oauth.pojo.User;

/**
 * Created by kuba on 07.05.16.
 */
public interface TokenApi {

    @POST("/oauth/token")
    @Headers("Accept: application/json")
    Observable<OAuth2> getOAuth2(@Header("Authorization") String authorization,
                                 @Query("password") String password,
                                 @Query("username") String username,
                                 @Query("grant_type") String grantType,
                                 @Query("scope") String scope,
                                 @Query("client_secret") String clientSecret,
                                 @Query("client_id") String clientId);

    @POST("/oauth/token")
    @Headers("Accept: application/json")
    Observable<OAuth2> refresh(@Header("Authorization") String authorization,
                                 @Query("grant_type") String grantType,
                                 @Query("refresh_token") String refreshToken);

    @GET("/api/user/me")
    @Headers("Accept: application/json")
    Observable<User> getUser();
}
