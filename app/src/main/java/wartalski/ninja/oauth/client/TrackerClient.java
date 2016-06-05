package wartalski.ninja.oauth.client;

import android.content.Context;
import android.util.Log;

import rx.Observable;
import wartalski.ninja.oauth.pojo.User;
import wartalski.ninja.oauth.vo.CoordinatesVO;

/**
 * Created by kuba on 19.05.16.
 */
public class TrackerClient extends Client<TrackerApi> {

    public TrackerClient(String path, Context context) {
        super(TrackerApi.class, path, context);
    }

    public Observable<Void> postData(CoordinatesVO coordinatesVO, Long userId) {
        Log.i("...", "lng " + coordinatesVO.getLng());
        Log.i("...", "lat " + coordinatesVO.getLat());
        Log.i("...", "creation date " + coordinatesVO.getCreationDate());
        return apiClient.postCoordinates(userId, coordinatesVO);
    }

}
