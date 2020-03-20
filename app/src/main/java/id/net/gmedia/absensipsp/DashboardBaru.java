package id.net.gmedia.absensipsp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.JSONBuilder;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import id.net.gmedia.absensipsp.Adapter.CustomLinearLayoutManager;
import id.net.gmedia.absensipsp.Adapter.BeritaAdapter;
import id.net.gmedia.absensipsp.Model.BeritaModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DashboardBaru extends Fragment {
    private Activity activity;
    private View view;

    private SlidingUpPanelLayout slidingPaneLayout;
    static boolean tombol = true;
    private RecyclerView rvView;
    private RecyclerView.LayoutManager layoutManager;

    private SessionManager session;
    private Boolean klikToVisibleInputPIN = true;
    private Boolean klikToVisibleReTypeInputPIN = true;
    private EditText inputPIN, reTypeInputPIN;
    private String flagPIN = "";

    private ArrayList<BeritaModel> listBerita = new ArrayList<>();
    private BeritaAdapter beritaAdapter;

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup viewGroup, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dashboard_baru, viewGroup, false);
        activity = getActivity();

        session = new SessionManager(activity);
        FirebaseApp.initializeApp(activity);
        FirebaseMessaging.getInstance().subscribeToTopic("gmedia_news");

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.statusbar));
        }

        rvView = view.findViewById(R.id.rv_DashboardBaru);
        CustomLinearLayoutManager customLayoutManager = new
                CustomLinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        rvView.setLayoutManager(customLayoutManager);

        final RelativeLayout layoutnews = view.findViewById(R.id.layoutnews);
        final ImageView panah = view.findViewById(R.id.panah);
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.animasi);
        LinearLayout layoutdaftar = view.findViewById(R.id.layoutdaftar);
        layoutdaftar.startAnimation(animation);
        slidingPaneLayout = view.findViewById(R.id.sliding);
        slidingPaneLayout.setDragView(R.id.layoutnews);
        slidingPaneLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layout.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    layoutnews.setLayoutParams(layout);
                    layoutnews.setBackground(getResources().getDrawable(R.drawable.backgroundnewsslidinglayout));
                    panah.setImageResource(R.drawable.panah_keatas_putih);
                    CustomLinearLayoutManager customLayoutManager = new CustomLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    rvView.setLayoutManager(customLayoutManager);
                } else if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    layoutnews.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                    layoutnews.setBackgroundColor(getResources().getColor(R.color.colorNews));
                    panah.setImageResource(R.drawable.panah_kebawah_putih);
                    layoutManager = new LinearLayoutManager(activity);
                    rvView.setLayoutManager(layoutManager);
                }
            }
        });

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);

        int device_TotalHeight = size.y;
        slidingPaneLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingPaneLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
        slidingPaneLayout.setPanelHeight(device_TotalHeight/6);

        /*final Button checkin = view.findViewById(R.id.tombolCheckinDashboard);
        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                *//*fragment = new Checkin();
                bundle.putString("Check", "in");
                fragment.setArguments(bundle);
                callFragment(fragment);
                MainActivity.title.setText("Absen");
                MainActivity.title.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/Helvetica-Bold.otf"));
                MainActivity.dashboard_fragment_active = false;
                tombol = true;*//*

                Toast.makeText(activity, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });
        Button checkout = view.findViewById(R.id.tombolCheckoutDashboard);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                *//*fragment = new Checkin();
                bundle.putString("Check", "out");
                fragment.setArguments(bundle);
                callFragment(fragment);
                MainActivity.title.setText("Absen");
                MainActivity.title.setTypeface(Typeface.createFromAsset(DashboardBaru.this.getActivity().getAssets(), "fonts/Helvetica-Bold.otf"));
                MainActivity.dashboard_fragment_active = false;
                tombol = false;*//*

                Toast.makeText(activity, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });*/

        RelativeLayout menuSisaCuti = view.findViewById(R.id.menuSisaCuti);
        menuSisaCuti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new Cuti();
                callFragment(fragment);
                ((MainActivity)activity).title.setText("Cuti");
                MainActivity.dashboard_fragment_active = false;
            }
        });

        RelativeLayout menuUangLembur = view.findViewById(R.id.menuUangLembur);
        menuUangLembur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new UangLembur();
                callFragment(fragment);
                ((MainActivity)activity).title.setText("Uang Lembur");
                MainActivity.dashboard_fragment_active = false;
            }
        });

        RelativeLayout menuUangMakan = view.findViewById(R.id.menuUangMakan);
        menuUangMakan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new ViewMangkir();
                callFragment(fragment);
                ((MainActivity)activity).title.setText("Mangkir");
                MainActivity.dashboard_fragment_active = false;
            }
        });

        RelativeLayout menuTotalTerlambat = view.findViewById(R.id.menuTotalTerlambat);
        menuTotalTerlambat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, TotalTerlambat.class);
                startActivity(i);
            }
        });

        if(IMEIManager.isPermitted(activity)){
            dashboard();
            newsDashboard();
        }
        else{
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    ((MainActivity)activity).PERMISSION_PHONE_STATE);
        }
        return view;
    }

    void newsDashboard() {
        ApiVolleyManager.getInstance().addSecureRequest(activity, Constant.urlNews, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(activity), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        listBerita = new ArrayList<>();
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            if (status.equals("1")) {
                                JSONArray array = object.getJSONArray("response");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject objectNews = array.getJSONObject(i);
                                    listBerita.add(new BeritaModel(
                                            objectNews.getString("id"),
                                            objectNews.getString("judul"),
                                            objectNews.getString("berita"),
                                            objectNews.getString("tanggal"),
                                            objectNews.getString("file")
                                    ));
                                }

                                rvView.setAdapter(null);
                                beritaAdapter = new BeritaAdapter(activity, listBerita);
                                rvView.setAdapter(beritaAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(activity, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);
                    }
                });
    }

    private void preparePopupInputPin() {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.popup_input_pin);

        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.setCanceledOnTouchOutside(false);

        final ImageView visibleInputPIN = dialog.findViewById(R.id.visibleInputPin);
        inputPIN = dialog.findViewById(R.id.inputPIN);

        inputPIN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final Handler handler = new Handler();
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (inputPIN.getText().toString().length() < 6) {
                            inputPIN.setError("PIN harus 6 karakter");
                        }
                    }
                };
                handler.postDelayed(runnable, 2000);
            }
        });

        visibleInputPIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (klikToVisibleInputPIN) {
                    visibleInputPIN.setImageDrawable(getResources().getDrawable(R.drawable.visible));
                    inputPIN.setInputType(InputType.TYPE_CLASS_NUMBER);
                    klikToVisibleInputPIN = false;
                } else {
                    visibleInputPIN.setImageDrawable(getResources().getDrawable(R.drawable.invisible));
                    inputPIN.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    inputPIN.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    klikToVisibleInputPIN = true;
                }
            }
        });

        final ImageView visibleReTypeInputPIN = dialog.findViewById(R.id.visibleReTypeInputPin);
        reTypeInputPIN = dialog.findViewById(R.id.reTypeInputPIN);

        reTypeInputPIN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final Handler handler = new Handler();
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (reTypeInputPIN.getText().toString().length() < 6) {
                            reTypeInputPIN.setError("PIN harus 6 karakter");
                        }
                    }
                };
                handler.postDelayed(runnable, 2000);
            }
        });

        visibleReTypeInputPIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (klikToVisibleReTypeInputPIN) {
                    visibleReTypeInputPIN.setImageDrawable(getResources().getDrawable(R.drawable.visible));
                    reTypeInputPIN.setInputType(InputType.TYPE_CLASS_NUMBER);
                    klikToVisibleReTypeInputPIN = false;
                } else {
                    visibleReTypeInputPIN.setImageDrawable(getResources().getDrawable(R.drawable.invisible));
                    reTypeInputPIN.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    reTypeInputPIN.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    klikToVisibleReTypeInputPIN = true;
                }
            }
        });

        RelativeLayout OK = dialog.findViewById(R.id.tombolOKInputPIN);
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // validasi
                if (inputPIN.getText().toString().isEmpty()) {
                    inputPIN.setError("PIN harap diisi");
                    inputPIN.requestFocus();
                    return;
                } else if (inputPIN.getText().toString().length() != 6) {
                    inputPIN.setError("PIN harus 6 karakter");
                    inputPIN.requestFocus();
                    return;
                } else {
                    inputPIN.setError(null);
                }
                if (reTypeInputPIN.getText().toString().isEmpty()) {
                    reTypeInputPIN.setError("PIN harap diisi");
                    reTypeInputPIN.requestFocus();
                    return;
                } else if (reTypeInputPIN.getText().toString().length() != 6) {
                    reTypeInputPIN.setError("PIN harus 6 karakter");
                    reTypeInputPIN.requestFocus();
                    return;
                } else if (!reTypeInputPIN.getText().toString().equals(inputPIN.getText().toString())) {
                    reTypeInputPIN.setError("PIN anda tidak singkron");
                    return;
                } else {
                    reTypeInputPIN.setError(null);
                }

                prepareDataInputPin();
                dialog.dismiss();
            }
        });

        RelativeLayout cancel = dialog.findViewById(R.id.tombolSkipInputPIN);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void prepareDataInputPin() {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.loading);

        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        JSONBuilder body = new JSONBuilder();
        body.add("pin", inputPIN.getText().toString());
        body.add("confirm_pin", reTypeInputPIN.getText().toString());

        ApiVolleyManager.getInstance().addSecureRequest(activity, Constant.urlCreatePIN, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(activity), body.create(), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        dialog.dismiss();

                        try {
                            JSONObject object = new JSONObject(result);
                            String pesan = object.getJSONObject("metadata").getString("message");
                            String status = object.getJSONObject("metadata").getString("status");
                            if (status.equals("1")) {
                                Toast.makeText(activity, pesan, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(activity, pesan, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        dialog.dismiss();

                        Toast.makeText(activity, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    void dashboard() {
        //String imei = Arrays.toString(GetImei.getIMEI(activity).toArray());

        JSONBuilder body = new JSONBuilder();
        body.add("imei", new JSONArray(IMEIManager.getIMEI(activity)));
        body.add("nip", session.getNip());
        body.add("fcm_id", SessionManager.getFcm(activity));

        ApiVolleyManager.getInstance().addSecureRequest(getContext(), Constant.urlDashboard,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(getContext()), body.create(),
                new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            if (status.equals("1")) {
                                JSONArray array = object.getJSONArray("response");
                                if (array.length() > 0) {
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject isi = array.getJSONObject(i);
                                        if (i == 0) {
                                            TextView sisaCutiDashboard = view.findViewById(R.id.sisaCutiDashboard);
                                            sisaCutiDashboard.setText(isi.getString("label"));
                                            TextView contentSisaCutiDashboard = view.findViewById(R.id.contentSisaCutiDashboard);
                                            contentSisaCutiDashboard.setText(isi.getString("value"));
                                        } else if (i == 1) {
                                            TextView totalTerlambatDashboard = view.findViewById(R.id.totalTerlambatDashboard);
                                            totalTerlambatDashboard.setText(isi.getString("label"));
                                            TextView contentTotalTerlambatDashboard = view.findViewById(R.id.contentTotalTerlambatDashboard);
                                            contentTotalTerlambatDashboard.setText(isi.getString("value"));
                                        } else if (i == 2) {
                                            TextView uangMakanDashboard = view.findViewById(R.id.uangMakanDashboard);
                                            uangMakanDashboard.setText(isi.getString("label"));
                                            TextView contentUangMakanDashboard = view.findViewById(R.id.contentUangMakanDashboard);
                                            contentUangMakanDashboard.setText(isi.getString("value"));
                                        } else if (i == 3) {
                                            TextView uangLemburDashboard = view.findViewById(R.id.uangLemburDashboard);
                                            uangLemburDashboard.setText(isi.getString("label"));
                                            TextView contentUangLemburDashboard = view.findViewById(R.id.contentUangLemburDashboard);
                                            contentUangLemburDashboard.setText(isi.getString("value"));
                                        } else if (i == 4) {
                                            if (MainActivity.tvNotif != null){
                                                MainActivity.tvNotif.setText(isi.getString("value"));
                                            }

                                           /* flagPIN = isi.getString("flag_pin");
                                            Log.d("flagPin", isi.getString("flag_pin"));*/
                                        }
                                    }
                                }
                                if (flagPIN.equals("0")) {
                                    preparePopupInputPin();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(getContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);
                    }
                });
    }

    private Fragment fragment;

    private void callFragment(Fragment fragment) {
        ((FragmentActivity)activity).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainLayout, fragment, fragment.getClass().getSimpleName())
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .addToBackStack(null)
                .commit();
    }
}
