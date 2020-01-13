package id.net.gmedia.absensipsp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.util.SparseIntArray;

import java.util.ArrayList;

/**
 * Created by Shin on 30-03-2017.
 */
public abstract class RuntimePermissionsActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSION_SETTING = 10;
    private SparseIntArray mErrorString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mErrorString = new SparseIntArray();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean all_permitted = true;

        for (int i = 0, len = permissions.length; i < len; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Aplikasi Membutuhkan Izin");
                builder.setMessage("Aplikasi membutuhkan semua izin untuk dapat berjalan dengan benar.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                    }
                });
                builder.setCancelable(false);
                builder.create().show();
                all_permitted = false;
            }
        }

        if(all_permitted){
            onPermissionsGranted();
        }
    }

    public void requestAppPermissions(String[] requestedPermissions,
                                      int stringId, int requestCode) {
        mErrorString.put(requestCode, stringId);
        ArrayList<String> permissionsToRequest = new ArrayList<>();

        for (String permission : requestedPermissions) {
            if (ActivityCompat.checkSelfPermission(this,
                    permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), requestCode);
    }

    public abstract void onPermissionsGranted();
}
