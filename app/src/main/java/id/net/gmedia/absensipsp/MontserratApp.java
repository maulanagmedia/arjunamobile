package id.net.gmedia.absensipsp;

import android.app.Application;

public class MontserratApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/Montserrat-Regular.ttf");
    }
}