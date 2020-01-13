package id.net.gmedia.absensipsp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fxn.pix.Pix;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.DialogFactory;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Bayu on 20/02/2018.
 */

public class Ijin extends Fragment implements AdapterView.OnItemSelectedListener {
    private Activity activity;
    private Spinner dropdownMenu;
    private View layoutTimePixer, layoutKeluarKantor, layouttglmulaiijin, layoutUploadFile;
    private TextView textTimePixer, texttglmulaiijin, textKeluarKantor, txt_file;

    private EditText keterangan;
    private int posisiDropdown;

    private String upload_file = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(id.net.gmedia.absensipsp.R.layout.ijin, viewGroup, false);
        activity = getActivity();

        RelativeLayout kirim;
        RelativeLayout layoutijin = view.findViewById(id.net.gmedia.absensipsp.R.id.layoutijin);
        layoutTimePixer = view.findViewById(id.net.gmedia.absensipsp.R.id.layoutTimePixer);
        layoutKeluarKantor = view.findViewById(R.id.layoutKeluarKantor);
        textKeluarKantor = view.findViewById(R.id.textKeluarKantor);
        layoutUploadFile = view.findViewById(R.id.layoutUploadFile);
        txt_file = view.findViewById(R.id.txt_file);
        /*layoutJamMulaiKeluarKantor = view.findViewById(R.id.layoutInputMulaiKeluarKantor);
        textJamMulaiKeluarKantor = view.findViewById(R.id.textMulai);
        layoutJamSelesaiKeluarKantor = view.findViewById(R.id.layoutInputSelesaiKeluarKantor);
        textJamSelesaiKeluarKantor = view.findViewById(R.id.textSelesai);*/
        textTimePixer = view.findViewById(id.net.gmedia.absensipsp.R.id.textTimePixer);
        kirim = view.findViewById(id.net.gmedia.absensipsp.R.id.layoutkirimhistoryijin);
        layouttglmulaiijin = view.findViewById(id.net.gmedia.absensipsp.R.id.layouttglmulaiijin);
        texttglmulaiijin = view.findViewById(id.net.gmedia.absensipsp.R.id.texttglmulaiijin);
        keterangan = view.findViewById(id.net.gmedia.absensipsp.R.id.edit_text_keterangan_historyijin);
        dropdownMenu = view.findViewById(R.id.menuDropdownJenisIjin);

        layoutijin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() != null){
                    HideKeyboard.hideSoftKeyboard(getActivity());
                }
            }
        });

        layoutUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pix.start((FragmentActivity) activity, ((MainActivity)activity).UPLOAD_CODE, 1);
            }
        });

        dropdownMenu.setOnItemSelectedListener(this);
        String[] items = new String[]{"Jenis Ijin: ", "Ijin Pulang Awal", "Ijin Meninggalkan Kerja"};
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, items) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    dropdownMenu.setAlpha(0.5f);
                    return false;
                } else {
                    dropdownMenu.setAlpha(1);
                    return true;
                }
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tv.setTextColor(Color.BLACK);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.DKGRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownMenu.setAdapter(adapter);
        layouttglmulaiijin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final java.util.Calendar customDate = java.util.Calendar.getInstance();
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        customDate.set(java.util.Calendar.YEAR, year);
                        customDate.set(java.util.Calendar.MONTH, month);
                        customDate.set(java.util.Calendar.DATE, dayOfMonth);
                        SimpleDateFormat sdFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                        texttglmulaiijin.setText(sdFormat.format(customDate.getTime()));

                    }
                };
                new DatePickerDialog(activity, date, customDate.get(java.util.Calendar.YEAR),
                        customDate.get(java.util.Calendar.MONTH), customDate.get(java.util.Calendar.DATE)).show();
            }
        });

        layoutTimePixer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                java.util.Calendar mcurrentTime = java.util.Calendar.getInstance();
                int hour = mcurrentTime.get(java.util.Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(java.util.Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String jam = String.valueOf(selectedHour);
                        String menit = String.valueOf(selectedMinute);
                        jam = jam.length() < 2 ? "0" + jam : jam;
                        menit = menit.length() < 2 ? "0" + menit : menit;
                        String waktu = jam + ":" + menit;
                        textTimePixer.setText(waktu);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        layoutKeluarKantor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = DialogFactory.getInstance().createDialog(activity,
                        R.layout.popup_jumlahmenit_keluarkantor, 80, 40);
                final EditText txt_input = dialog.findViewById(R.id.txt_input);
                Button btn_proses = dialog.findViewById(R.id.btn_proses);

                btn_proses.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(txt_input.getText().toString().isEmpty()){
                            Toast.makeText(activity, "Input jumlah menit", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            textKeluarKantor.setText(txt_input.getText().toString());
                            dialog.dismiss();
                        }
                    }
                });

                dialog.show();
            }
        });

        /*layoutJamMulaiKeluarKantor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                java.util.Calendar mcurrentTime = java.util.Calendar.getInstance();
                int hour = mcurrentTime.get(java.util.Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(java.util.Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String jam = String.valueOf(selectedHour);
                        String menit = String.valueOf(selectedMinute);
                        jam = jam.length() < 2 ? "0" + jam : jam;
                        menit = menit.length() < 2 ? "0" + menit : menit;
                        String waktu = jam + ":" + menit;
                        textJamMulaiKeluarKantor.setText(waktu);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        layoutJamSelesaiKeluarKantor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                java.util.Calendar mcurrentTime = java.util.Calendar.getInstance();
                int hour = mcurrentTime.get(java.util.Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(java.util.Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String jam = String.valueOf(selectedHour);
                        String menit = String.valueOf(selectedMinute);
                        jam = jam.length() < 2 ? "0" + jam : jam;
                        menit = menit.length() < 2 ? "0" + menit : menit;
                        String waktu = jam + ":" + menit;
                        textJamSelesaiKeluarKantor.setText(waktu);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });*/

        //loadApproval();
        final TextView ijin = view.findViewById(id.net.gmedia.absensipsp.R.id.ijin);
        ijin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, HistoryIjin.class);
                startActivity(i);
            }
        });

        kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (posisiDropdown == 0) {
                    Toast.makeText(activity, "Silahkan Pilih Jenis Ijin Anda", Toast.LENGTH_LONG).show();
                }
                if (posisiDropdown == 1) {
                    if (texttglmulaiijin.getText().toString().equals("Tanggal Mulai")) {
                        Toast.makeText(activity, "Silahkan Isi Tanggal Ijin Anda", Toast.LENGTH_LONG).show();
                    } /*else if (posisiDropdownApproval.equals("0")) {
                        Toast.makeText(activity, "Silahkan Pilih Siapa Approval Anda", Toast.LENGTH_LONG).show();
                    }*/ else if (textTimePixer.getText().toString().equals("Jam")) {
                        Toast.makeText(activity, "Silahkan Isi Jam Ijin Anda", Toast.LENGTH_LONG).show();
                    } else if (keterangan.getText().toString().isEmpty()) {
                        Toast.makeText(activity, "Silahkan Isi Keterangan Ijin Anda", Toast.LENGTH_LONG).show();
                    } else {
                        /*dropdownApproval.setSelection(1);
                        posisiDropdownApproval = String.valueOf(dropdownApproval.getSelectedItemPosition());*/

                        final Dialog dialog = new Dialog(activity);
                        dialog.setContentView(R.layout.popup_konfirmasi);

                        TextView txt_konfirmasi = dialog.findViewById(R.id.txt_konfirmasi);
                        txt_konfirmasi.setText("Yakin ingin mengajukan ijin?");

                        Button ya = dialog.findViewById(id.net.gmedia.absensipsp.R.id.yacheckout);
                        ya.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                prepareDataPulangAwal();
                            }
                        });
                        Button tidak = dialog.findViewById(id.net.gmedia.absensipsp.R.id.tidakcheckout);
                        tidak.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        if(dialog.getWindow() != null){
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        }
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                        Window window = dialog.getWindow();
                        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    }
                } else if (posisiDropdown == 2) {
                    if (texttglmulaiijin.getText().toString().equals("Tanggal Mulai")) {
                        Toast.makeText(activity, "Silahkan Isi Tanggal Ijin Anda", Toast.LENGTH_LONG).show();
                    } /*else if (posisiDropdownApproval.equals("0")) {
                        Toast.makeText(activity, "Silahkan Pilih Siapa Approval Anda", Toast.LENGTH_LONG).show();
                    }*/ /*else if (isianKeluarKantor.getText().toString().isEmpty()) {
                        Toast.makeText(activity, "Silahkan Isi Lama Ijin Anda", Toast.LENGTH_LONG).show();
                    } else if (textJamMulaiKeluarKantor.getText().toString().equals("Jam Mulai")) {
                        Toast.makeText(activity, "Silahkan Isi Jam Mulai Anda", Toast.LENGTH_LONG).show();
                    } else if (textJamSelesaiKeluarKantor.getText().toString().equals("Jam Selesai")) {
                        Toast.makeText(activity, "Silahkan Isi Jam Selesai Anda", Toast.LENGTH_LONG).show();*/
                    else if (textKeluarKantor.getText().toString().equals("Jumlah Menit")) {
                            Toast.makeText(activity, "Silahkan Isi Jumlah menit Anda", Toast.LENGTH_LONG).show();
                    } else if (keterangan.getText().toString().isEmpty()) {
                        Toast.makeText(activity, "Silahkan Isi Keterangan Ijin Anda", Toast.LENGTH_LONG).show();
                    } else {
                        /*dropdownApproval.setSelection(1);
                        posisiDropdownApproval = String.valueOf(dropdownApproval.getSelectedItemPosition());*/
                        final Dialog dialog = new Dialog(activity);
                        dialog.setContentView(R.layout.popup_konfirmasi);

                        TextView txt_konfirmasi = dialog.findViewById(R.id.txt_konfirmasi);
                        txt_konfirmasi.setText("Yakin ingin mengajukan ijin?");

                        Button ya = dialog.findViewById(id.net.gmedia.absensipsp.R.id.yacheckout);
                        ya.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                prepareDataKeluarKantor();
                            }
                        });
                        Button tidak = dialog.findViewById(id.net.gmedia.absensipsp.R.id.tidakcheckout);
                        tidak.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        if(dialog.getWindow() != null){
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        }
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                        Window window = dialog.getWindow();
                        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    }
                }
            }
        });
        return view;
    }

    private void prepareDataKeluarKantor() {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(id.net.gmedia.absensipsp.R.layout.loading);
        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        JSONBuilder body = new JSONBuilder();
        body.add("tanggal", texttglmulaiijin.getText().toString());
        /*body.add("dari", textJamMulaiKeluarKantor.getText().toString());
        body.add("sampai", textJamSelesaiKeluarKantor.getText().toString());*/
        body.add("jml_menit", textKeluarKantor.getText().toString());
        body.add("keterangan", keterangan.getText().toString());
        //body.add("approval", posisiDropdownApproval);
        Log.d(Constant.TAG, body.create().toString());

        ApiVolleyManager.getInstance().addSecureRequest(getContext(), Constant.urlIjinKeluarKantor, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(getContext()), body.create(), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        dialog.dismiss();
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            String message = object.getJSONObject("metadata").getString("message");
                            if (status.equals("1")) {
                                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(activity, MainActivity.class);
                                activity.startActivity(intent);
                                activity.finish();
                            }
                        } catch (JSONException e) {
                            dialog.dismiss();

                            Toast.makeText(getContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                            Log.e(Constant.TAG, e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        dialog.dismiss();

                        Toast.makeText(getContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);
                    }
                });
    }

    private void prepareDataPulangAwal() {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(id.net.gmedia.absensipsp.R.layout.loading);
        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        JSONBuilder body = new JSONBuilder();
        body.add("tanggal", texttglmulaiijin.getText().toString());
        body.add("jam", textTimePixer.getText().toString());
        body.add("keterangan", keterangan.getText().toString());
        body.add("file", upload_file);
        Log.d(Constant.TAG, body.create().toString());

        ApiVolleyManager.getInstance().addSecureRequest(getContext(), Constant.urlIjinPulangAwal, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(getContext()), body.create(), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        dialog.dismiss();
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            String message = object.getJSONObject("metadata").getString("message");
                            if (status.equals("1")) {
                                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(activity, MainActivity.class);
                                activity.startActivity(intent);
                                activity.finish();
                            }
                        } catch (JSONException e) {
                            dialog.dismiss();

                            Toast.makeText(getContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);
                    }
                });
    }

    void prepareBitmap(String path, Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();

        txt_file.setText(path);

        upload_file = Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        posisiDropdown = parent.getSelectedItemPosition();
        if (position == 1) {
            layoutTimePixer.setVisibility(View.VISIBLE);
            layouttglmulaiijin.setVisibility(View.VISIBLE);
            layoutUploadFile.setVisibility(View.VISIBLE);
            layoutKeluarKantor.setVisibility(View.GONE);
            /*layoutJamMulaiKeluarKantor.setVisibility(View.GONE);
            layoutJamSelesaiKeluarKantor.setVisibility(View.GONE);*/
            textTimePixer.setText("Jam");
        } else if (position == 2) {
            /*layoutJamMulaiKeluarKantor.setVisibility(View.VISIBLE);
            layoutJamSelesaiKeluarKantor.setVisibility(View.VISIBLE);*/
            layouttglmulaiijin.setVisibility(View.VISIBLE);
            layoutKeluarKantor.setVisibility(View.VISIBLE);
            layoutUploadFile.setVisibility(View.GONE);
            layoutTimePixer.setVisibility(View.GONE);
            textKeluarKantor.setText("Jumlah Menit");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /*private void loadApproval(){
        ApiVolleyManager.getInstance().addSecureRequest(activity, Constant.urlMasterApproval, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(activity), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        approval = new ArrayList<>();
                        approval.add(new CustomMasterApproval("0", "Pilih Approval: "));
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            if (status.equals("1")) {
                                JSONArray response = object.getJSONArray("response");
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject isi = response.getJSONObject(i);
                                    approval.add(new CustomMasterApproval(
                                            isi.getString("kode"),
                                            isi.getString("nama")
                                    ));
                                }
                                arrayAdapterMasterApproval = new ArrayAdapter<CustomMasterApproval>(activity, android.R.layout.simple_spinner_item, approval) {
                                    @Override
                                    public boolean isEnabled(int position) {
                                        if (position == 0) {
                                            // Disable the first item from Spinner
                                            // First item will be use for hint
                                            dropdownApproval.setAlpha(0.5f);
                                            return false;
                                        } else {
                                            dropdownApproval.setAlpha(1);
                                            return true;
                                        }
                                    }

                                    @Override
                                    public View getDropDownView(int position, View convertView,
                                                                @NonNull ViewGroup parent) {

                                        View view = super.getDropDownView(position, convertView, parent);
                                        TextView tv = (TextView) view;
                                        if (position == 0) {
                                            // Set the hint text color gray
                                            tv.setTextColor(Color.BLACK);
                                        } else {
                                            tv.setTextColor(Color.BLACK);
                                        }
                                        return view;
                                    }
                                };
                                dropdownApproval.setAdapter(arrayAdapterMasterApproval);
                                dropdownApproval.setSelection(1);
                                dropdownApproval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        custom = approval.get(position);
                                        posisiDropdownApproval = custom.getKode();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(activity, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                        Log.e(Constant.TAG, result);
                    }
                });
    }*/
}
