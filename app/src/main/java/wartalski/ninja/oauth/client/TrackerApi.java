package wartalski.ninja.oauth.client;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;
import wartalski.ninja.oauth.pojo.User;
import wartalski.ninja.oauth.vo.CoordinatesVO;

/**
 * Created by kuba on 19.05.16.
 */
public interface TrackerApi {

    @POST("/api/coordinates/{userId}")
    @Headers("Accept: application/json")
    Observable<Void> postCoordinates(@Path("userId") Long userId, @Body CoordinatesVO coordinatesVO);

}
