package wartalski.ninja.oauth.provider;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import wartalski.ninja.oauth.interceptor.ClientDataFactory;
import wartalski.ninja.oauth.interceptor.RequestInterceptor;

/**
 * Created by kuba on 19.05.16.
 */
public class RestAdapterProvider {

    private String path;
    private Context context;

    public RestAdapterProvider setPath(String path) {
        this.path = path;
        return this;
    }

    public RestAdapterProvider setContext(Context context) {
        this.context = context;
        return this;
    }

    public Retrofit getRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new RequestInterceptor(path,
                        new LocalStorageProvider(context),
                        ClientDataFactory.createClientData(context))).build();

        Retrofit restAdapter = new Retrofit.Builder()
                .client(client)
                .baseUrl(path)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return restAdapter;
    }

}
