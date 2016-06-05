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
import java.util.Date;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import wartalski.ninja.oauth.BuildConfig;
import wartalski.ninja.oauth.vo.CoordinatesVO;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by kuba on 19.05.16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 19)
public class TrackerClientTest {
    private MockWebServer mockWebServer;
    private ObjectMapper objectMapper;
    private TrackerClient trackerClient;

    public TrackerClientTest() throws IOException {
        mockWebServer = new MockWebServer();
        final HttpUrl url = mockWebServer.url("");
        Context context = RuntimeEnvironment.application.getApplicationContext();
        trackerClient = new TrackerClient(url.toString(), context);
        objectMapper = new ObjectMapper();
    }

    @Test
    public void shouldPostData() throws InterruptedException, IOException {
        MockResponse mockResponse = new MockResponse();
        mockResponse.setBody("{}");
        mockWebServer.enqueue(mockResponse);

        final CoordinatesVO coordinatesVO = new CoordinatesVO();
        coordinatesVO.setCreationDate(new Date());
        coordinatesVO.setLat(12.0);
        coordinatesVO.setLng(24.0);

        trackerClient.postData(coordinatesVO, 1L).toBlocking().first();

        final RecordedRequest request = mockWebServer.takeRequest();
        final CoordinatesVO capturedJson = objectMapper.readValue(request.getBody().readUtf8(), CoordinatesVO.class);

        assertThat(request.getPath(), containsString("/coordinates/1"));
        assertThat(dateToTheSecond(capturedJson), equalTo(dateToTheSecond(coordinatesVO)));
        assertThat(capturedJson.getLat(), equalTo(coordinatesVO.getLat()));
        assertThat(capturedJson.getLng(), equalTo(coordinatesVO.getLng()));
    }

    private long dateToTheSecond(CoordinatesVO capturedJson) {
        return capturedJson.getCreationDate().getTime()/1000;
    }
}
