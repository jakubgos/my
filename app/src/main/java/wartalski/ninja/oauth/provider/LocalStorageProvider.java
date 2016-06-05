package wartalski.ninja.oauth.provider;

import android.content.Context;
import android.content.SharedPreferences;

import wartalski.ninja.oauth.pojo.OAuth2;
import wartalski.ninja.oauth.pojo.User;

/**
 * Created by kuba on 07.05.16.
 */
public class LocalStorageProvider {

    private static SharedPreferences sharedPreferences;
    private final static String ACCESS_TOKEN = "access_token";
    private final static String REFRESH_TOKEN= "refresh_token";
    private final static String USER_NAME = "user_name";
    private static final String USER_SURNAME = "user_surname";
    private final static String USER_ID = "user_id";

    public LocalStorageProvider(Context context) {
        if(sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences("token_store", Context.MODE_PRIVATE);
        }
    }

    public void setOAuth2(OAuth2 tokens) {
        setAccessToken(tokens.getAccessToken());
        setRefreshToken(tokens.getRefreshToken());
    }

    public String getAccessToken() {
        return sharedPreferences.getString(ACCESS_TOKEN, "");
    }

    public void setAccessToken(String accessToken) {
        sharedPreferences
                .edit()
                .putString(ACCESS_TOKEN, accessToken)
                .commit();
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(REFRESH_TOKEN, "");
    }

    public void setRefreshToken(String refreshToken) {
        sharedPreferences
                .edit()
                .putString(REFRESH_TOKEN, refreshToken)
                .commit();
    }

    public void setUser(User user) {
        sharedPreferences.edit()
                .putLong(USER_ID, user.getId())
                .putString(USER_NAME, user.getName())
                .putString(USER_SURNAME, user.getSurname())
                .commit();
    }

    public String getUserName() {
        return sharedPreferences.getString(USER_NAME, "");
    }

    public String getUserSurname() {
        return sharedPreferences.getString(USER_SURNAME, "");
    }

    public Long getUserId() {
        return sharedPreferences.getLong(USER_ID, -1);
    }

    public void reset() {
        sharedPreferences.edit().clear().commit();
    }

}
