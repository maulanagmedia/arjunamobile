package id.net.gmedia.absensipsp;

import android.app.DatePickerDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.JSONBuilder;

import id.net.gmedia.absensipsp.Model.CustomMasterApproval;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EditCuti extends AppCompatActivity {
    private TextView awal, akhir;
    private EditText alasan;
    private String isiID = "";
    private String isiAwal = "";
    private String isiAkhir = "";
    private String isiAlasan = "";
    private String posisiDropdown = "";
    private List<CustomMasterApproval> approval;
    private ArrayAdapter<CustomMasterApproval> adapterMasterApproval;
    private CustomMasterApproval custom;
    private Spinner dropdownApproval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(id.net.gmedia.absensipsp.R.layout.edit_cuti);

        RelativeLayout btnSave, tglMulai, tglSelesai, utama;
        LinearLayout back = findViewById(id.net.gmedia.absensipsp.R.id.backEditCuti);
        awal = findViewById(id.net.gmedia.absensipsp.R.id.texttglmulaiEditCuti);
        akhir = findViewById(id.net.gmedia.absensipsp.R.id.texttglselesaiEditCuti);
        alasan = findViewById(id.net.gmedia.absensipsp.R.id.edit_text_keterangan_edit_cuti);
        btnSave =  findViewById(id.net.gmedia.absensipsp.R.id.layoutKirimEditCuti);
        tglMulai = findViewById(id.net.gmedia.absensipsp.R.id.layouttglmulaiEditCuti);
        tglSelesai = findViewById(id.net.gmedia.absensipsp.R.id.layouttglselesaiEditCuti);
        utama = findViewById(id.net.gmedia.absensipsp.R.id.layoutEditCuti);
        dropdownApproval = findViewById(R.id.menuDropdownEditCuti);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Bundle save = getIntent().getExtras();
        if (save != null) {
            isiID = save.getString("id", "");
            isiAwal = save.getString("awal", "");
            isiAkhir = save.getString("akhir", "");
            isiAlasan = save.getString("alasan", "");
            posisiDropdown = save.getString("approval", "");
        }
        awal.setText(isiAwal);
        akhir.setText(isiAkhir);
        alasan.setText(isiAlasan);
        tglMulai.setOnClickListener(new View.OnClickListener() {
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
                        awal.setText(sdFormat.format(customDate.getTime()));

                    }
                };
                new DatePickerDialog(EditCuti.this, date, customDate.get(java.util.Calendar.YEAR), customDate.get(java.util.Calendar.MONTH), customDate.get(java.util.Calendar.DATE)).show();
            }
        });
        tglSelesai.setOnClickListener(new View.OnClickListener() {
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
                        akhir.setText(sdFormat.format(customDate.getTime()));

                    }
                };
                new DatePickerDialog(EditCuti.this, date, customDate.get(java.util.Calendar.YEAR), customDate.get(java.util.Calendar.MONTH), customDate.get(java.util.Calendar.DATE)).show();
            }
        });

        //loadApproval();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (posisiDropdown.equals("0")) {
                    Toast.makeText(getApplicationContext(), "Silahkan Pilih Siapa Approval Anda", Toast.LENGTH_LONG).show();
                    return;
                }

                JSONBuilder body = new JSONBuilder();
                body.add("id", isiID);
                body.add("startdate", awal.getText().toString());
                body.add("enddate", akhir.getText().toString());
                body.add("approval", posisiDropdown);
                body.add("keterangan", alasan.getText().toString());

                ApiVolleyManager.getInstance().addSecureRequest(EditCuti.this, Constant.urlCuti, ApiVolleyManager.METHOD_POST,
                        Constant.getTokenHeader(EditCuti.this), body.create(), new ApiVolleyManager.RequestCallback() {
                            @Override
                            public void onSuccess(String result) {
                                try {
                                    JSONObject object = new JSONObject(result);
                                    String status = object.getJSONObject("metadata").getString("status");
                                    String pesan = object.getJSONObject("metadata").getString("message");
                                    if (status.equals("1")) {
                                        Toast.makeText(EditCuti.this, pesan, Toast.LENGTH_LONG).show();
                                        onBackPressed();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.e(Constant.TAG, e.getMessage());
                                }
                            }

                            @Override
                            public void onError(String result) {
                                Toast.makeText(EditCuti.this, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                                Log.e(Constant.TAG, result);
                            }
                        });
            }
        });
        utama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideKeyboard.hideSoftKeyboard(EditCuti.this);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditCuti.this, HistoryCuti.class);
        startActivity(intent);
        finish();
    }

    private void loadApproval(){
        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.urlMasterApproval, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(this), new ApiVolleyManager.RequestCallback() {
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
                                adapterMasterApproval = new ArrayAdapter<CustomMasterApproval>(EditCuti.this, android.R.layout.simple_spinner_item, approval) {
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
                                };
                                dropdownApproval.setAdapter(adapterMasterApproval);
                                int position = 0;
                                for (int i = 0; i < approval.size(); i++) {
                                    if (approval.get(i).getKode().equals(posisiDropdown)) {
                                        position = i;
                                        break;
                                    }
                                }
                                dropdownApproval.setSelection(position);
                                dropdownApproval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        custom = approval.get(position);
                                        posisiDropdown = String.valueOf(custom.getKode());
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            }
                        } catch (JSONException e) {
                            Toast.makeText(EditCuti.this, "Terjadi kesalahan data", Toast.LENGTH_SHORT).show();

                            e.printStackTrace();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(EditCuti.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                        Log.e(Constant.TAG, result);
                    }
                });
    }
}
