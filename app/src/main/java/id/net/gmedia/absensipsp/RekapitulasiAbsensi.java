package id.net.gmedia.absensipsp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RekapitulasiAbsensi extends Fragment {
    private Context context;
    private TextView tanggal1, tanggal2, tanggal3, tanggal4,
            harga1, harga2, harga3, harga4,
            textMulai, textAkhir;
    private ProgressDialog progressDialog;
    private MaterialCardView layoutData;
    private String awal, akhir;
    private SessionManager session;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(id.net.gmedia.absensipsp.R.layout.rekapitulasi_absensi, container, false);
        context = getContext();
        session = new SessionManager(context);
        tanggal1 = view.findViewById(id.net.gmedia.absensipsp.R.id.tanggal1RekapAbsensi);
        tanggal2 = view.findViewById(id.net.gmedia.absensipsp.R.id.tanggal2RekapAbsensi);
        tanggal3 = view.findViewById(id.net.gmedia.absensipsp.R.id.tanggal3RekapAbsensi);
        tanggal4 = view.findViewById(id.net.gmedia.absensipsp.R.id.tanggal4RekapAbsensi);
        harga1 = view.findViewById(id.net.gmedia.absensipsp.R.id.harga1RekapAbsensi);
        harga2 = view.findViewById(id.net.gmedia.absensipsp.R.id.harga2RekapAbsensi);
        harga3 = view.findViewById(id.net.gmedia.absensipsp.R.id.harga3RekapAbsensi);
        harga4 = view.findViewById(id.net.gmedia.absensipsp.R.id.harga4RekapAbsensi);
        RelativeLayout mulai = view.findViewById(id.net.gmedia.absensipsp.R.id.layoutPeriodeRekapAbsensi);
        RelativeLayout sampai = view.findViewById(id.net.gmedia.absensipsp.R.id.layoutSampaiRekapAbsensi);
        RelativeLayout proses = view.findViewById(id.net.gmedia.absensipsp.R.id.layoutProsesRekapAbsensi);
        layoutData = view.findViewById(id.net.gmedia.absensipsp.R.id.layoutdatarekapabsensi);
        textMulai = view.findViewById(id.net.gmedia.absensipsp.R.id.tglPeriodeRekapAbsensi);
        textAkhir = view.findViewById(id.net.gmedia.absensipsp.R.id.tglSampaiRekapAbsensi);
        RelativeLayout download = view.findViewById(id.net.gmedia.absensipsp.R.id.downloadPDF);
        mulai.setOnClickListener(new View.OnClickListener() {
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
                        textMulai.setText(sdFormat.format(customDate.getTime()));
                        awal = "" + textMulai.getText();
                        textMulai.setAlpha(1);

                    }
                };
                new DatePickerDialog(context, date, customDate.get(java.util.Calendar.YEAR), customDate.get(java.util.Calendar.MONTH), customDate.get(java.util.Calendar.DATE)).show();
            }
        });
        sampai.setOnClickListener(new View.OnClickListener() {
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
                        textAkhir.setText(sdFormat.format(customDate.getTime()));
                        akhir = "" + textAkhir.getText();
                        textAkhir.setAlpha(1);

                    }
                };
                new DatePickerDialog(context, date, customDate.get(java.util.Calendar.YEAR), customDate.get(java.util.Calendar.MONTH), customDate.get(java.util.Calendar.DATE)).show();
            }
        });
        proses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textMulai.getText().toString().equals("Tanggal Mulai")) {
                    Toast.makeText(context, "Silahkan Pilih Tanggal Mulai", Toast.LENGTH_LONG).show();
                } else if (textAkhir.getText().toString().equals("Tanggal Selesai")) {
                    Toast.makeText(context, "Silahkan Pilih Tanggal Akhir", Toast.LENGTH_LONG).show();
                } else {
                    prepareDataRekapAbsen();
                }

            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = Constant.urlDownloadPDFRekapAbsensi+"nip="+session.getNip()+"&tgl_awal="+awal+"&tgl_akhir="+akhir;
                new DownloadFileFromURL().execute(link, "DetailRekapAbsensi.pdf");
            }
        });
        return view;
    }

    private void prepareDataRekapAbsen() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(id.net.gmedia.absensipsp.R.layout.loading);

        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        JSONBuilder body = new JSONBuilder();
        body.add("startdate", awal);
        body.add("enddate", akhir);

        ApiVolleyManager.getInstance().addSecureRequest(getContext(), Constant.urlRekapAbsen, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(getContext()), body.create(), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            JSONObject response = object.getJSONObject("response");
                            if (status.equals("1")) {
                                tanggal1.setText(response.getString("range"));
                                tanggal2.setText(response.getString("range"));
                                tanggal3.setText(response.getString("range"));
                                tanggal4.setText(response.getString("range"));
                                harga1.setText(response.getString("jumlah_terlambat"));
                                harga2.setText(response.getString("jumlah_pulang_awal"));
                                harga3.setText(response.getString("tidak_absen_masuk"));
                                harga4.setText(response.getString("tidak_absen_pulang"));
                                layoutData.setVisibility(View.VISIBLE);
                                dialog.dismiss();
                            }
                        } catch (JSONException e) {
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
                        (Environment.DIRECTORY_DOCUMENTS) + File.separator + "Detail Rekap Absensi");
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
                        .setIcon(id.net.gmedia.absensipsp.R.mipmap.ic_launcher)
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
                        .setIcon(id.net.gmedia.absensipsp.R.mipmap.ic_launcher)
                        .setMessage(message)
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();
            }

            //delete file from server
            JSONObject jBody = new JSONObject();

            try {
                jBody.put("file_name", fileName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
