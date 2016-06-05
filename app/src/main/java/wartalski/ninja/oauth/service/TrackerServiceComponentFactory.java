package wartalski.ninja.oauth.service;

import android.content.Context;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import wartalski.ninja.oauth.R;
import wartalski.ninja.oauth.client.TrackerClient;
import wartalski.ninja.oauth.provider.LocalStorageProvider;

/**
 * Created by kuba on 27.05.16.
 */
public class TrackerServiceComponentFactory {

    private final String path;
    private Context context;

    public TrackerServiceComponentFactory(Context context) {
        this.context = context;
        path = context.getResources().getString(R.string.path);
    }

    public TrackerClient getTrackerClient() {
        return new TrackerClient(path, context);
    }

    public LocalStorageProvider getLocalStorageProvider() {
        return new LocalStorageProvider(context);
    }

    public Scheduler getSchedulerIO() {
        return Schedulers.io();
    }

    public Scheduler getSchedulerUI() {
        return AndroidSchedulers.mainThread();
    }
}
