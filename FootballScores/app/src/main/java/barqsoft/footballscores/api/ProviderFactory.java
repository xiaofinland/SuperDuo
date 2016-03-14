package barqsoft.footballscores.api;

import android.content.Context;

import barqsoft.footballscores.utils.Connection;

/**
 * Created by xiaoma on 15/03/16.
 */
public class ProviderFactory {
    private Context mContext;
    private OnDataReady mOnDataReady;

    public ProviderFactory(Context context, OnDataReady onDataReady ){
        mContext = context;
        mOnDataReady = onDataReady;
    }

    /**
     * Gets the correct provider. It depends on the internet connection.
     * @return Provider.
     */
    public Provider getProvider() {
        if(Connection.checkInternet(mContext)) {
            return new InternetProvider(mContext, mOnDataReady);
        } else {
            return new DatabaseProvider(mContext, mOnDataReady);
        }
    }
}
