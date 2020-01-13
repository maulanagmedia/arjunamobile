package id.net.gmedia.absensipsp;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    private final int PERMISSION_PHONE_STATE = 900;

    private EditText NIK, password;
    public Button login;
    private boolean doubleBackToExitPressedOnce = false;
    SessionManager session;
    private String level = "";

    private ImageView img_visible;
    private boolean password_visible = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        MainActivity.dashboard_fragment_active = true;
        session = new SessionManager(getApplicationContext());
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusbar));
        }

        //Inisialisasi UI
        NIK = findViewById(R.id.nik);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        img_visible = findViewById(R.id.img_visible);

        View layoutLogin = findViewById(R.id.layoutLogin);
        layoutLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideKeyboard.hideSoftKeyboard(Login.this);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(IMEIManager.isPermitted(Login.this)){
                    checkLogin();
                }
                else{
                    ActivityCompat.requestPermissions(Login.this,
                            new String[]{Manifest.permission.READ_PHONE_STATE},
                            PERMISSION_PHONE_STATE);
                }
            }
        });

        findViewById(R.id.txt_daftar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, Register.class);
                i.putExtra(Constant.EXTRA_REGISTER, true);
                startActivity(i);
            }
        });

        findViewById(R.id.txt_lupa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, Register.class);
                i.putExtra(Constant.EXTRA_REGISTER, false);
                startActivity(i);
            }
        });

        img_visible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password_visible = !password_visible;
                if(password_visible){
                    img_visible.setImageDrawable(getResources().getDrawable(R.drawable.visible));
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                else{
                    img_visible.setImageDrawable(getResources().getDrawable(R.drawable.invisible));
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    private void checkLogin(){
        String username = NIK.getText().toString();
        String pass = password.getText().toString();
        if (username.trim().length() > 0 && pass.trim().length() > 0) {
            loginRequest();
        } else {
            final Dialog dialog = new Dialog(Login.this);
            dialog.setContentView(R.layout.popupenterusernamepassword);

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

    private void loginRequest() {
        final Dialog dialogLoading = new Dialog(Login.this);
        dialogLoading.setContentView(R.layout.loading);
        if(dialogLoading.getWindow() != null){
            dialogLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialogLoading.setCanceledOnTouchOutside(false);
        dialogLoading.show();

        JSONBuilder body = new JSONBuilder();
        body.add("username", NIK.getText().toString());
        body.add("password", password.getText().toString());
        body.add("imei", new JSONArray(IMEIManager.getIMEI(this)));

        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.urlLogin, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(this), body.create(), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        dialogLoading.dismiss();
                        try {
                            JSONObject object = new JSONObject(result);
                            final String status = object.getJSONObject("metadata").getString("status");
                            if (status.equals("1")) {
                                String token = object.getJSONObject("response").getString("token");
                                String nip = object.getJSONObject("response").getString("nip");
                                level = object.getJSONObject("response").getString("approval");
                                //boolean first_login = object.getJSONObject("response").getInt("flag_password") == 0;
                                session.createLoginSession("", "", token, nip, level);

                                Intent intent = new Intent(Login.this, MainActivity.class);
                                /*if(first_login){
                                    intent.putExtra(Constant.EXTRA_FLAG_LOGIN, true);
                                }*/
                                startActivity(intent);

                                finish();
                                dialogLoading.dismiss();
                            } else {
                                dialogLoading.dismiss();
                                final Dialog dialog = new Dialog(Login.this);

                                dialog.setContentView(R.layout.popupincorectusernamepassword);
                                RelativeLayout close = dialog.findViewById(R.id.closeincorectusernamepassword);
                                close.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                if(dialog.getWindow() != null){
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                }

                                TextView txt_message = dialog.findViewById(R.id.txt_message);
                                txt_message.setText(object.getJSONObject("metadata").getString("message"));

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
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        dialogLoading.dismiss();
                        Toast.makeText(getApplicationContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();

                        Log.e(Constant.TAG, result);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
        }
        else{
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Klik sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_PHONE_STATE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                checkLogin();
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
