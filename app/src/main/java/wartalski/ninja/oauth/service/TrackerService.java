package wartalski.ninja.oauth.service;

import rx.Scheduler;
import wartalski.ninja.oauth.client.TrackerClient;
import wartalski.ninja.oauth.provider.LocalStorageProvider;
import wartalski.ninja.oauth.vo.CoordinatesVO;

/**
 * Created by kuba on 27.05.16.
 */
public class TrackerService {

    private TrackerClient trackerClient;
    private LocalStorageProvider localStorageProvider;
    private Scheduler io;
    private Scheduler ui;

    private TrackerService(TrackerServiceComponentFactory factory) {
        trackerClient = factory.getTrackerClient();
        localStorageProvider = factory.getLocalStorageProvider();
        io = factory.getSchedulerIO();
        ui = factory.getSchedulerUI();
    }

    public void sendCoordinates(CoordinatesVO coordinatesVO, final Callback callback) {
        trackerClient.postData(coordinatesVO, localStorageProvider.getUserId())
                .subscribeOn(io)
                .observeOn(ui)
                .subscribe(new CallbackAdapter(callback));
    }

    public static class Builder {

        private TrackerServiceComponentFactory factory;

        public Builder setFactory(TrackerServiceComponentFactory factory) {
            this.factory = factory;
            return this;
        }

        public TrackerService build() {
            if(factory == null)
                throw new IllegalStateException("TrackerServiceComponentFactory not provided !");

            return new TrackerService(factory);
        }


    }


}
