package id.net.gmedia.absensipsp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Register extends AppCompatActivity {

    private final int PERMISSION_PHONE_STATE = 900;
    private boolean register = true;
    private EditText nik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView txt_activity = findViewById(R.id.txt_activity);
        Button btn_daftar = findViewById(R.id.btn_daftar);
        nik = findViewById(R.id.nik);

        if(getIntent().hasExtra(Constant.EXTRA_REGISTER)){
            register = getIntent().getBooleanExtra(Constant.EXTRA_REGISTER, true);
            if(register){
                txt_activity.setText("Daftar Baru");
                btn_daftar.setText("Daftar");
            }
            else{
                txt_activity.setText("Reset Password");
                btn_daftar.setText("Reset");
            }
        }

        findViewById(R.id.btn_daftar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = nik.getText().toString();
                if(!username.isEmpty()){
                    if(register){
                        if(IMEIManager.isPermitted(Register.this)){
                            daftar(username);
                        }
                        else{
                            ActivityCompat.requestPermissions(Register.this,
                                    new String[]{Manifest.permission.READ_PHONE_STATE},
                                    PERMISSION_PHONE_STATE);
                        }
                    }
                    else{
                        resetPassword(username);
                    }
                }
                else{
                    final Dialog dialog = new Dialog(Register.this);
                    dialog.setContentView(R.layout.popupenterusernamepassword);

                    TextView txt_message = dialog.findViewById(R.id.txt_message);
                    txt_message.setText("Please enter username");
                    dialog.findViewById(R.id.btn_tryagain).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    RelativeLayout close = dialog.findViewById(R.id.closeenterusernamepassword);
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    if(dialog.getWindow() != null){
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    }

                    dialog.show();
                    final Handler handler = new Handler();
                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                    };
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            handler.removeCallbacks(runnable);
                        }
                    });
                    handler.postDelayed(runnable, 3000);
                }
            }
        });
    }

    private void daftar(String username){
        AppLoading.getInstance().showLoading(this);
        JSONBuilder body = new JSONBuilder();
        body.add("nip", username);
        body.add("imei", new JSONArray(IMEIManager.getIMEI(this)));
        Log.d(Constant.TAG, body.create().toString());

        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.urlRegister, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(this), body.create(), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject response = new JSONObject(result);
                            int status = response.getJSONObject("metadata").getInt("status");
                            String message = response.getJSONObject("metadata").getString("message");

                            Toast.makeText(Register.this, message, Toast.LENGTH_SHORT).show();
                            if(status == 1){
                                Intent i = new Intent(Register.this, Login.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(getApplicationContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);

                        AppLoading.getInstance().stopLoading();
                    }
                });
    }

    private void resetPassword(String username){
        AppLoading.getInstance().showLoading(this);
        JSONBuilder body = new JSONBuilder();
        body.add("nip", username);

        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.urlForgotPassword, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(this), body.create(), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject response = new JSONObject(result);
                            int status = response.getJSONObject("metadata").getInt("status");
                            String message = response.getJSONObject("metadata").getString("message");

                            Toast.makeText(Register.this, message, Toast.LENGTH_SHORT).show();
                            if(status == 1){
                                Intent i = new Intent(Register.this, Login.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(getApplicationContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);

                        AppLoading.getInstance().stopLoading();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_PHONE_STATE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(!nik.getText().toString().isEmpty()){
                    daftar(nik.getText().toString());
                }
            }
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Aplikasi Membutuhkan Izin");
                builder.setMessage("Maaf, anda tidak bisa melanjutkan masuk ke aplikasi. " +
                        "Aplikasi tidak memperoleh izin yang dibutuhkan untuk bisa berjalan dengan benar");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(false);
                builder.create().show();
            }
        }
        else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
