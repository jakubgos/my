package wartalski.ninja.oauth.client;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import wartalski.ninja.oauth.BuildConfig;
import wartalski.ninja.oauth.pojo.Credentials;
import wartalski.ninja.oauth.pojo.OAuth2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 19)
public class TokenClientTest {

    private MockWebServer mockWebServer;
    private ObjectMapper mapper;
    private TokenClient restClient;

    public TokenClientTest() throws IOException {
        mockWebServer = new MockWebServer();
        final HttpUrl url = mockWebServer.url("");
        Context context = RuntimeEnvironment.application.getApplicationContext();
        restClient = new TokenClient(url.toString(), context);
        mapper = new ObjectMapper();
    }

    @Test
    public void shouldPassDataToServer() throws IOException, InterruptedException {
        MockResponse mockResponse = new MockResponse();
        mockResponse.setBody("{}");
        mockWebServer.enqueue(mockResponse);

        Credentials credentials = new Credentials();
        credentials.setUserName("test@test.pl");
        credentials.setPassword("password");
        credentials.setGrantType("password");
        credentials.setClientSecret("123456");
        credentials.setClientId("clientapp");
        credentials.setScope("read write");

        restClient.getToken(credentials)
                .toBlocking()
                .first();

        RecordedRequest request = mockWebServer.takeRequest();

        assertThat(request.getPath(), containsString("/oauth/token"));
        assertThat(request.getPath(), containsString("password=password"));
        assertThat(request.getPath(), containsString("username=test@test.pl"));
        assertThat(request.getPath(), containsString("grant_type=password"));
        assertThat(request.getPath(), containsString("scope=read%2Bwrite"));
        assertThat(request.getPath(), containsString("client_secret=123456"));
        assertThat(request.getPath(), containsString("client_id=clientapp"));
    }

    @Test
    public void shouldGetCredentials() throws IOException, InterruptedException {
        MockResponse mockResponse = new MockResponse();

        final OAuth2 oAuth2 = new OAuth2();
        oAuth2.setScope("read write");
        oAuth2.setAccessToken("123456");
        oAuth2.setRefreshToken("654321");
        oAuth2.setExpiresIn(123);
        oAuth2.setTokenType("bearer");

        mockResponse.setBody(mapper.writeValueAsString(oAuth2));

        mockWebServer.enqueue(mockResponse);

        Credentials credentials = new Credentials();
        credentials.setUserName("test@test.pl");
        credentials.setPassword("password");
        credentials.setGrantType("password");
        credentials.setClientSecret("123456");
        credentials.setClientId("clientapp");
        credentials.setScope("read write");

        OAuth2 received = restClient.getToken(credentials)
                .toBlocking()
                .first();

        mockWebServer.takeRequest();

        assertThat(received.getAccessToken(), equalTo(oAuth2.getAccessToken()));
        assertThat(received.getRefreshToken(), equalTo(oAuth2.getRefreshToken()));
        assertThat(received.getScope(), equalTo(oAuth2.getScope()));
        assertThat(received.getExpiresIn(), equalTo(oAuth2.getExpiresIn()));
        assertThat(received.getTokenType(), equalTo(oAuth2.getTokenType()));
    }

}