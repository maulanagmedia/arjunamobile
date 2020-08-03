package id.net.gmedia.absensipsp;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.FileDownloadManager;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.SimpleObjectModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.absensipsp.Adapter.SlipGajiAdapter;
import id.net.gmedia.absensipsp.Volley.ApiVolley;


public class SlipGaji extends Fragment {

    private Context context;
    private Activity activity;
    private ProgressDialog progressDialog;

    private AppCompatSpinner spn_bulan, spn_tahun;
    private Button btn_dwonload;
    private String message="";
    private SessionManager sessionManager;
    private String[] bulan = new String[]{"Pilih Bulan : ", "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"};

    private String[] tahun = new String[]
            {
                    "Pilih Tahun : ",
                    String.valueOf(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) - 1),
                    String.valueOf(java.util.Calendar.getInstance().get(Calendar.YEAR)),
            };

    private View layout_detail, layout_kosong;

    private HashMap<String, List<SimpleObjectModel>> detailGaji = new HashMap<>();
    private List<String> listHeader = new ArrayList<>();
    private SlipGajiAdapter adapter;

    public SlipGaji() {

    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        activity = getActivity();
        View v = inflater.inflate(R.layout.fragment_slip_gaji, container, false);

        sessionManager = new SessionManager(activity);

        spn_bulan =  v.findViewById(R.id.spn_bulan);
        spn_tahun = v.findViewById(R.id.spn_tahun);
        layout_detail = v.findViewById(R.id.layout_detail);
        layout_kosong = v.findViewById(R.id.layout_kosong);
        btn_dwonload = v.findViewById(R.id.btn_download);

        RecyclerView rv_gaji = v.findViewById(R.id.rv_gaji);
        rv_gaji.setItemAnimator(new DefaultItemAnimator());
        rv_gaji.setLayoutManager(new LinearLayoutManager(context));
        adapter = new SlipGajiAdapter(context, listHeader, detailGaji);
        rv_gaji.setAdapter(adapter);

        initSpinner();
        v.findViewById(R.id.btn_proses).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spn_bulan.getSelectedItemPosition() == 0){
                    Toast.makeText(context, "Pilih bulan terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else if(spn_tahun.getSelectedItemPosition() == 0){
                    Toast.makeText(context, "Pilih tahun terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else{
                    loadGaji();
                }
            }
        });

        btn_dwonload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* String link = Constant.urlDwonloadfile+"nip="+sessionManager.getNip()+"&bulan="+spn_bulan.getSelectedItemPosition()+"&tahun="+(spn_tahun.getSelectedItemPosition());
                *//*Intent intent = new Intent(activity, ActivityPDFLink.class);
                intent.putExtra("web", link);
                startActivity(intent);*//*
                downloadFile(link);
                new DownloadFileFromURL().execute(link);*/

                dwonloadPDF();

            }
        });

        return v;
    }

    private void downloadFile(String url){
        FileDownloadManager manager = new FileDownloadManager(activity);
        manager.download(url);
    }

    private void dwonloadPDF(){
        String parameter = String.format(Locale.getDefault(), "?nip=%s&bulan=%s&tahun=%s", sessionManager.getNip(), spn_bulan.getSelectedItemPosition(), spn_tahun.getSelectedItem());
        new ApiVolley(context, new JSONObject(), "GET", Constant.urlDwonloadfile+parameter, message, message, 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    String status = object.getJSONObject("metadata").getString("status");
                    message = object.getJSONObject("metadata" ).getString("message");

                    if (Integer.parseInt(status)==1){
                        JSONObject ob = object.getJSONObject("response");
                        String file = ob.getString("file");
                        downloadFile(file);
                        //Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }else{
                        //Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                Toast.makeText(context, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadGaji(){
        AppLoading.getInstance().showLoading(context);
        JSONBuilder body = new JSONBuilder();
        body.add("bulan", spn_bulan.getSelectedItemPosition());
        body.add("tahun", (String)spn_tahun.getSelectedItem());

        layout_kosong.setVisibility(View.GONE);
        layout_detail.setVisibility(View.GONE);

        ApiVolleyManager.getInstance().addSecureRequest(context, Constant.urlGaji, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(context), body.create(), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject response = new JSONObject(result);
                            int status = response.getJSONObject("metadata").getInt("status");
                            String message = response.getJSONObject("metadata").getString("message");

                            if(status == 1){
                                listHeader.clear();
                                detailGaji.clear();
                                btn_dwonload.setVisibility(View.VISIBLE);
                                JSONArray slip = response.getJSONArray("response");
                                if(slip.length() > 0){
                                    JSONObject d = slip.getJSONObject(0);

                                    String header = "Keterangan";
                                    listHeader.add(header);
                                    JSONArray arr = d.getJSONArray("header_gaji");
                                    List<SimpleObjectModel> s = new ArrayList<>();
                                    for(int i = 0; i < arr.length(); i++){
                                        s.add(new SimpleObjectModel(arr.getJSONObject(i).getString("label"),
                                                arr.getJSONObject(i).getString("value")));
                                    }
                                    detailGaji.put(header, s);

                                    header = "Nominal";
                                    listHeader.add(header);
                                    arr = d.getJSONArray("nominal");
                                    s = new ArrayList<>();
                                    for(int i = 0; i < arr.length(); i++){
                                        s.add(new SimpleObjectModel(arr.getJSONObject(i).getString("label"),
                                                arr.getJSONObject(i).getString("value")));
                                    }
                                    s.add(new SimpleObjectModel("Total Nominal",
                                            d.getJSONArray("total_nominal").getJSONObject(0).getString("value")));
                                    detailGaji.put(header, s);

                                    header = "Potongan";
                                    listHeader.add(header);
                                    arr = d.getJSONArray("potongan");
                                    s = new ArrayList<>();
                                    for(int i = 0; i < arr.length(); i++){
                                        s.add(new SimpleObjectModel(arr.getJSONObject(i).getString("label"),
                                                arr.getJSONObject(i).getString("value")));
                                    }
                                    s.add(new SimpleObjectModel("Total Potongan",
                                            d.getJSONArray("total_potongan").getJSONObject(0).getString("value")));
                                    detailGaji.put(header, s);

                                    String total = d.getJSONArray("total").getJSONObject(0).getString("value");
                                    adapter.setTotal(total);

                                    adapter.notifyDataSetChanged();
                                    layout_detail.setVisibility(View.VISIBLE);
                                }
                                else{
                                    layout_kosong.setVisibility(View.VISIBLE);
                                    btn_dwonload.setVisibility(View.GONE);
                                }
                            }
                            else{
                                btn_dwonload.setVisibility(View.GONE);
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e){
                            Log.e(Constant.TAG, e.getMessage());
                        }
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(context, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                        Log.e(Constant.TAG, result);

                        AppLoading.getInstance().stopLoading();
                    }
                });
    }

    private void initSpinner(){
        ArrayAdapter<String> adapter_bulan = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, bulan) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    spn_bulan.setAlpha(0.5f);
                    return false;
                } else {
                    spn_bulan.setAlpha(1);
                    return true;
                }
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(Color.BLACK);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.DKGRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        ArrayAdapter<String> adapter_tahun = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, tahun) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    spn_bulan.setAlpha(0.5f);
                    return false;
                } else {
                    spn_bulan.setAlpha(1);
                    return true;
                }
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(Color.BLACK);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.DKGRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        adapter_bulan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_tahun.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_bulan.setAdapter(adapter_bulan);
        spn_tahun.setAdapter(adapter_tahun);

        spn_bulan.setSelection(java.util.Calendar.getInstance().get(Calendar.MONTH) + 1);
        spn_tahun.setSelection(2);

        spn_bulan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private class DownloadFileFromURL extends AsyncTask<String, String, String> {
        String extension;
        boolean success;
        String fileName;
        String donwloadedFilePath;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                java.net.URL url = new java.net.URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();


                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                extension = f_url[0].substring(f_url[0].lastIndexOf("."));
                File vidDir = new File(Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_DOCUMENTS) + File.separator + "Slip Gaji");
                vidDir.mkdirs();

                // create unique identifier
                Date date = new Date();
                // create file name
                //fileName = "mutasi_" + date.getYear()+date.getMonth()+date.getDate()+date.getHours()+date.getMinutes()+date.getSeconds()+extension;
                fileName = f_url[1];

                File fileDownload = new File(vidDir.getAbsolutePath(), fileName);
                donwloadedFilePath = fileDownload.getAbsolutePath();
                success = false;
                InputStream input = new BufferedInputStream(url.openStream());
                // Output stream
                OutputStream output = new FileOutputStream(fileDownload);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                    success = true;
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {

            dismissDialog();

            //String downloadedPath = "file://" + Environment.getExternalStorageDirectory().toString() + "/downloadedfile."+extension;

            showFinishDialog(success);
        }

        private void showDialog() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Downloading file. Please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
        }

        private void dismissDialog() {
            progressDialog.dismiss();
        }

        private void showFinishDialog(final boolean downloadSuccess) {
            String title, message;
            if (downloadSuccess) {

                title = "Download selesai";
                message = "File telah terdownload " + donwloadedFilePath;

                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle(title)
                        .setIcon(R.drawable.icon)
                        .setMessage(message)
                        .setPositiveButton("Buka", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String path = donwloadedFilePath;
                                File targetFile = new File(path);
                                Uri targetUri = FileProvider.getUriForFile(context,
                                        context.getApplicationContext().getPackageName()+".provider",targetFile);

                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(targetUri, "application/pdf");
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                try {
                                    startActivity(intent);
                                } catch (Exception e) {
//                                    Toast.makeText(context,"tidak ada aplikasi",Toast.LENGTH_LONG).show();
                                    final Intent intent2 = new Intent(context, PDFReader.class);
                                    intent2.putExtra(PDFViewerActivities.EXTRA_PDFFILENAME, path);
                                    try {
                                        startActivity(intent2);
                                    } catch (Exception e2) {
                                        e2.printStackTrace();
                                    }
                                    /*try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.microsoft.office.excel")));
                                    } catch (Exception el) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.microsoft.office.excel")));
                                    }*/
                                }
                            }
                        })
                        .setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                builder.show();
            } else {

                title = "Download terganggu";
                message = "Tidak dapat mengunduh file, silahkan coba kembali";

                final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle(title)
                        .setIcon(R.drawable.icon)
                        .setMessage(message)
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();
            }

            //delete file from server
            /*JSONObject jBody = new JSONObject();

            try {
                jBody.put("file_name", fileName);
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        }
    }
}
