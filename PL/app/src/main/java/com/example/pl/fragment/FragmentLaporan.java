package com.example.pl.fragment;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.pl.R;
import com.example.pl.activity.ActivityHome;
import com.example.pl.adapter.DataPegawaiAdapter;
import com.example.pl.adapter.ListPengajuanAdapter;
import com.example.pl.adapter.RecyclerItemClickListener;
import com.example.pl.model.DataPengajuanModel;
import com.example.pl.response.ListPengajuanResponse;
import com.example.pl.retrofit.RetrofitClient;
import com.example.pl.retrofit.RetrofitInterface;
import com.example.pl.utils.DetectConnection;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLaporan extends Fragment {
    ListPengajuanAdapter listPengajuanAdapter = new ListPengajuanAdapter();
    DetectConnection detectConnection;
    RecyclerView rvLaporan;
    int cek_hrd = 0;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    SwipeRefreshLayout refreshLaporan;
    ProgressDialog progressDialog;

    public FragmentLaporan() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_laporan, container, false);
        pref = getActivity().getApplicationContext().getSharedPreferences("data", 0); // 0 - for private mode
        editor = pref.edit();
        refreshLaporan = (SwipeRefreshLayout)view.findViewById(R.id.refreshLaporan);
        rvLaporan = (RecyclerView)view.findViewById(R.id.rvLaporan);
        detectConnection = new DetectConnection(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Harap Tunggu...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        rvLaporan.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        rvLaporan.setAdapter(listPengajuanAdapter);

        refreshLaporan.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLaporan.setRefreshing(true);
                if (cek_hrd == 0)
                    getAllConfirmed();
                else
                    getSingleConfirmed(pref.getString("nip", null));
                refreshLaporan.setRefreshing(false);
            }
        });

        refreshLaporan.post(new Runnable() {
            @Override
            public void run() {
                refreshLaporan.setRefreshing(true);
                if (cek_hrd == 0)
                    getAllConfirmed();
                else
                    getSingleConfirmed(pref.getString("nip", null));
                refreshLaporan.setRefreshing(false);
            }
        });

        if(!"HRD".equalsIgnoreCase(pref.getString("level", null))){
            getSingleConfirmed(pref.getString("nip", null));
            cek_hrd = 1;
        } else {
            getAllConfirmed();
        }

        rvLaporan.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                final String id_pengajuan = ((MaterialTextView)rvLaporan.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.list_pengajuan_id_pengajuan)).getText().toString(),
                        nama_divisi = ((MaterialTextView)rvLaporan.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.list_pengajuan_nama_divisi)).getText().toString(),
                        nama_pm = ((MaterialTextView)rvLaporan.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.list_pengajuan_nama_pm)).getText().toString(),
                        nip = ((MaterialTextView)rvLaporan.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.list_pengajuan_nip)).getText().toString(),
                        nama = ((MaterialTextView)rvLaporan.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.list_pengajuan_nama_pegawai)).getText().toString(),
                        hari = ((MaterialTextView)rvLaporan.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.list_pengajuan_hari)).getText().toString(),
                        tanggal = ((MaterialTextView)rvLaporan.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.list_pengajuan_tanggal)).getText().toString(),
                        jam_mulai = ((MaterialTextView)rvLaporan.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.list_pengajuan_jam_mulai)).getText().toString(),
                        jam_selesai = ((MaterialTextView)rvLaporan.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.list_pengajuan_jam_selesai)).getText().toString(),
                        keterangan = ((MaterialTextView)rvLaporan.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.list_pengajuan_keterangan)).getText().toString(),
                        status = ((MaterialTextView)rvLaporan.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.list_pengajuan_status)).getText().toString(),
                        gaji = ((MaterialTextView)rvLaporan.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.list_gaji)).getText().toString();

                System.out.println("INIIIIIIIIIIIIIIII IH :" + nama_divisi + " " + nama_pm + " " + id_pengajuan);
                LayoutInflater inflater =getLayoutInflater();
                final View viewDetail = inflater.inflate(R.layout.custom_alert_detail_single_pengajuan, null);
                final MaterialTextView tvNip = (MaterialTextView)viewDetail.findViewById(R.id.detail_list_pengajuan_nip),
                        tvNama = (MaterialTextView)viewDetail.findViewById(R.id.detail_list_pengajuan_nama),
                        tvDivisi = (MaterialTextView)viewDetail.findViewById(R.id.detail_list_pengajuan_divisi),
                        tvHariTanggal = (MaterialTextView)viewDetail.findViewById(R.id.detail_list_pengajuan_hari_tanggal),
                        tvJamMulai = (MaterialTextView)viewDetail.findViewById(R.id.detail_list_pengajuan_jam_mulai),
                        tvJamSelesai = (MaterialTextView)viewDetail.findViewById(R.id.detail_list_pengajuan_jam_selesai),
                        tvLeader = (MaterialTextView)viewDetail.findViewById(R.id.detail_list_pengajuan_leader),
                        tvKeterangan = (MaterialTextView)viewDetail.findViewById(R.id.detail_list_pengajuan_keterangan),
                        tvStatus = (MaterialTextView)viewDetail.findViewById(R.id.detail_list_pengajuan_status),
                        tvGaji = (MaterialTextView)viewDetail.findViewById(R.id.detail_list_gaji);

                tvNip.setText(nip);
                tvNama.setText(nama);
                tvHariTanggal.setText(hari + ", " + tanggal);
                tvJamMulai.setText(jam_mulai);
                tvJamSelesai.setText(jam_selesai);
                tvDivisi.setText(nama_divisi);
                tvLeader.setText(nama_pm);
                tvKeterangan.setText(keterangan);
                tvStatus.setText(status);
                Locale localeID = new Locale("in", "ID");
                NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
                tvGaji.setText(formatRupiah.format(Integer.parseInt(gaji)));
                MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                        .setView(viewDetail);
                alert.setPositiveButton("CETAK", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(status.equals("Ditolak")){
                            dialog.dismiss();
                            new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                    .setMessage("Tidak bisa mencetak data lembur yang ditolak")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                        } else {
                            progressDialog.show();
                            createPdf(nama, nama_divisi, hari, tanggal, jam_mulai, jam_selesai, nama_pm, keterangan);
                            dialog.dismiss();
                        }
                    }
                })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        }));

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if (cek_hrd == 0)
            inflater.inflate(R.menu.toolbar_cetak_perbulan, menu);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(cek_hrd == 0)
            setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.toolbar_cetak:
                LayoutInflater inflater =getLayoutInflater();
                final View viewBulan = inflater.inflate(R.layout.custom_alert_hrd, null);
                final AutoCompleteTextView pilih_bulan = (AutoCompleteTextView)viewBulan.findViewById(R.id.pilih_bulan_untuk_cetak_xml);
                String[] isiBulan = new String[]{"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
                ArrayAdapter<String> adapterBulan = new ArrayAdapter<>(getActivity(), R.layout.dropdown_data_pegawai_input_divisi, R.id.haeya, isiBulan);
                adapterBulan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                pilih_bulan.setAdapter(adapterBulan);
                new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                        .setView(viewBulan)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String bulan = null;
                                switch (pilih_bulan.getText().toString()){
                                    case "Januari": bulan = "1"; break;
                                    case "Februari": bulan = "2"; break;
                                    case "Maret": bulan = "3"; break;
                                    case "April": bulan = "4"; break;
                                    case "Mei": bulan = "5"; break;
                                    case "Juni": bulan = "6"; break;
                                    case "Juli": bulan = "7"; break;
                                    case "Agustus": bulan = "8"; break;
                                    case "September": bulan = "9"; break;
                                    case "Oktober": bulan = "10"; break;
                                    case "November": bulan = "11"; break;
                                    case "Desember": bulan = "12"; break;
                                }
                                getDetailLaporanPerbulan(bulan, pilih_bulan.getText().toString());
                                Toast.makeText(getActivity().getApplicationContext(), "Bulan ke-" + bulan, Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                Toast.makeText(getActivity().getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

    private void getDetailLaporanPerbulan(String bulan, final String nama_bulan) {
        progressDialog.show();
        if(detectConnection.isConnectingToInternet()){
            Retrofit retrofit = new Retrofit.Builder().baseUrl(RetrofitClient.BASE_URL).addConverterFactory(ScalarsConverterFactory.create()).build();
            RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
            Call<String> call = retrofitInterface.getDataPerbulan("pengajuandikonfirmasi_perbulan", bulan);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()){
                        if (response.body() != null) {
                            //E X C E L
                            Workbook wb = new HSSFWorkbook();
                            Cell c = null;

                            //Cell style for header row
                            CellStyle cs = wb.createCellStyle();
                            cs.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
                            cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                            cs.setAlignment(CellStyle.ALIGN_CENTER);
                            cs.setBorderBottom(HSSFCellStyle.BORDER_THIN);
                            cs.setBorderTop(HSSFCellStyle.BORDER_THIN);
                            cs.setBorderRight(HSSFCellStyle.BORDER_THIN);
                            cs.setBorderLeft(HSSFCellStyle.BORDER_THIN);
                            HSSFFont font = ((HSSFWorkbook) wb).createFont();
                            font.setColor(IndexedColors.WHITE.getIndex());
                            font.setBoldweight(Font.BOLDWEIGHT_BOLD);
                            cs.setFont(font);

                            //New Sheet
                            Sheet sheet1 = null;
                            sheet1 = wb.createSheet("Bulan " + nama_bulan);

                            //Generate column headings
                            Row row = sheet1.createRow(0);

                            c = row.createCell(0);
                            c.setCellValue("Nama");
                            c.setCellStyle(cs);

                            c = row.createCell(1);
                            c.setCellValue("Jam Masuk");
                            c.setCellStyle(cs);

                            c = row.createCell(2);
                            c.setCellValue("Jam Keluar");
                            c.setCellStyle(cs);

                            c = row.createCell(3);
                            c.setCellValue("Durasi");
                            c.setCellStyle(cs);

                            sheet1.setColumnWidth(0, (15 * 400));
                            sheet1.setColumnWidth(1, (15 * 450));
                            sheet1.setColumnWidth(2, (15 * 450));
                            sheet1.setColumnWidth(3, (15 * 400));

                            String jsonReponse = response.body().toString();
                            try {
                                JSONObject jsonObject = new JSONObject(jsonReponse);
                                if (jsonObject.optString("status").equals("ok")) {
                                    ArrayList<DataPengajuanModel> dataPengajuanModelArrayList = new ArrayList<>();
                                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        DataPengajuanModel dataPengajuanModel = new DataPengajuanModel();
                                        JSONObject dataObj = jsonArray.getJSONObject(i);

                                        dataPengajuanModel.setId_pengajuan(dataObj.getString("id_pengajuan"));
                                        dataPengajuanModel.setNip(dataObj.getString("nip"));
                                        dataPengajuanModel.setNama(dataObj.getString("nama"));
                                        dataPengajuanModel.setNama_divisi(dataObj.getString("nama_divisi"));
                                        dataPengajuanModel.setHari(dataObj.getString("hari"));
                                        dataPengajuanModel.setTanggal(dataObj.getString("tanggal"));
                                        dataPengajuanModel.setJam_mulai(dataObj.getString("jam_mulai"));
                                        dataPengajuanModel.setJam_selesai(dataObj.getString("jam_selesai"));
                                        dataPengajuanModel.setNama_pm(dataObj.getString("nama_pm"));
                                        dataPengajuanModel.setKeterangan(dataObj.getString("keterangan"));
                                        dataPengajuanModel.setStatus(dataObj.getString("status"));
                                        dataPengajuanModel.setEstimasi_jam(dataObj.getString("estimasi_jam"));
                                        dataPengajuanModel.setTanggal_selesai(dataObj.getString("tanggal_selesai"));

                                        dataPengajuanModelArrayList.add(dataPengajuanModel);
                                        CellStyle cs1 = wb.createCellStyle();
                                        cs1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
                                        cs1.setBorderTop(HSSFCellStyle.BORDER_THIN);
                                        cs1.setBorderRight(HSSFCellStyle.BORDER_THIN);
                                        cs1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
                                        row = sheet1.createRow(i+1);
                                        c = row.createCell(0);
                                        c.setCellValue(dataPengajuanModelArrayList.get(i).getNama());
                                        c.setCellStyle(cs1);
                                        c = row.createCell(1);
                                        c.setCellValue(dataPengajuanModelArrayList.get(i).getTanggal() + " " + dataPengajuanModelArrayList.get(i).getJam_mulai());
                                        c.setCellStyle(cs1);
                                        c = row.createCell(2);
                                        c.setCellValue(dataPengajuanModelArrayList.get(i).getTanggal_selesai() + " " + dataPengajuanModelArrayList.get(i).getJam_selesai());
                                        c.setCellStyle(cs1);
                                        c = row.createCell(3);
                                        c.setCellValue(dataPengajuanModelArrayList.get(i).getEstimasi_jam());
                                        c.setCellStyle(cs1);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            File file = new File(getActivity().getExternalFilesDir(null), "Data Lembur Bulan " + nama_bulan + ".xls");
                            FileOutputStream os = null;

                            try {
                                os = new FileOutputStream(file);
                                wb.write(os);
                                Log.w("FileUtils", "Writing file" + file);
                                progressDialog.dismiss();
                                Toast.makeText(getActivity().getApplicationContext(), "Berhasil disimpan di " + file, Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                Log.w("FileUtils", "Error writing " + file, e);
                            } catch (Exception e) {
                                Log.w("FileUtils", "Failed to save file", e);
                            } finally {
                                try {
                                    if (null != os)
                                        os.close();
                                } catch (Exception ex) {
                                }

                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    progressDialog.dismiss();
                }
            });
        } else
            progressDialog.dismiss();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createPdf(String nama, String divisi, String hari, String tanggal, String jamMulai, String jamSelesai, String leader, String keterangan) {
        //buat dokumennya
        PdfDocument document = new PdfDocument();

        //buat deskripsi halamannya
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(404,529,1).create();

        //halaman mulai
        PdfDocument.Page page = document.startPage(pageInfo);
        final Canvas canvas = page.getCanvas();
        final Paint paint = new Paint();

        Resources resources = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.logotiketux3);
        Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, 384/4, 131/4, false);
        canvas.drawBitmap(bitmap1, 50, 30, paint);

        paint.setTextSize(8);
        paint.setColor(Color.GRAY);
        paint.setTypeface(Typeface.defaultFromStyle(Typeface.SANS_SERIF.BOLD));
        canvas.drawText("PT. TRANS BERJAYA KHATULISTIWA", 150, 44, paint);
        paint.setTextSize((float) 4.5);
        paint.setTypeface(Typeface.DEFAULT.SANS_SERIF);
        canvas.drawText("Jl. Pesantren - Komp. Taman Bumi Prima Blok P7, Cibabat, Cimahi Utara, Jawa Barat", 150, 51, paint);
        canvas.drawText("Telp : +62 22 06 11 404  |  Email : info@tiketux.com  |  www.tiketux.com", 150, 58, paint);


        paint.setTextSize(16);
        paint.setColor(Color.BLACK);
        paint.setUnderlineText(true);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "times_new_roman.ttf");
        paint.setTypeface(typeface);
        canvas.drawText("FORM SURAT PERINTAH LEMBUR", 35, 90, paint);

        paint.setTypeface(typeface);
        paint.setTextSize(12);
        paint.setUnderlineText(false);
        canvas.drawText("Perusahaan dengan ini menugaskan kepada :", 35, 105, paint);
        canvas.drawText("NAMA", 35, 120, paint);
        canvas.drawText(":", 140, 120, paint);
        canvas.drawLine(145, 120, 270, 120, paint);
        canvas.drawText("DIVISI", 35, 135, paint);
        canvas.drawText(":", 140, 135, paint);
        canvas.drawLine(145, 135, 270, 135, paint);

        canvas.drawText("Untuk melaksanakan pekerjaan lembur sebagai", 35, 160, paint);
        canvas.drawText("berikut :", 35, 175, paint);
        canvas.drawText("HARI", 35, 190, paint);
        canvas.drawText(":", 140, 190, paint);
        canvas.drawLine(145, 190, 270, 190, paint);
        canvas.drawText("TANGGAL", 35, 205, paint);
        canvas.drawText(":", 140, 205, paint);
        canvas.drawLine(145, 205, 270, 205, paint);
        canvas.drawText("JAM" , 35, 220, paint);
        canvas.drawText(":", 140, 220, paint);
        canvas.drawLine(145, 220, 180, 220, paint);
        canvas.drawText("s/d", 185, 220, paint);
        canvas.drawLine(210, 220, 245, 220, paint );
        canvas.drawText("PEKERJAAN", 35, 235, paint);
        canvas.drawText(":", 140, 235, paint);
        canvas.drawLine(145, 235, 270, 235, paint);

        canvas.drawText("Menyetujui," , 50, 400, paint);
        canvas.drawText("Karyawan", 50, 415, paint);
        canvas.drawText("(", 50, 500, paint);
        canvas.drawText(")", 170, 500, paint);

        canvas.drawText("Yang Menugaskan," , (404/2) + 50, 400, paint);
        canvas.drawText("Atasan", (404/2) + 50, 415, paint);
        canvas.drawText("(",  (404/2) + 50, 500, paint);
        canvas.drawText(")",  (404/2) + 170, 500, paint);

        paint.setTextSize((float) 4.5);
        paint.setTypeface(Typeface.DEFAULT.SANS_SERIF);
        paint.setColor(Color.GRAY);
        canvas.drawText("PT. TRANS BERJAYA KHATULISTIWA", (404/2) - 30, 515, paint);


        paint.setTypeface(typeface);
        paint.setTextSize(12);
        paint.setColor(Color.BLACK);
        canvas.drawText(nama, 145, 118, paint);
        canvas.drawText(divisi, 145, 133, paint);
        canvas.drawText(hari, 145, 188, paint);
        canvas.drawText(tanggal, 145, 203, paint);
        canvas.drawText(jamMulai, 145, 218, paint);
        canvas.drawText(jamSelesai, 210, 218, paint);
        canvas.drawText(keterangan, 145, 233, paint);
        canvas.drawText(nama, 100 - (nama.length() * 2), 500, paint);
        canvas.drawText(leader, (404/2) + 100 - (leader.length() * 2), 500, paint);

        //canvas lagi ngegambar
        //finish the page
        document.finishPage(page);
        //draw text on the graphics object of the page

        //write the document content
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/mypdf/";
        File file = new File(directory_path);
        if(!file.exists()){
            file.mkdirs();
        }
        String target_pdf = directory_path + "data-pegawai.pdf";
        File filePath = new File(target_pdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
            progressDialog.dismiss();
            new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                    .setMessage("Disimpan di : " + directory_path)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        } catch (IOException e) {
            String dir = Environment.getDataDirectory().getPath() + "/mypdf/";
            File file1 = new File(dir);
            if(!file1.exists()){
                file1.mkdirs();
            }
            String target_pdf1 = dir + "data-pegawai.pdf";
            File filePath1 = new File(target_pdf1);

            try {
                document.writeTo(new FileOutputStream(filePath1));
                progressDialog.dismiss();
                new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                        .setMessage("Disimpan di : " + dir)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            } catch (IOException e1) {
                Log.e("main", "error " + e.toString());
                progressDialog.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), "Something Wrong : " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        //tutup document
        document.close();
    }

    private void getSingleConfirmed(String nip) {
        progressDialog.show();
        if(detectConnection.isConnectingToInternet()){
            RetrofitInterface retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
            Call<ListPengajuanResponse> call = retrofitInterface.getSingleConfirmed("singlepengajuan_dikonfirmasi", nip);
            call.enqueue(new Callback<ListPengajuanResponse>() {
                @Override
                public void onResponse(Call<ListPengajuanResponse> call, Response<ListPengajuanResponse> response) {
                    ListPengajuanResponse pengajuanResponse = response.body();
                    listPengajuanAdapter.setDataPengajuanModelList(pengajuanResponse.getResult());
                    listPengajuanAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<ListPengajuanResponse> call, Throwable t) {
                    Toast.makeText(getActivity().getApplicationContext(), "Something Wrong", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } else
            progressDialog.dismiss();
    }

    private void getAllConfirmed() {
        progressDialog.show();
        if(detectConnection.isConnectingToInternet()){
            RetrofitInterface retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
            Call<ListPengajuanResponse> call = retrofitInterface.getAllConfirmed("pengajuan_dikonfirmasi");
            call.enqueue(new Callback<ListPengajuanResponse>() {
                @Override
                public void onResponse(Call<ListPengajuanResponse> call, Response<ListPengajuanResponse> response) {
                    ListPengajuanResponse pengajuanResponse = response.body();
                    listPengajuanAdapter.setDataPengajuanModelList(pengajuanResponse.getResult());
                    listPengajuanAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<ListPengajuanResponse> call, Throwable t) {
                    Toast.makeText(getActivity().getApplicationContext(), "Something Wrong", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } else
            progressDialog.dismiss();
    }

}
