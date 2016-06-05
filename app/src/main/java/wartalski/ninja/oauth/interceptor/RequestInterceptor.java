package wartalski.ninja.oauth.interceptor;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import wartalski.ninja.oauth.client.TokenApi;
import wartalski.ninja.oauth.pojo.OAuth2;
import wartalski.ninja.oauth.provider.LocalStorageProvider;

/**
 * Created by kuba on 07.05.16.
 */
public class RequestInterceptor implements Interceptor {

    private TokenApi tokenClient;
    private String url;
    private ClientData clientData;
    private LocalStorageProvider tokenProvider;

    public RequestInterceptor(String url, LocalStorageProvider tokenProvider, ClientData clientData) {
        this.tokenProvider = tokenProvider;
        this.url = url;
        this.clientData = clientData;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if(isTokenRequest(request)) {
            Log.d("...", request.toString());
            Log.d("...", request.headers().toString());
            return chain.proceed(request);
        }

        request = request.newBuilder()
                .header("Authorization", "Bearer " + tokenProvider.getAccessToken())
                .build();

        Response response = chain.proceed(request);

        if(response.code() == 401) {
            if(tokenClient == null) {
                createTokenClient();
            }
            OAuth2 tokens = tokenClient.refresh(
                    getAuthorization(clientData.getClientId(), clientData.getClientSecret()),
                    "refresh_token",
                    tokenProvider.getRefreshToken())
                    .toBlocking()
                    .first();

            tokenProvider.setOAuth2(tokens);

            if(!StringUtils.isEmpty(tokens.getAccessToken())) {
                Request newRequest = request.newBuilder()
                        .removeHeader("Authorization")
                        .addHeader("Authorization", "Bearer " + tokenProvider.getAccessToken())
                        .build();

                return chain.proceed(newRequest);
            }
        }
        return response;
    }

    private boolean isTokenRequest(Request request) {
        return !StringUtils.isEmpty(request.url().encodedQuery()) && request.url().encodedQuery().contains("grant_type=password");
    }

    private void createTokenClient() {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Retrofit restAdapter = new Retrofit.Builder()
                .client(client)
                .baseUrl(url)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        tokenClient = restAdapter.create(TokenApi.class);
    }

    private String getAuthorization(String user, String password) {
        return "Authorization: " + okhttp3.Credentials.basic(user, password);
    }

}
