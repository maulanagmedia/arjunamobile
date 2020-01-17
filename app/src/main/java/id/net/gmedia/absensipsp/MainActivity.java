package id.net.gmedia.absensipsp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.fxn.pix.Pix;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;

import android.provider.MediaStore;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.ImageLoader;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.TopCropCircularImageView;

import id.net.gmedia.absensipsp.Adapter.ListAdapter;
import id.net.gmedia.absensipsp.Model.CustomItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public final int UPLOAD_CODE = 999;
    public final int PERMISSION_PHONE_STATE = 900;

    private RelativeLayout rvnotif;
    public static TextView tvNotif;


    public static boolean dashboard_fragment_active = true;
    public DrawerLayout drawer;
    public TextView title, tvNik;
    private EditText passLama, passBaru, rePassBaru;
    private EditText passLamaEditPin, passBaruEditPin, rePassBaruEditPin;
    private Boolean klikToVisiblePassLama = true;
    private Boolean klikToVisiblePassBaru = true;
    private Boolean klikToVisibleRePassBaru = true;

    private Fragment fragment;

    //ganti icon untuk PSP
    public final Integer[] icon =
            {
                    R.drawable.drawer_dashboard,
                    R.drawable.drawer_scanlog,
                    R.drawable.drawer_approval,
                    R.drawable.drawer_profile,
                    R.drawable.drawer_jadwal,
                    R.drawable.drawer_uanglembur,
                    R.drawable.drawer_klarifikasi,
                    R.drawable.drawer_cuti,
                    R.drawable.drawer_cutikhusus,
                    R.drawable.drawer_slipgaji,
                    R.drawable.drawer_ijin,
                    R.drawable.drawer_rekapabsensi,
                    R.drawable.drawer_riwayatsp,
                    R.drawable.drawer_userguide,
                    R.drawable.drawer_about,
                    R.drawable.setting,
                    R.drawable.drawer_logout
            };
    public final String[] textIcon=
            {
                    "Dashboard",
                    "Scan Log",
                    "Approval",
                    "Profile",
                    "Jadwal",
                    "Uang Lembur",
                    "Klarifikasi Absensi",
                    "Cuti",
                    "Cuti Khusus",
                    "Slip Gaji",
                    "Ijin",
                    "Rekap Absensi",
                    "Riwayat SP",
                    "User Guide",
                    "About",
                    "Ganti Password",
                    "Logout"
            };
    private boolean doubleBackToExitPressedOnce = false;
    private String version, latestVersion, link;
    public static boolean changelog = false;

    private AlertDialog dialog_update;
    private boolean dialogActive = false;
    private TopCropCircularImageView img_profil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setActionBar(toolbar);

        //CEK AUTHORITY
        SessionManager manager = new SessionManager(this);
        if(!manager.isLoggedIn()){
            Intent i = new Intent(this, Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
            return;
        }

        setContentView(id.net.gmedia.absensipsp.R.layout.activity_main);
        dialogActive = false;

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(id.net.gmedia.absensipsp.R.color.statusbar));
        }

        NavigationView navigationView = findViewById(id.net.gmedia.absensipsp.R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        img_profil = findViewById(R.id.img_profil);
        title = findViewById(id.net.gmedia.absensipsp.R.id.title);
        tvNik = (TextView) findViewById(R.id.tv_nik);
        RelativeLayout buttonDrawer = findViewById(id.net.gmedia.absensipsp.R.id.drawer_button);
        buttonDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = findViewById(id.net.gmedia.absensipsp.R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            }
        });

        //Notif

       /* final  RelativeLayout buttonNotif = findViewById((R.id.notif));
        buttonNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int count = Integer.parseInt(tvNotif.getText().toString());
                tvNotif.setText(++count+"");


            }
        });*/

// Notifikasi
        final RelativeLayout btnNotif = findViewById(R.id.notif);
        btnNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*int count = Integer.parseInt(tvNotif.getText().toString());
                tvNotif.setText(++count+"");*/

                Intent intent = new Intent(MainActivity.this, ActifityNotfif.class);
                startActivity(intent);

            }
        });

        ListView listView = findViewById(id.net.gmedia.absensipsp.R.id.listmenu);
        ArrayList<CustomItem> items = isiArray();
        ListAdapter adapter = new ListAdapter(this, items);
        listView.setAdapter(adapter);

        drawer = findViewById(id.net.gmedia.absensipsp.R.id.drawer_layout);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        loadFragment(new DashboardBaru(), "Dashboard");
                        break;
                    case 1:
                        loadFragment(new ScanLog(), "Scan Log");
                        break;
                    case 2:
                        loadFragment(new Approval(), "Approval");
                        break;
                    case 3:
                        loadFragment(new Profile(), "Profile");
                        break;
                    case 4:
                        loadFragment(new Jadwal(), "Jadwal");
                        break;
                    case 5:
                        loadFragment(new UangLembur(), "Uang Lembur");
                        break;
                    case 6:
                        loadFragment(new KlarifikasiAbsensi(), "Klarifikasi Absensi");
                        break;
                    case 7:
                        loadFragment(new Cuti(), "Cuti");
                        break;
                    case 8:
                        loadFragment(new CutiKhusus(), "Cuti Khusus");
                        break;
                    case 9:
                        loadFragment(new SlipGaji(), "Slip Gaji");
                        break;
                    case 10:
                        loadFragment(new Ijin(), "Ijin");
                        break;
                    case 11:
                        loadFragment(new RekapitulasiAbsensi(), "Rekapitulasi Absensi");
                        break;
                    case 12:
                        loadFragment(new RiwayatSp(), "Riwayat SP");
                        break;
                    case 13:
                        loadFragment(new UserGuide(), "User Guide");
                        break;
                    case 14:
                        loadFragment(new About(), "About");
                        break;
                    case 15:
                        loadFragment(new ChangePSW(), "Ganti Password");
                        break;
                    case 16:
                        SessionManager session = new SessionManager(getApplicationContext());
                        session.logoutUser();
                        finish();
                        break;
                }
            }
        });

        FirebaseApp.initializeApp(MainActivity.this);
        FirebaseMessaging.getInstance().subscribeToTopic("gmedia_news");
        updateFcm();

        prepareBiodataDrawer();
        prepareFotoProfil();

        //Load dashboard for first create
        /*if(getIntent().hasExtra(Constant.EXTRA_APPROVAL)){
            Fragment fragment = new Approval();

            Bundle bundle = new Bundle();
            bundle.putString(Constant.EXTRA_APPROVAL, getIntent()
                    .getStringExtra(Constant.EXTRA_APPROVAL));
            fragment.setArguments(bundle);

            loadFragment(fragment, "Approval");
        }
        else{
            loadFragment(new DashboardBaru(), "Dashboard");
        }*/

        loadFragment(new DashboardBaru(), "Dashboard");
        tvNotif = (TextView) findViewById(R.id.badge_textView);//mendeklarasikan count textview
    }


//============================== MENU SWITCHER =====================================================

    private void callFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainLayout, fragment, fragment.getClass().getSimpleName())
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .addToBackStack(null)
                .commit();
    }

    public void loadFragment(Fragment new_fragment, String fragment_title){
        fragment = new_fragment;
        callFragment(fragment);
        title.setText(fragment_title);
        title.setTypeface(Typeface.createFromAsset(
                this.getAssets(), "fonts/Montserrat-Bold.ttf"));
        drawer.closeDrawer(GravityCompat.START);
        dashboard_fragment_active = fragment instanceof DashboardBaru;
    }

//==================================================================================================

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        if (!dialogActive) {
            dialogActive = true;
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Mohon Hidupkan Akses Lokasi (GPS) Anda.")
                    .setCancelable(false)
                    .setPositiveButton("Hidupkan", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();

            alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    dialogActive = false;
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseApp.initializeApp(MainActivity.this);

        checkVersion();
        if (android.os.Build.VERSION.SDK_INT > 25) {
            statusCheck();
        }

        if(fragment instanceof DashboardBaru) ((DashboardBaru)fragment).dashboard();
    }

    private void checkVersion() {
        version = "";

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e(Constant.TAG, e.getMessage());
        }

        latestVersion = "";
        link = "";

        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.urlUpVersion, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(this), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        JSONObject responseAPI;
                        try {
                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");

                            if (status.equals("1")) {
                                latestVersion = responseAPI.getJSONObject("response").getString("app_version");
                                link = responseAPI.getJSONObject("response").getString("app_link");
                                boolean mandatory = responseAPI.getJSONObject("response").getInt("update_required") == 1;

                                if (!version.trim().equals(latestVersion.trim()) && link.length() > 0) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setIcon(R.mipmap.ic_launcher)
                                            .setTitle("Update")
                                            .setMessage("Versi terbaru " + latestVersion + " " +
                                                    "telah tersedia, mohon download versi terbaru.")
                                            .setPositiveButton("Update Sekarang", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                                    startActivity(browserIntent);
                                                    dialog.dismiss();
                                                }
                                            }).setCancelable(false);

                                    if(!mandatory){
                                        builder.setNegativeButton("Nanti", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                    }

                                    dialog_update = builder.create();
                                    dialog_update.show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(getApplicationContext(), "Terjadi kesalahan check version", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);
                    }
                });
    }

    @Override
    protected void onPause() {
        if(dialog_update != null && dialog_update.isShowing()){
            dialog_update.dismiss();
        }
        super.onPause();
    }

    private void preparePopupEditPin() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.popup_edit_pin);

        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.setCanceledOnTouchOutside(false);
        final ImageView visiblePassLama = dialog.findViewById(id.net.gmedia.absensipsp.R.id.visiblePassLama);
        final ImageView visiblePassBaru = dialog.findViewById(id.net.gmedia.absensipsp.R.id.visiblePassBaru);
        final ImageView visibleRePassBaru = dialog.findViewById(id.net.gmedia.absensipsp.R.id.visibleRePassBaru);
        passLamaEditPin = dialog.findViewById(id.net.gmedia.absensipsp.R.id.passLama);
        passBaruEditPin = dialog.findViewById(id.net.gmedia.absensipsp.R.id.passBaru);
        rePassBaruEditPin = dialog.findViewById(id.net.gmedia.absensipsp.R.id.reTypePassBaru);
        visiblePassLama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (klikToVisiblePassLama) {
                    visiblePassLama.setImageDrawable(getResources().getDrawable(id.net.gmedia.absensipsp.R.drawable.visible));
                    passLamaEditPin.setInputType(InputType.TYPE_CLASS_NUMBER);
                    klikToVisiblePassLama = false;
                } else {
                    visiblePassLama.setImageDrawable(getResources().getDrawable(id.net.gmedia.absensipsp.R.drawable.invisible));
                    passLamaEditPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    passLamaEditPin.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    klikToVisiblePassLama = true;
                }
            }
        });
        visiblePassBaru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (klikToVisiblePassBaru) {
                    visiblePassBaru.setImageDrawable(getResources().getDrawable(id.net.gmedia.absensipsp.R.drawable.visible));
                    passBaruEditPin.setInputType(InputType.TYPE_CLASS_NUMBER);
                    klikToVisiblePassBaru = false;
                } else {
                    visiblePassBaru.setImageDrawable(getResources().getDrawable(id.net.gmedia.absensipsp.R.drawable.invisible));
                    passBaruEditPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    passBaruEditPin.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    klikToVisiblePassBaru = true;
                }
            }
        });
        visibleRePassBaru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (klikToVisibleRePassBaru) {
                    visibleRePassBaru.setImageDrawable(getResources().getDrawable(id.net.gmedia.absensipsp.R.drawable.visible));
                    rePassBaruEditPin.setInputType(InputType.TYPE_CLASS_NUMBER);
                    klikToVisibleRePassBaru = false;
                } else {
                    visibleRePassBaru.setImageDrawable(getResources().getDrawable(id.net.gmedia.absensipsp.R.drawable.invisible));
                    rePassBaruEditPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    rePassBaruEditPin.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    klikToVisibleRePassBaru = true;
                }
            }
        });
        RelativeLayout OK = dialog.findViewById(id.net.gmedia.absensipsp.R.id.tombolOKgantiPassword);
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // validasi
                if (passLamaEditPin.getText().toString().isEmpty()) {
                    passLamaEditPin.setError("Password lama harap diisi");
                    passLamaEditPin.requestFocus();
                    return;
                } else {
                    passLamaEditPin.setError(null);
                }

                if (passBaruEditPin.getText().toString().isEmpty()) {
                    passBaruEditPin.setError("Password baru harap diisi");
                    passBaruEditPin.requestFocus();
                    return;
                } else {
                    passBaruEditPin.setError(null);
                }

                if (rePassBaruEditPin.getText().toString().isEmpty()) {
                    rePassBaruEditPin.setError("Password baru ulang harap diisi");
                    rePassBaruEditPin.requestFocus();
                    return;
                } else {
                    rePassBaruEditPin.setError(null);
                }
                if (!rePassBaruEditPin.getText().toString().equals(passBaruEditPin.getText().toString())) {
                    rePassBaruEditPin.setError("Password ulang tidak sama");
                    rePassBaruEditPin.requestFocus();
                    return;
                } else {
                    rePassBaruEditPin.setError(null);
                }
                prepareDataGantiPin();
                dialog.dismiss();
            }
        });
        RelativeLayout cancel = dialog.findViewById(id.net.gmedia.absensipsp.R.id.tombolcancelGantiPassword);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void prepareDataGantiPin() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(id.net.gmedia.absensipsp.R.layout.loading);
        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        JSONBuilder body = new JSONBuilder();
        body.add("pin", passLamaEditPin.getText().toString());
        body.add("pin_baru", passBaruEditPin.getText().toString());
        body.add("confirm_pin", rePassBaruEditPin.getText().toString());

        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.urlEditPIN, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(this), body.create(), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        dialog.dismiss();
                        try {
                            JSONObject object = new JSONObject(result);
                            String pesan = object.getJSONObject("metadata").getString("message");
                            String status = object.getJSONObject("metadata").getString("status");
                            if (status.equals("1")) {
                                Toast.makeText(getApplicationContext(), pesan, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), pesan, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);
                    }
                });
    }


    // Ganti Pasword
    private void preparePopupGantiPassword() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(id.net.gmedia.absensipsp.R.layout.popup_ganti_password);

        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        final ImageView visiblePassLama = dialog.findViewById(R.id.visiblePassLama);
        final ImageView visiblePassBaru = dialog.findViewById(R.id.visiblePassBaru);
        final ImageView visibleRePassBaru = dialog.findViewById(R.id.visibleRePassBaru);
        passLama = dialog.findViewById(R.id.passLama);
        passBaru = dialog.findViewById(R.id.passBaru);
        rePassBaru = dialog.findViewById(R.id.reTypePassBaru);
        visiblePassLama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (klikToVisiblePassLama) {
                    visiblePassLama.setImageDrawable(getResources().getDrawable(R.drawable.visible));
                    passLama.setInputType(InputType.TYPE_CLASS_TEXT);
                    klikToVisiblePassLama = false;
                } else {
                    visiblePassLama.setImageDrawable(getResources().getDrawable(R.drawable.invisible));
                    passLama.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passLama.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    klikToVisiblePassLama = true;
                }
            }
        });
        visiblePassBaru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (klikToVisiblePassBaru) {
                    visiblePassBaru.setImageDrawable(getResources().getDrawable(R.drawable.visible));
                    passBaru.setInputType(InputType.TYPE_CLASS_TEXT);
                    klikToVisiblePassBaru = false;
                } else {
                    visiblePassBaru.setImageDrawable(getResources().getDrawable(R.drawable.invisible));
                    passBaru.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passBaru.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    klikToVisiblePassBaru = true;
                }
            }
        });
        visibleRePassBaru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (klikToVisibleRePassBaru) {
                    visibleRePassBaru.setImageDrawable(getResources().getDrawable(R.drawable.visible));
                    rePassBaru.setInputType(InputType.TYPE_CLASS_TEXT);
                    klikToVisibleRePassBaru = false;
                } else {
                    visibleRePassBaru.setImageDrawable(getResources().getDrawable(R.drawable.invisible));
                    rePassBaru.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    rePassBaru.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    klikToVisibleRePassBaru = true;
                }
            }
        });
        RelativeLayout OK = dialog.findViewById(R.id.tombolOKgantiPassword);
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // validasi
                if (passLama.getText().toString().isEmpty()) {
                    passLama.setError("Password lama harap diisi");
                    passLama.requestFocus();
                    return;
                } else {
                    passLama.setError(null);
                }

                if (passBaru.getText().toString().isEmpty()) {
                    passBaru.setError("Password baru harap diisi");
                    passBaru.requestFocus();
                    return;
                } else {
                    passBaru.setError(null);
                }

                if (rePassBaru.getText().toString().isEmpty()) {
                    rePassBaru.setError("Password baru ulang harap diisi");
                    rePassBaru.requestFocus();
                    return;
                } else {
                    rePassBaru.setError(null);
                }
                if (!rePassBaru.getText().toString().equals(passBaru.getText().toString())) {
                    rePassBaru.setError("Password ulang tidak sama");
                    rePassBaru.requestFocus();
                    return;
                } else {
                    rePassBaru.setError(null);
                }
                prepareDataGantiPassword();
                dialog.dismiss();
            }
        });
        RelativeLayout cancel = dialog.findViewById(id.net.gmedia.absensipsp.R.id.tombolcancelGantiPassword);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void prepareDataGantiPassword() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(id.net.gmedia.absensipsp.R.layout.loading);

        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        JSONBuilder body = new JSONBuilder();
        body.add("password_lama", passLama.getText().toString());
        body.add("password_baru", passBaru.getText().toString());
        body.add("re_password", rePassBaru.getText().toString());

        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.urlGantiPassword, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(this), body.create(), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        dialog.dismiss();
                        try {
                            JSONObject object = new JSONObject(result);
                            String pesan = object.getJSONObject("metadata").getString("message");
                            String status = object.getJSONObject("metadata").getString("status");
                            if (status.equals("1")) {
                                Toast.makeText(getApplicationContext(), pesan, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), pesan, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        dialog.dismiss();

                        Toast.makeText(getApplicationContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);
                    }
                });
    }

    private void prepareFotoProfil(){
        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.urlFotoProfil, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(this), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject response = new JSONObject(result);
                            int status = response.getJSONObject("metadata").getInt("status");
                            String message = response.getJSONObject("metadata").getString("message");

                            if(status == 1){
                                ImageLoader.load(MainActivity.this,
                                        response.getJSONObject("response").getString("img_link"),
                                        img_profil);
                            }
                            else{
                                Log.e(Constant.TAG, message);
                            }
                        }
                        catch (JSONException e){
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        Log.e(Constant.TAG, result);
                    }
                }
        );
    }

    private void prepareBiodataDrawer() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(id.net.gmedia.absensipsp.R.layout.loading);
        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.urlBiodata, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(this), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        dialog.dismiss();
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            String message = object.getJSONObject("metadata").getString("message");
                            if (status.equals("1")) {
                                JSONObject biodata = object.getJSONObject("response");
                                TextView nama = findViewById(id.net.gmedia.absensipsp.R.id.namaDrawer);
                                nama.setText(biodata.getString("nama"));
                                TextView nip = findViewById(id.net.gmedia.absensipsp.R.id.nikDrawer);
                                nip.setText(biodata.getString("nip"));
                                tvNik.setText(biodata.getString("nip"));
                            } else {
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        dialog.dismiss();

                        Toast.makeText(getApplicationContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);
                    }
                });
    }

    private ArrayList<CustomItem> isiArray() {
        ArrayList<CustomItem> rvData = new ArrayList<>();
        for (int i = 0; i < icon.length; i++) {
            CustomItem customItem = new CustomItem();
            customItem.setIcon(icon[i]);
            customItem.setTextIcon(textIcon[i]);
            rvData.add(customItem);
        }
        return rvData;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!dashboard_fragment_active) {
                loadFragment(new DashboardBaru(), "Dashboard");
            } else {
                if (doubleBackToExitPressedOnce) {
                    finish();
                }

                doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Klik sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = MainActivity.this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(MainActivity.this.getResources().getColor(R.color.statusbar));
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(id.net.gmedia.absensipsp.R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.notif);//Mendapatkan menu notifikasi
        MenuItemCompat.setActionView(menuItem, R.layout.app_bar_main);//mendefinisikan set action view untuk menu notifikasi dengan actionbar_notification
        RelativeLayout relativeLayout = (RelativeLayout)MenuItemCompat.getActionView(menuItem);//parent dari actionbar_notification adalah relativelayout, kita tangkap parentnya untuk digunakan mencari childnya
        tvNotif = (TextView)relativeLayout.findViewById(R.id.badge_textView);//mendeklarasikan count textview
        return super.onCreateOptionsMenu(menu);
    }
*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_notif){
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        return true;
    }

    private void updateFcm(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener
                (this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.d(Constant.TAG, newToken);

                SessionManager.setFcm(MainActivity.this, newToken);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPLOAD_CODE) {
            if (data != null) {
                try {
                    String path = data.getStringArrayListExtra(Pix.IMAGE_RESULTS).get(0);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                            Uri.fromFile(new File(path)));
                    bitmap = Converter.resizeBitmap(bitmap, 750);

                    if(fragment instanceof Ijin){
                        ((Ijin)fragment).prepareBitmap(path, bitmap);
                    }
                    else if(fragment instanceof CutiKhusus){
                        ((CutiKhusus)fragment).prepareBitmap(path, bitmap);
                    }
                    else if(fragment instanceof KlarifikasiAbsensi){
                        ((KlarifikasiAbsensi)fragment).prepareBitmap(path, bitmap);
                    }
                } catch (IOException e) {
                    Log.e(Constant.TAG, e.getMessage());
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_PHONE_STATE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(fragment instanceof DashboardBaru){
                    ((DashboardBaru)fragment).newsDashboard();
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
                        finish();
                    }
                });
                builder.setCancelable(false);
                builder.create().show();
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                });
            }
        }
        else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /*@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
						startActivityForResult(intent, 900);
					}
				});
				builder.setCancelable(false);
				builder.create().show();
				break;
			}
		}
	}*/
}