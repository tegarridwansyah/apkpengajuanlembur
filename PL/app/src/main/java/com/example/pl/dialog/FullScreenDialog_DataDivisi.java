package com.example.pl.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pl.R;
import com.example.pl.model.DataDivisiModel;
import com.example.pl.retrofit.RetrofitClient;
import com.example.pl.retrofit.RetrofitInterface;
import com.example.pl.utils.DetectConnection;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FullScreenDialog_DataDivisi extends DialogFragment {
    TextInputEditText etId_Divisi, etNama_Divisi;
    MaterialButton btnHapus;

    DetectConnection detectConnection;
    int cek_button_hapus = 0;
    public static String TAG = "FullScreenDialog_DataDivisi";
    String id_divisi = "", nama_divisi;
    ArrayList<DataDivisiModel> dataDivisiModelArrayList;
    ArrayList<String> nampung_id = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ProgressDialog progressDialog;
    public FullScreenDialog_DataDivisi(){

    }

    public FullScreenDialog_DataDivisi(String id_divisi, String nama_divisi){
        this.id_divisi = id_divisi;
        this.nama_divisi = nama_divisi;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_divisi, container, false);
        etId_Divisi = (TextInputEditText)view.findViewById(R.id.data_divisi_id_pm_fix);
        etNama_Divisi = (TextInputEditText)view.findViewById(R.id.data_divisi_nama_pm);
        btnHapus = (MaterialButton)view.findViewById(R.id.button_hapus_pm);
        detectConnection = new DetectConnection(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Harap Tunggu...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        MaterialToolbar toolbar = view.findViewById(R.id.materialToolbarPM);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        if(!"".equals(id_divisi)){
            etId_Divisi.setText(id_divisi);
            etNama_Divisi.setText(nama_divisi);
            btnHapus.setVisibility(View.VISIBLE);
            cek_button_hapus = 1;
            toolbar.setTitle("Ubah Data Divisi");
        } else {
            setId();
            btnHapus.setVisibility(View.INVISIBLE);
            cek_button_hapus = 0;
            toolbar.setTitle("Tambah Data Divisi");
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
                                    RetrofitInterface retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
                                    Call<DataDivisiModel> call = retrofitInterface.deleteDataDivisi("delete_divisi", etId_Divisi.getText().toString());
                                    call.enqueue(new Callback<DataDivisiModel>() {
                                        @Override
                                        public void onResponse(Call<DataDivisiModel> call, Response<DataDivisiModel> response) {
                                            Toast.makeText(getActivity().getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                            getDialog().dismiss();
                                            getActivity().recreate();
                                            progressDialog.dismiss();
                                        }

                                        @Override
                                        public void onFailure(Call<DataDivisiModel> call, Throwable t) {
                                            Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                            getDialog().dismiss();
                                            getActivity().recreate();
                                            progressDialog.dismiss();
                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                }
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

    private void setId() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                RetrofitClient.BASE_URL + "ControllerDivisi.php?action=auto_incrementdivisi",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj.optString("status").equals("ok")){
                                dataDivisiModelArrayList = new ArrayList<>();
                                JSONArray jsonArray = obj.getJSONArray("result");

                                for(int i = 0; i < jsonArray.length(); i++){
                                    DataDivisiModel divisiModel = new DataDivisiModel();
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    divisiModel.setId_divisi(object.getString("id_divisi"));

                                    dataDivisiModelArrayList.add(divisiModel);
                                    nampung_id.add(dataDivisiModelArrayList.get(i).getId_divisi().toString());

                                    String nip_last = nampung_id.get(i).toString(), tigawal, fix_nip;
                                    tigawal = nip_last.substring(0, 3);
                                    int tigakhir = Integer.parseInt(nip_last.substring(3, 6)) + 1;
                                    if(tigakhir < 10)
                                        fix_nip = tigawal + "00" + tigakhir;
                                    else if(tigakhir > 9 && tigakhir < 100)
                                        fix_nip = tigawal + "0" + tigakhir;
                                    else
                                        fix_nip = tigawal + tigakhir;

                                    etId_Divisi.setText(fix_nip);
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
                Toast.makeText(getActivity().getApplicationContext(), "Terdapat kesalahan saat mengambil data id divisi", Toast.LENGTH_SHORT).show();
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
                    insertDivisi();
                else
                    updateDivisi();
                return true;
        }
        return false;
    }

    private void updateDivisi() {
        progressDialog.show();
        if(detectConnection.isConnectingToInternet()){
            RetrofitInterface retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
            Call<DataDivisiModel> call = retrofitInterface.updateDataDivisi("update_divisi", etId_Divisi.getText().toString(), etNama_Divisi.getText().toString());
            call.enqueue(new Callback<DataDivisiModel>() {
                @Override
                public void onResponse(Call<DataDivisiModel> call, Response<DataDivisiModel> response) {
                    Toast.makeText(getActivity().getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                    getDialog().dismiss();
                    getActivity().recreate();
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<DataDivisiModel> call, Throwable t) {
                    Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    getDialog().dismiss();
                    getActivity().recreate();
                    progressDialog.dismiss();
                }
            });
        } else
            progressDialog.dismiss();
    }

    private void insertDivisi() {
        progressDialog.show();
        if (detectConnection.isConnectingToInternet()){
            RetrofitInterface retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
            Call<DataDivisiModel> call = retrofitInterface.insertDataDivisi("insert_divisi", etId_Divisi.getText().toString(), etNama_Divisi.getText().toString());
            call.enqueue(new Callback<DataDivisiModel>() {
                @Override
                public void onResponse(Call<DataDivisiModel> call, Response<DataDivisiModel> response) {
                    Toast.makeText(getActivity().getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                    getDialog().dismiss();
                    getActivity().recreate();
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<DataDivisiModel> call, Throwable t) {
                    Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    getDialog().dismiss();
                    getActivity().recreate();
                    progressDialog.dismiss();
                }
            });
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }
}
