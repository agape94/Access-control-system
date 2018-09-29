package dailyalex.com.openthedoor;

import android.os.Handler;
import android.os.Looper;



/**
 * Created by alex on 20.03.2018.
 */

public class Utils {
    public static void runOnUiThread(Runnable runnable){
        final Handler UIHandler = new Handler(Looper.getMainLooper());
        UIHandler.post(runnable);
    }
}