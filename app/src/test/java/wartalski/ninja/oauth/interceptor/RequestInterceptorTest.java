package wartalski.ninja.oauth.interceptor;

import android.content.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import wartalski.ninja.oauth.BuildConfig;
import wartalski.ninja.oauth.pojo.OAuth2;
import wartalski.ninja.oauth.provider.LocalStorageProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


/**
 * Created by kuba on 07.05.16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 19)
public class RequestInterceptorTest {

    private MockWebServer mockWebServer;
    private HttpUrl url;
    private RequestInterceptor requestInterceptor;
    private ObjectMapper objectMapper;
    private LocalStorageProvider tokenProvider;

    public RequestInterceptorTest() throws IOException {
        objectMapper = new ObjectMapper();
    }

    @Before
    public void setup() {
        mockWebServer = new MockWebServer();
        url = mockWebServer.url("");
        Context context = RuntimeEnvironment.application.getApplicationContext();
        tokenProvider = new LocalStorageProvider(context);
        requestInterceptor = new RequestInterceptor(url.toString(), tokenProvider, ClientDataFactory.createClientData(context));
    }

    @Test
    public void shouldRefreshToken() throws IOException, InterruptedException {
        final String badToken = "123";
        final String correctToken = "456";

        tokenProvider.setAccessToken(badToken);

        mockWebServer.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                if (request.getHeader("Authorization").equalsIgnoreCase("Bearer " + badToken)) {
                    return new MockResponse().setResponseCode(401);
                } else if (request.getPath().contains("refresh_token")) {
                    OAuth2 oAuth2 = new OAuth2();
                    oAuth2.setAccessToken(correctToken);
                    try {
                        return new MockResponse().setBody(objectMapper.writeValueAsString(oAuth2));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                } else {
                    return new MockResponse();
                }

                return new MockResponse();
            }
        });

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + badToken)
                .build();

        Response response = new OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .build()
                .newCall(request)
                .execute();

        mockWebServer.takeRequest();

        assertThat(response.isSuccessful(), equalTo(true));
    }

    @Test
    public void shouldAddAuthorizationHeader() throws IOException, InterruptedException {
        final String accessToken = "123123123";
        tokenProvider.setAccessToken(accessToken);

        mockWebServer.enqueue(new MockResponse());
        Request request = new Request.Builder()
                .url(url)
                .build();

        new OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .build()
                .newCall(request)
                .execute();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();

        assertThat(recordedRequest.getHeader("Authorization"), equalTo("Bearer " + accessToken));
    }

    @Test
    public void shouldNotPutAuthorizationIfItExists() throws IOException, InterruptedException {
        final String accessToken = "123123123";
        tokenProvider.setAccessToken(accessToken);

        mockWebServer.enqueue(new MockResponse());
        Request request = new Request.Builder()
                .url(url + "/oauth/token?password=test&username=john@doe.com&grant_type=password&scope=read%20write&client_secret=123456&client_id=clientapp")
                .addHeader("Authorization", "Basic test")
                .build();

        new OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .build()
                .newCall(request)
                .execute();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();

        assertThat(recordedRequest.getHeader("Authorization"), equalTo("Basic test"));
    }
}
