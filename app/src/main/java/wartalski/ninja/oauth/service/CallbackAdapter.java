package wartalski.ninja.oauth.service;

/**
 * Created by kuba on 19.05.16.
 */
public class CallbackAdapter extends rx.Subscriber<Void> {

    private Callback loginCallback;

    public CallbackAdapter(Callback loginCallback) {
        this.loginCallback = loginCallback;
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        loginCallback.onFailure();
    }

    @Override
    public void onNext(Void v) {
        loginCallback.onSuccess();
    }
}
