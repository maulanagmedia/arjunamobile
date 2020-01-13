package id.net.gmedia.absensipsp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONException;
import org.json.JSONObject;


public class ChangePSW extends Fragment {

    private Activity activity;
    private Boolean klikToVisiblePassLama = true;
    private Boolean klikToVisiblePassBaru = true;
    private Boolean klikToVisibleRePassBaru = true;
    private EditText passLama, passBaru, rePassBaru ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_change_psw, viewGroup, false);
        activity = getActivity();

        final ImageView visiblePassLama = view.findViewById(R.id.visiblePassLama);
        final ImageView visiblePassBaru = view.findViewById(R.id.visiblePassBaru);
        final ImageView visibleRePassBaru = view.findViewById(R.id.visibleRePassBaru);
        passLama = view.findViewById(id.net.gmedia.absensipsp.R.id.passLama);
        passBaru = view.findViewById(id.net.gmedia.absensipsp.R.id.passBaru);
        rePassBaru = view.findViewById(id.net.gmedia.absensipsp.R.id.reTypePassBaru);

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
       LinearLayout OK = view.findViewById(R.id.tombolOKgantiPassword);
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

            }
        });

        final LinearLayout cancel = view.findViewById(R.id.tombolcancelGantiPassword);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MainActivity.class);
                startActivity(intent);

            }
        });
        view.cancelLongPress();
        return view;
    }

    private void prepareDataGantiPassword() {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(id.net.gmedia.absensipsp.R.layout.loading);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        JSONBuilder body = new JSONBuilder();
        body.add("password_lama", passLama.getText().toString());
        body.add("password_baru", passBaru.getText().toString());
        body.add("re_password", rePassBaru.getText().toString());

        ApiVolleyManager.getInstance().addSecureRequest(activity, Constant.urlGantiPassword, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(activity), body.create(), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        dialog.dismiss();
                        try {
                            JSONObject object = new JSONObject(result);
                            String pesan = object.getJSONObject("metadata").getString("message");
                            String status = object.getJSONObject("metadata").getString("status");
                            if (status.equals("1")) {
                                Toast.makeText(activity.getApplicationContext(), pesan, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(activity.getApplicationContext(), pesan, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        dialog.dismiss();
                        Toast.makeText(activity.getApplicationContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);
                    }
                });
    }
}
