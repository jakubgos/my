package wartalski.ninja.oauth.service;

import android.content.Context;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import wartalski.ninja.oauth.R;
import wartalski.ninja.oauth.client.TokenClient;
import wartalski.ninja.oauth.provider.LocalStorageProvider;

/**
 * Created by kuba on 27.05.16.
 */
public class LoginServiceComponentFactory {

    private final Context context;
    private final String path;

    public LoginServiceComponentFactory(Context context) {
        this.context = context;
        path = context.getResources().getString(R.string.path);
    }

    public TokenClient getTokenClient() {
        return new TokenClient(path, context);
    }

    public LocalStorageProvider getLocalStorageTokenProvider() {
        return new LocalStorageProvider(context);
    }

    public String getClientId() {
        return context.getResources().getString(R.string.client_id);
    }

    public String getClientSecret() {
        return context.getResources().getString(R.string.client_secret);
    }

    public Scheduler getSchedulerIO() {
        return Schedulers.io();
    }

    public Scheduler getSchedulerUI() {
        return AndroidSchedulers.mainThread();
    }

}
