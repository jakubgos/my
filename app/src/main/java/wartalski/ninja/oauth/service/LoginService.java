package wartalski.ninja.oauth.service;

import android.util.Log;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;
import wartalski.ninja.oauth.client.TokenClient;
import wartalski.ninja.oauth.pojo.Credentials;
import wartalski.ninja.oauth.pojo.OAuth2;
import wartalski.ninja.oauth.pojo.User;
import wartalski.ninja.oauth.provider.LocalStorageProvider;

/**
 * Created by kuba on 19.05.16.
 */
public class LoginService {

    private TokenClient tokenClient;
    private Callback loginCallback;
    private LocalStorageProvider localStorageTokenProvider;
    private Scheduler observeOn;
    private Scheduler subscribeOn;
    private String cliendId;
    private String clientSecret;

    private LoginService(LoginServiceComponentFactory factory, Callback loginCallback) {
        tokenClient = factory.getTokenClient();
        this.loginCallback = loginCallback;
        localStorageTokenProvider = factory.getLocalStorageTokenProvider();
        observeOn = factory.getSchedulerUI();
        subscribeOn = factory.getSchedulerIO();
        cliendId = factory.getClientId();
        clientSecret = factory.getClientSecret();
    }

    public void login(final String login, String password) {
        Credentials credentials = new Credentials.Builder()
                .setLogin(login)
                .setPassword(password)
                .setClientId(cliendId)
                .setClientSercret(clientSecret)
                .build();

        tokenClient.getToken(credentials)
                .flatMap(new Func1<OAuth2, Observable<User>>() {
                    @Override
                    public Observable<User> call(OAuth2 oAuth2) {
                        localStorageTokenProvider.setOAuth2(oAuth2);
                        return tokenClient.getUser();
                    }
                })
                .map(new Func1<User, Void>() {
                    @Override
                    public Void call(User user) {
                        localStorageTokenProvider.setUser(user);
                        Log.i("...", "user.name " + user.getName());
                        Log.i("...", "user.surname " + user.getSurname());
                        return null;
                    }
                })
                .observeOn(observeOn)
                .subscribeOn(subscribeOn)
                .subscribe(new CallbackAdapter(loginCallback));
    }

    public static class Builder {

        private Callback callback;
        private LoginServiceComponentFactory factory;

        public Builder setCallback(Callback callback) {
            this.callback = callback;
            return this;
        }

        public Builder setFactory(LoginServiceComponentFactory factory) {
            this.factory = factory;
            return this;
        }

        public LoginService build() {
            if(factory == null) {
                throw new IllegalStateException("LoginServiceComponentFactory not provided !");
            }
            if(callback == null) {
                throw new IllegalStateException("Callback not provided !");
            }
            return new LoginService(factory, callback);
        }
    }

}
