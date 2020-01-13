package id.net.gmedia.absensipsp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class IMEIManager{

    public static ArrayList<String> getIMEI(Context context) {
        ArrayList<String> imeiList = new ArrayList<>();

        try {
            TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
            Class<?>[] parameter = new Class[1];

            parameter[0] = int.class;
            Method getFirstMethod = telephonyClass.getMethod("getDeviceId", parameter);
            Object[] obParameter = new Object[1];

            //First IMEI
            obParameter[0] = 0;
            String first = (String) getFirstMethod.invoke(telephony, obParameter);
            if (first != null && !first.equals("")) imeiList.add(first);

            //Second IMEI
            obParameter[0] = 1;
            String second = (String) getFirstMethod.invoke(telephony, obParameter);
            if (second != null && !second.equals("")) imeiList.add(second);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(Constant.TAG, e.getMessage());
        }

        return imeiList;
    }

    public static boolean isPermitted(Activity activity){
        if(Build.VERSION.SDK_INT >= 23){
            return ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.READ_PHONE_STATE) ==
                    PackageManager.PERMISSION_GRANTED;
        }
        else{
            return true;
        }
    }
}
