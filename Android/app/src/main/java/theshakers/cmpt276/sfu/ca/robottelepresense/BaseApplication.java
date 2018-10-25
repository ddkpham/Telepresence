package theshakers.cmpt276.sfu.ca.robottelepresense;

import android.app.Application;
import android.content.Context;

/**
 * Created by baesubin on 2018-10-14.
 */

//singleton model for holding important things like context
public class BaseApplication extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
    }

    public static Context getInstance(){
        return context;
    }

}
