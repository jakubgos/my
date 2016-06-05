package wartalski.ninja.oauth.client;

import android.content.Context;

import retrofit2.Retrofit;
import wartalski.ninja.oauth.provider.RestAdapterProvider;

/**
 * Created by kuba on 19.05.16.
 */
public abstract class Client<T> {

    protected T apiClient;

    public Client(Class<T> apiClass, String path, Context context) {
        Retrofit restAdapter = new RestAdapterProvider().setPath(path).setContext(context).getRetrofit();
        apiClient = restAdapter.create(apiClass);
    }




}
