package com.example.pl.fragment;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pl.R;
import com.example.pl.activity.ActivityHome;
import com.example.pl.model.DataDivisiModel;
import com.example.pl.model.DataPMModel;
import com.example.pl.model.DataPegawaiModel;
import com.example.pl.model.DataPengajuanModel;
import com.example.pl.model.SearchIDPMDIVModel;
import com.example.pl.retrofit.RetrofitClient;
import com.example.pl.retrofit.RetrofitInterface;
import com.example.pl.utils.DetectConnection;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.apache.poi.ss.usermodel.DateUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.Response;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFormPengajuanLembur extends Fragment {

    TextInputEditText  etNama, etHariTanggal, etJamMulai, etJamSelesai, etEstimasiJam, etKeterangan, etDivisi;
    AutoCompleteTextView etNip, etLeader;
    MaterialButton buttonAddPengajuan;
    DetectConnection detectConnection;

    ArrayList<DataPMModel> dataPMModelArrayList;
    ArrayList<String> nampung_leader = new ArrayList<>();
    ArrayList<DataPengajuanModel> dataPengajuanModelArrayList;
    ArrayList<String> nampungId_pengajuan = new ArrayList<>();
    ArrayList<DataDivisiModel> dataDivisiModelArrayList;
    ArrayList<String> names = new ArrayList<>();

    String hari = "", tanggalToAPI = "", tanggalToAPIBeresLembur = "";
    int jamMulai = -1, menitMulai = -1, jamSelesai = -1, menitSelesai = -1, hours, min;
    Date dateJamMulai, dateJamSelesai;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int cek_hrd = 0;
    ProgressDialog progressDialog;
    public FragmentFormPengajuanLembur() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_form_pengajuan_lembur, container, false);
        etNip = (AutoCompleteTextView) view.findViewById(R.id.input_pengajuan_nip);
        etNama = (TextInputEditText)view.findViewById(R.id.input_pengajuan_nama);
        etDivisi = (TextInputEditText) view.findViewById(R.id.input_pengajuan_divisi);
        etHariTanggal = (TextInputEditText)view.findViewById(R.id.input_pengajuan_hari_tanggal);
        etJamMulai = (TextInputEditText)view.findViewById(R.id.input_pengajuan_jam_mulai);
        etJamSelesai = (TextInputEditText)view.findViewById(R.id.input_pengajuan_jam_selesai);
        etEstimasiJam = (TextInputEditText)view.findViewById(R.id.input_pengajuan_estimasi_jam);
        etLeader = (AutoCompleteTextView)view.findViewById(R.id.input_pengajuan_leader);
        etKeterangan = (TextInputEditText)view.findViewById(R.id.input_pengajuan_keterangan);
        buttonAddPengajuan = (MaterialButton)view.findViewById(R.id.button_pengajuan);
        detectConnection = new DetectConnection(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Harap Tunggu...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        pref = getActivity().getApplicationContext().getSharedPreferences("data", 0); // 0 - for private mode
        editor = pref.edit();

        if(!"HRD".equalsIgnoreCase(pref.getString("level", null))){
            etNip.setText(pref.getString("nip", null));
            etNama.setText(pref.getString("nama", null));
            etDivisi.setText(pref.getString("nama_divisi", null));
            etNip.setFocusable(false);
            etNama.setFocusable(false);
            etDivisi.setFocusable(false);
            etDivisi.setFocusableInTouchMode(false);
            etDivisi.setCursorVisible(false);
            cek_hrd = 1;
        } else {
            getNip();
            //getDataDivisi();
        }
        getDataPM();

        etNip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s != null && !s.toString().equalsIgnoreCase("")){
                    if(etNip.getText().hashCode() == s.hashCode()){
                        String n = s.toString();
                        getDetailNip(n);
                        System.out.println("NIP yang Dipilih: " + n);
                    }
                }
            }
        });

        etHariTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                final int mYear = calendar.get(java.util.Calendar.YEAR),
                        mMonth = calendar.get(java.util.Calendar.MONTH),
                        mDay = calendar.get(java.util.Calendar.DAY_OF_MONTH);
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        SimpleDateFormat formatHari = new SimpleDateFormat("EEEE");
                        switch (formatHari.format(new Date(year, month, dayOfMonth - 1))){
                            case ("Sunday"):
                                hari = "Minggu";
                                break;
                            case ("Monday"):
                                hari = "Senin";
                                break;
                            case ("Tuesday"):
                                hari = "Selasa";
                                break;
                            case ("Wednesday"):
                                hari = "Rabu";
                                break;
                            case ("Thursday"):
                                hari = "Kamis";
                                break;
                            case ("Friday"):
                                hari = "Jumat";
                                break;
                            case ("Saturday"):
                                hari = "Sabtu";
                                break;
                        }
                        String bulanIndonesia = "";
                        switch (month){
                            case 0: bulanIndonesia = "Januari"; break;
                            case 1: bulanIndonesia = "Februari"; break;
                            case 2: bulanIndonesia = "Maret"; break;
                            case 3: bulanIndonesia = "April"; break;
                            case 4: bulanIndonesia = "Mei"; break;
                            case 5: bulanIndonesia = "Juni"; break;
                            case 6: bulanIndonesia = "Juli"; break;
                            case 7: bulanIndonesia = "Agustus"; break;
                            case 8: bulanIndonesia = "September"; break;
                            case 9: bulanIndonesia = "Oktober"; break;
                            case 10: bulanIndonesia = "November"; break;
                            case 11: bulanIndonesia = "Desember"; break;
                        }
                        etHariTanggal.setText(hari + ", " + dayOfMonth + " " + bulanIndonesia + " " + year);
                        SimpleDateFormat formatToAPI = new SimpleDateFormat("yyyy-MM-dd");
                        tanggalToAPI = formatToAPI.format(new Date(year-1900, month, dayOfMonth));
                    }
                }, mYear, mMonth, mDay).show();
            }
        });

        etJamMulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String jam = String.valueOf(hourOfDay), menit = String.valueOf(minute);
                        if(hourOfDay < 10){
                            jam = "0" + hourOfDay;
                            if(minute < 10){
                                menit = "0" + minute;
                            }
                        } else{
                            if (minute < 10){
                                menit = "0" + minute;
                            }
                        }
                        jamMulai = hourOfDay;
                        menitMulai = minute;
                        SimpleDateFormat formatJamMulai = new SimpleDateFormat("hh:mm");
                        try {
                            dateJamMulai = formatJamMulai.parse(jam + ":" + menit);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        getSelisihJam();
                        etJamMulai.setText(jam + ":" + menit);
                    }
                }, hour, minute, true).show();
            }
        });

        etJamSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String jam = String.valueOf(hourOfDay), menit = String.valueOf(minute);
                        if(hourOfDay < 10){
                            jam = "0" + hourOfDay;
                            if(minute < 10){
                                menit = "0" + minute;
                            }
                        } else{
                            if (minute < 10){
                                menit = "0" + minute;
                            }
                        }
                        jamSelesai = hourOfDay;
                        menitSelesai = minute;
                        SimpleDateFormat formatJamSelesai = new SimpleDateFormat("hh:mm");
                        try {
                            dateJamSelesai = formatJamSelesai.parse(jam + ":" + menit);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        getSelisihJam();
                        etJamSelesai.setText(jam + ":" + menit);
                    }
                }, hour, minute, true).show();
            }
        });

        buttonAddPengajuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                if(detectConnection.isConnectingToInternet()){
                    getResponse();
                } else
                    progressDialog.dismiss();
            }
        });

        return view;
    }

    private void getDetailNip(final String nipfix) {
        progressDialog.show();
        if(detectConnection.isConnectingToInternet()){
            Retrofit retrofit = new Retrofit.Builder().baseUrl(RetrofitClient.BASE_URL).addConverterFactory(ScalarsConverterFactory.create()).build();
            RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
            Call<String> call = retrofitInterface.searchDataPegawai("fetch_singlepegawai", nipfix);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                    if(response.isSuccessful()){
                        if (response.body() != null){
                            String jsonResponse = response.body().toString();
                            try {
                                JSONObject jsonObject = new JSONObject(jsonResponse);
                                if(jsonObject.optString("status").equals("ok")){
                                    ArrayList<DataPegawaiModel> dataPegawaiModelArrayList = new ArrayList<>();
                                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                                    for (int i = 0; i < jsonArray.length(); i++){
                                        DataPegawaiModel pegawaiModel = new DataPegawaiModel();
                                        JSONObject dataObj = jsonArray.getJSONObject(i);

                                        pegawaiModel.setNama(dataObj.getString("nama"));
                                        pegawaiModel.setNama_divisi(dataObj.getString("nama_divisi"));

                                        dataPegawaiModelArrayList.add(pegawaiModel);

                                        etNama.setText(dataPegawaiModelArrayList.get(i).getNama());
                                        etDivisi.setText(dataPegawaiModelArrayList.get(i).getNama_divisi());
                                        progressDialog.dismiss();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
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

    private void getNip() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                RetrofitClient.BASE_URL + "ControllerKaryawan.php?action=fetch_datapegawai",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj.optString("status").equals("ok")){
                                List<DataPegawaiModel> dataPegawaiModelList = new ArrayList<>();
                                List<String> nampung_nip = new ArrayList<>();
                                JSONArray jsonArray = obj.getJSONArray("result");
                                for (int i = 0; i < jsonArray.length(); i++){
                                    DataPegawaiModel pegawaiModel = new DataPegawaiModel();
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    pegawaiModel.setNip(object.getString("nip"));
                                    pegawaiModel.setNama(object.getString("nama"));

                                    dataPegawaiModelList.add(pegawaiModel);
                                }

                                for (int i = 0; i < dataPegawaiModelList.size(); i++){
                                    nampung_nip.add(dataPegawaiModelList.get(i).getNip() + " " + dataPegawaiModelList.get(i).getNama());
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_data_pegawai_input_divisi,R.id.haeya, nampung_nip);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                etNip.setAdapter(adapter);
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), "Terdapat kesalahan saat mengambil data NIP", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void getResponse() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(RetrofitClient.BASE_URL).addConverterFactory(ScalarsConverterFactory.create()).build();
        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        Call<String> call = retrofitInterface.searchIDPMDIV("search_id",  etLeader.getText().toString(), etDivisi.getText().toString());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                if(response.isSuccessful()){
                    if (response.body() != null){
                        String jsonResponse = response.body().toString();
                        id(jsonResponse);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void id(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            if(jsonObject.optString("status").equals("ok")) {
                ArrayList<SearchIDPMDIVModel> list = new ArrayList<>();
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                for (int i = 0; i < jsonArray.length(); i++) {
                    SearchIDPMDIVModel search_id_pm_div = new SearchIDPMDIVModel();
                    JSONObject dataObj = jsonArray.getJSONObject(i);

                    search_id_pm_div.setId_pm(dataObj.getString("id_pm"));
                    search_id_pm_div.setId_divisi(dataObj.getString("id_divisi"));

                    list.add(search_id_pm_div);
                    getId_pengajuan(list.get(i).getId_pm(), list.get(i).getId_divisi());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getId_pengajuan(final String id_leader, final String id_div) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                RetrofitClient.BASE_URL + "ControllerPengajuan.php?action=auto_incrementpengajuan",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj.optString("status").equals("ok")){
                                dataPengajuanModelArrayList = new ArrayList<>();
                                JSONArray jsonArray = obj.getJSONArray("result");

                                for(int i = 0; i < jsonArray.length(); i++){
                                    DataPengajuanModel dataPengajuanModel = new DataPengajuanModel();
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    dataPengajuanModel.setId_pengajuan(object.getString("id_pengajuan"));
                                    dataPengajuanModelArrayList.add(dataPengajuanModel);
                                    nampungId_pengajuan.add(dataPengajuanModelArrayList.get(i).getId_pengajuan().toString());
                                    final String nip_last = nampungId_pengajuan.get(i).toString(), tigawal, fix_nip;
                                    tigawal = nip_last.substring(0, 3);
                                    int tigakhir = Integer.parseInt(nip_last.substring(3, 6));
                                    tigakhir++;
                                    if(tigakhir < 10)
                                        fix_nip = tigawal + "00" + tigakhir;
                                    else if(tigakhir > 9 && tigakhir < 100)
                                        fix_nip = tigawal + "0" + tigakhir;
                                    else
                                        fix_nip = tigawal + tigakhir;

                                    Retrofit retrofit = new Retrofit.Builder().baseUrl(RetrofitClient.BASE_URL).addConverterFactory(ScalarsConverterFactory.create()).build();
                                    RetrofitInterface retrofitInterface1 = retrofit.create(RetrofitInterface.class);
                                    Call<String> call1 = retrofitInterface1.searchDataPegawai("fetch_singlepegawai", etNip.getText().toString());
                                    call1.enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                                            if(response.isSuccessful()){
                                                if (response.body() != null){
                                                    String jsonResponse1 = response.body().toString();
                                                    try {
                                                        JSONObject jsonObject1 = new JSONObject(jsonResponse1);
                                                        if (jsonObject1.optString("status").equals("ok")){
                                                            ArrayList<DataPegawaiModel> dataPegawaiModelArrayList = new ArrayList<>();
                                                            JSONArray jsonArray2 = jsonObject1.getJSONArray("result");
                                                            for (int i = 0; i < jsonArray2.length(); i++){
                                                                DataPegawaiModel dataPegawaiModel = new DataPegawaiModel();
                                                                JSONObject dataObj1 = jsonArray2.getJSONObject(i);

                                                                dataPegawaiModel.setGaji(dataObj1.getString("gaji"));
                                                                dataPegawaiModelArrayList.add(dataPegawaiModel);
                                                                int gajinya = Integer.parseInt(dataPegawaiModelArrayList.get(i).getGaji()), jam = 0, menit = 0;
                                                                gajinya = ((hours * 60) + min) * 2 * (gajinya/60);
                                                                /*String estJam = etEstimasiJam.getText().toString();
                                                                switch (estJam.length()){
                                                                    case 13: jam = Integer.parseInt(estJam.substring(0, 1));
                                                                    menit = Integer.parseInt(estJam.substring(6, 7));
                                                                    gajinya = ((jam/60) + menit) * 2 * gajinya;
                                                                    break;
                                                                    case 14: if(estJam.substring(0, 2).trim().length() == 1){
                                                                        jam = Integer.parseInt(estJam.substring(0,1));
                                                                        menit = Integer.parseInt(estJam.substring(6, 8));
                                                                        gajinya =
                                                                    }
                                                                    case 15:
                                                                }*/
                                                                Log.i("GAJINYA : ", String.valueOf(gajinya));
                                                                RetrofitInterface ri = RetrofitClient.getClient().create(RetrofitInterface.class);
                                                                Call<DataPengajuanModel> cdpm = ri.insertDataPengajuan("insert_pengajuan", fix_nip, etNip.getText().toString(),
                                                                        etNama.getText().toString(), id_div, hari, tanggalToAPI, etJamMulai.getText().toString(), etJamSelesai.getText().toString(),
                                                                        id_leader, etKeterangan.getText().toString(), tanggalToAPIBeresLembur);
                                                                cdpm.enqueue(new Callback<DataPengajuanModel>() {
                                                                    @Override
                                                                    public void onResponse(Call<DataPengajuanModel> call, retrofit2.Response<DataPengajuanModel> response) {
                                                                        if (response.isSuccessful()){
                                                                            progressDialog.dismiss();
                                                                            new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                                                                    .setMessage("Berhasil Mengajukan")
                                                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                                            clear();
                                                                                            dialog.dismiss();
                                                                                        }
                                                                                    }).show();
                                                                        } else {
                                                                            progressDialog.dismiss();
                                                                            new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                                                                    .setMessage("Gagal Mengajukan")
                                                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                                            clear();
                                                                                            dialog.dismiss();
                                                                                        }
                                                                                    }).show();
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onFailure(Call<DataPengajuanModel> call, Throwable t) {
                                                                        Toast.makeText(getActivity().getApplicationContext(), "Gagal Mengajukan", Toast.LENGTH_SHORT).show();
                                                                        progressDialog.dismiss();
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {

                                        }
                                    });
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void clear() {
        if(cek_hrd == 0){
            etNip.setText(null);
            etNama.setText(null);
            etDivisi.setText(null);
        }
        etHariTanggal.setText(null);
        etJamMulai.setText(null);
        etJamSelesai.setText(null);
        etEstimasiJam.setText(null);
        etLeader.setText(null);
        etKeterangan.setText(null);
    }

    private void getSelisihJam() {
        if(jamMulai != -1 && jamSelesai!= -1){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                long difference = dateJamSelesai.getTime() - dateJamMulai.getTime();
                if(difference<0)
                {
                    try {
                        Date dateMax = simpleDateFormat.parse("24:00");
                        Date dateMin = simpleDateFormat.parse("00:00");
                        difference=(dateMax.getTime() -dateJamMulai.getTime() )+(dateJamSelesai.getTime()-dateMin.getTime());
                        SimpleDateFormat formatOneDay = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar c = Calendar.getInstance();
                        c.setTime(formatOneDay.parse(tanggalToAPI));
                        c.add(Calendar.DATE, 1);
                        Date newDate = c.getTime();
                        tanggalToAPIBeresLembur = String.valueOf(formatOneDay.format(newDate));
                        Toast.makeText(getActivity().getApplicationContext(), tanggalToAPIBeresLembur, Toast.LENGTH_SHORT).show();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else{
                    tanggalToAPIBeresLembur = tanggalToAPI;
                    Toast.makeText(getActivity().getApplicationContext(), tanggalToAPIBeresLembur, Toast.LENGTH_SHORT).show();
                }
                int days = (int) (difference / (1000*60*60*24));
                hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
                min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
                etEstimasiJam.setText(hours + " Jam " + min + " Menit");
                Log.i("log_tag","Hours: "+hours+", Mins: "+min);
        }
    }

    private void getDataPM() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                RetrofitClient.BASE_URL + "ControllerPM.php?action=fetch_datapm",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj.optString("status").equals("ok")){
                                dataPMModelArrayList = new ArrayList<>();
                                JSONArray jsonArray = obj.getJSONArray("result");

                                for(int i = 0; i < jsonArray.length(); i++){
                                    DataPMModel dataPMModel = new DataPMModel();
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    dataPMModel.setNama_pm(object.getString("nama_pm"));
                                    dataPMModelArrayList.add(dataPMModel);
                                }

                                for(int i = 0; i < dataPMModelArrayList.size(); i++){
                                    nampung_leader.add(dataPMModelArrayList.get(i).getNama_pm().toString());
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_data_pegawai_input_divisi, nampung_leader);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                etLeader.setAdapter(adapter);
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), "Terdapat kesalahan saat mengambil data PM", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

}
