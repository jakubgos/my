package wartalski.ninja.oauth.interceptor;

import android.content.Context;

import wartalski.ninja.oauth.R;

/**
 * Created by kuba on 27.05.16.
 */
public class ClientDataFactory {

    public static ClientData createClientData(Context context) {
        final ClientData clientData = new ClientData();
        clientData.setClientId(context.getResources().getString(R.string.client_id));
        clientData.setClientSecret(context.getResources().getString(R.string.client_secret));
        return clientData;
    }
}
