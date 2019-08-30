package com.example.pl.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pl.R;
import com.example.pl.activity.ActivityHome;
import com.example.pl.adapter.DataPegawaiAdapter;
import com.example.pl.fragment.FragmentDataPegawai;
import com.example.pl.model.DataDivisiModel;
import com.example.pl.model.DataPegawaiModel;
import com.example.pl.response.DataPegawaiResponse;
import com.example.pl.retrofit.RetrofitClient;
import com.example.pl.retrofit.RetrofitInterface;
import com.example.pl.utils.DetectConnection;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class FullScreenDialog_DataPegawai extends DialogFragment  {
    TextInputEditText etNip, etNama,  etNo_telp, etAlamat, etGaji;
    AutoCompleteTextView etDivisi;
    MaterialButton btnHapus;
    public static String TAG = "FullScreenDialog_DataPegawai";
    private String nip = "", nama, divisi, no_telp, alamat, gaji;
    private int cek_button_hapus = 0;
    ArrayAdapter<String> adapter;
    ArrayList<DataDivisiModel> dataDivisiModelArrayList;
    ArrayList<DataPegawaiModel> dataPegawaiModelArrayList;
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> names1 = new ArrayList<>();

    DetectConnection detectConnection;
    ProgressDialog progressDialog;
    public FullScreenDialog_DataPegawai(){

    }

    public FullScreenDialog_DataPegawai(String nip, String nama, String divisi, String no_telp, String alamat, String gaji){
        this.nip = nip;
        this.nama = nama;
        this.divisi = divisi;
        this.no_telp = no_telp;
        this.alamat = alamat;
        this.gaji = gaji;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.dialog_add_pegawai, container, false);
        etNip = (TextInputEditText)view.findViewById(R.id.data_pegawai_input_nip);
        etNama = (TextInputEditText)view.findViewById(R.id.data_pegawai_input_nama);
        etDivisi = (AutoCompleteTextView) view.findViewById(R.id.data_pegawai_input_divisi);
        etNo_telp = (TextInputEditText)view.findViewById(R.id.data_pegawai_input_no_telp);
        etAlamat = (TextInputEditText)view.findViewById(R.id.data_pegawai_input_alamat);
        etGaji = (TextInputEditText)view.findViewById(R.id.data_pegawai_gaji_lembur);
        btnHapus = (MaterialButton)view.findViewById(R.id.button_hapus_pegawai);
        detectConnection = new DetectConnection(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Harap Tunggu...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        getDataDivisi();
        MaterialToolbar toolbar = view.findViewById(R.id.materialToolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        if(!"".equals(nip)){
            etNip.setText(nip);
            etNama.setText(nama);
            etDivisi.setText(divisi, false);
            etNo_telp.setText(no_telp);
            etAlamat.setText(alamat);
            etGaji.setText(gaji);
            btnHapus.setVisibility(View.VISIBLE);
            cek_button_hapus = 1;
            toolbar.setTitle("Ubah Data Pegawai");
        } else{
            btnHapus.setVisibility(View.INVISIBLE);
            cek_button_hapus = 0;
            setNip();
            toolbar.setTitle("Tambah Data Pegawai");
        }

        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                        .setMessage("Anda yakin ingin menghapus data ini??")
                        .setPositiveButton("YA", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.show();
                                if(detectConnection.isConnectingToInternet()){
                                    delete();
                                } else
                                    progressDialog.dismiss();
                            }
                        })
                        .setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        return view;
    }

    private void setNip() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                RetrofitClient.BASE_URL + "ControllerKaryawan.php?action=auto_increment",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj.optString("status").equals("ok")){
                                dataPegawaiModelArrayList = new ArrayList<>();
                                JSONArray jsonArray = obj.getJSONArray("result");

                                for(int i = 0; i < jsonArray.length(); i++){
                                    DataPegawaiModel pegawaiModel = new DataPegawaiModel();
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    pegawaiModel.setNip(object.getString("nip"));

                                    dataPegawaiModelArrayList.add(pegawaiModel);
                                    names1.add(dataPegawaiModelArrayList.get(i).getNip().toString());
                                    String nip_last = names1.get(i).toString(), tigawal, fix_nip;
                                    tigawal = nip_last.substring(0, 3);
                                    int tigakhir = Integer.parseInt(nip_last.substring(3, 6)) + 1;
                                    if(tigakhir < 10)
                                        fix_nip = tigawal + "00" + tigakhir;
                                    else if(tigakhir > 9 && tigakhir < 100)
                                        fix_nip = tigawal + "0" + tigakhir;
                                    else
                                        fix_nip = tigawal + tigakhir;
                                    etNip.setText(fix_nip);
                                    progressDialog.dismiss();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), "Terdapat kesalahan saat mengambil data divisi", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void getDataDivisi() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                RetrofitClient.BASE_URL + "ControllerDivisi.php?action=fetch_datadivisi",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj.optString("status").equals("ok")){
                                dataDivisiModelArrayList = new ArrayList<>();
                                JSONArray jsonArray = obj.getJSONArray("result");

                                for(int i = 0; i < jsonArray.length(); i++){
                                    DataDivisiModel dataDivisiModel = new DataDivisiModel();
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    dataDivisiModel.setNama_divisi(object.getString("nama_divisi"));

                                    dataDivisiModelArrayList.add(dataDivisiModel);
                                }

                                for(int i = 0; i < dataDivisiModelArrayList.size(); i++){
                                    names.add(dataDivisiModelArrayList.get(i).getNama_divisi().toString());
                                }

                                adapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_data_pegawai_input_divisi,R.id.haeya, names);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                etDivisi.setAdapter(adapter);
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), "Terdapat kesalahan saat mengambil data divisi", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_simpan_data_pegawai, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.toolbar_simpan_pegawai:
                if(cek_button_hapus == 0)
                    insertPegawai();
                else
                    updatePegawai();
                return true;
        }
        return false;
    }

    private void updatePegawai() {
        progressDialog.show();
        if(detectConnection.isConnectingToInternet()){
            getResponse("update");
        } else
            progressDialog.dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT,
            height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    private void insertPegawai(){
        if(detectConnection.isConnectingToInternet()) {
            getResponse("insert");
        }
    }

    private void getResponse(final String action) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(RetrofitClient.BASE_URL).addConverterFactory(ScalarsConverterFactory.create()).build();
        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        Call<String> call = retrofitInterface.searchIdDivisi("search_iddivisi", etDivisi.getText().toString());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        String jsonResponse = response.body().toString();
                        id(jsonResponse, action);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void id(String jsonResponse, String action) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            if(jsonObject.optString("status").equals("ok")){
                ArrayList<DataDivisiModel> list = new ArrayList<>();
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                for (int i = 0; i < jsonArray.length(); i++){
                    DataDivisiModel dataDivisi = new DataDivisiModel();
                    JSONObject dataObj = jsonArray.getJSONObject(i);

                    dataDivisi.setId_divisi(dataObj.getString("id_divisi"));

                    list.add(dataDivisi);
                    String id_div = list.get(i).getId_divisi();

                    if("insert".equals(action)){
                        insert(id_div);
                    } else if("update".equals(action)){
                        update(id_div);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void delete() {
        RetrofitInterface retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
        Call<DataPegawaiModel> call = retrofitInterface.deleteDataPegawai("delete_pegawai", etNip.getText().toString());
        call.enqueue(new Callback<DataPegawaiModel>() {
            @Override
            public void onResponse(Call<DataPegawaiModel> call, Response<DataPegawaiModel> response) {
                Toast.makeText(getActivity().getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
                getActivity().recreate();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<DataPegawaiModel> call, Throwable t) {
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
                getActivity().recreate();
                progressDialog.dismiss();
            }
        });
    }

    private void update(String id_div) {
        RetrofitInterface retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
        Call<DataPegawaiModel> call = retrofitInterface.updateDataPegawai("update_pegawai", etNip.getText().toString(), etNama.getText().toString(), etAlamat.getText().toString(), etNo_telp.getText().toString(), id_div, etGaji.getText().toString());
        call.enqueue(new Callback<DataPegawaiModel>() {
            @Override
            public void onResponse(Call<DataPegawaiModel> call, Response<DataPegawaiModel> response) {
                Toast.makeText(getActivity().getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
                getActivity().recreate();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<DataPegawaiModel> call, Throwable t) {
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
                getActivity().recreate();
                progressDialog.dismiss();
            }
        });
    }

    private void insert(String id_div){
        RetrofitInterface retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
        Call<DataPegawaiModel> call = retrofitInterface.insertPegawai("insert_pegawai", etNip.getText().toString(), etNama.getText().toString(), etAlamat.getText().toString(), etNo_telp.getText().toString(), id_div, etGaji.getText().toString());
        call.enqueue(new Callback<DataPegawaiModel>() {
            @Override
            public void onResponse(Call<DataPegawaiModel> call, Response<DataPegawaiModel> response) {
                Toast.makeText(getActivity().getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
                getActivity().recreate();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<DataPegawaiModel> call, Throwable t) {
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
                getActivity().recreate();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }
}
