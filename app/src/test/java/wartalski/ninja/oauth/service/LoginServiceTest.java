package wartalski.ninja.oauth.service;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import rx.Observable;
import rx.schedulers.Schedulers;
import wartalski.ninja.oauth.BuildConfig;
import wartalski.ninja.oauth.client.TokenClient;
import wartalski.ninja.oauth.pojo.Credentials;
import wartalski.ninja.oauth.pojo.OAuth2;
import wartalski.ninja.oauth.pojo.User;
import wartalski.ninja.oauth.provider.LocalStorageProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by kuba on 19.05.16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 19)
public class LoginServiceTest {

    @Mock
    private TokenClient tokenClient;
    @Mock
    private Callback callback;
    @Mock
    private LocalStorageProvider localStorageTokenProvider;
    @Mock
    private LoginServiceComponentFactory factory;

    public LoginServiceTest() {
        initMocks(this);
        when(factory.getLocalStorageTokenProvider()).thenReturn(localStorageTokenProvider);
        when(factory.getTokenClient()).thenReturn(tokenClient);
        when(factory.getSchedulerIO()).thenReturn(Schedulers.immediate());
        when(factory.getSchedulerUI()).thenReturn(Schedulers.immediate());
    }

    @After
    public void tearDown() {
        reset(tokenClient, callback, factory, localStorageTokenProvider);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowIllegalStateExceptionWhenFactoryNotProvided() {
        new LoginService.Builder().setCallback(callback).build();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowIllegalStateExceptionWhenCallbackNotProvided() {
        new LoginService.Builder().setFactory(factory).build();
    }

    @Test
    public void shouldCallOnSuccessWhenInitializationWasSuccessful() {
        final LoginService loginService = new LoginService.Builder()
                .setCallback(callback)
                .setFactory(factory)
                .build();

        when(tokenClient.getToken(any(Credentials.class))).thenReturn(Observable.just(new OAuth2()));
        when(tokenClient.getUser()).thenReturn(Observable.just(new User()));

        loginService.login("login", "password");

        verify(callback, times(1)).onSuccess();
        verifyNoMoreInteractions(callback);
    }

    @Test
    public void shouldSaveTokensWhenLoginWasSuccessful() {
        final LoginService loginService = new LoginService.Builder()
                .setCallback(callback)
                .setFactory(factory)
                .build();

        when(tokenClient.getToken(any(Credentials.class))).thenReturn(Observable.just(new OAuth2()));

        loginService.login("login", "password");

        verify(localStorageTokenProvider, times(1)).setOAuth2(any(OAuth2.class));
        verifyNoMoreInteractions(localStorageTokenProvider);
    }

    @Test
    public void shouldCallOnFailWhenErrorOccured() {
        final LoginService loginService = new LoginService.Builder()
                .setCallback(callback)
                .setFactory(factory)
                .build();

        when(tokenClient.getToken(any(Credentials.class))).thenReturn(Observable.<OAuth2>error(new Throwable()));

        loginService.login("login", "password");

        verify(callback, times(1)).onFailure();
        verifyNoMoreInteractions(callback);
    }

    @Test
    public void shouldSaveUserWhenLoginWasSuccessful() {
        final LoginService loginService = new LoginService.Builder()
                .setCallback(callback)
                .setFactory(factory)
                .build();

        final ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        final User user = new User();
        user.setId(123L);
        when(tokenClient.getToken(any(Credentials.class))).thenReturn(Observable.just(new OAuth2()));
        when(tokenClient.getUser()).thenReturn(Observable.just(user));

        loginService.login("login", "password");

        verify(localStorageTokenProvider, times(1)).setUser(captor.capture());
        assertThat(captor.getValue().getId(), is(123L));
    }


}
