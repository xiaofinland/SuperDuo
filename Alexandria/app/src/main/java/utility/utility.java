package utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by xiaoma on 10/02/16.
 */
public class Utility {
    /**
          * Returns true if the network is available or about to become available.
          *
          * @param c Context used to get the ConnectivityManager
          * @return
          */
        static public boolean isNetworkAvailable(Context c) {
                ConnectivityManager cm =
                                (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);

                        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
               return activeNetwork != null &&
                       activeNetwork.isConnectedOrConnecting();
        }
}
