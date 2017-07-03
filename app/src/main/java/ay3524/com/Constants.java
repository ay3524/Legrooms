package ay3524.com;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by Ashish on 03-07-2017.
 */

public class Constants {
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
