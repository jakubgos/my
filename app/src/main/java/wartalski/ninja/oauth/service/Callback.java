package wartalski.ninja.oauth.service;

/**
 * Created by kuba on 19.05.16.
 */
public interface Callback {

    void onSuccess();
    void onFailure();
}
