package wartalski.ninja.oauth.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import rx.Observable;
import rx.schedulers.Schedulers;
import wartalski.ninja.oauth.BuildConfig;
import wartalski.ninja.oauth.client.TrackerClient;
import wartalski.ninja.oauth.provider.LocalStorageProvider;
import wartalski.ninja.oauth.vo.CoordinatesVO;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by kuba on 27.05.16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 19)
public class TrackerServiceTest {

    @Mock
    private TrackerClient trackerClient;
    @Mock
    private LocalStorageProvider localStorageProvider;
    @Mock
    private TrackerServiceComponentFactory factory;
    @Mock
    private Callback callback;

    private final TrackerService trackerService;

    public TrackerServiceTest() {
        initMocks(this);
        when(factory.getLocalStorageProvider()).thenReturn(localStorageProvider);
        when(factory.getTrackerClient()).thenReturn(trackerClient);
        when(factory.getSchedulerIO()).thenReturn(Schedulers.immediate());
        when(factory.getSchedulerUI()).thenReturn(Schedulers.immediate());
        trackerService = new TrackerService.Builder().setFactory(factory).build();
    }

    @After
    public void tearDown() {
        reset(trackerClient, localStorageProvider, callback);
    }

    @Before
    public void setup() {
        when(trackerClient.postData(any(CoordinatesVO.class), anyLong())).thenReturn(Observable.<Void>just(null));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowIllegalStateExceptionWhenNoFactoryProvided() {
        new TrackerService.Builder().build();
    }

    @Test
    public void shouldSendCoordinates() {
        final CoordinatesVO coordinatesVO = new CoordinatesVO();
        coordinatesVO.setLng(11.0);
        coordinatesVO.setLng(55.0);
        final Date date = new Date();
        coordinatesVO.setCreationDate(date);

        when(localStorageProvider.getUserId()).thenReturn(123L);

        trackerService.sendCoordinates(coordinatesVO, callback);
        verify(trackerClient, times(1)).postData(coordinatesVO, 123L);
        verifyNoMoreInteractions(trackerClient);
        verify(callback, times(1)).onSuccess();
    }

    @Test
    public void shouldCallOnFailureWhenError() {
        final CoordinatesVO coordinatesVO = new CoordinatesVO();
        coordinatesVO.setLng(11.0);
        coordinatesVO.setLng(55.0);
        final Date date = new Date();
        coordinatesVO.setCreationDate(date);

        when(localStorageProvider.getUserId()).thenReturn(123L);
        when(trackerClient.postData(coordinatesVO, 123L)).thenReturn(Observable.<Void>error(new Throwable()));

        trackerService.sendCoordinates(coordinatesVO, callback);

        verify(callback, times(1)).onFailure();
    }

}
