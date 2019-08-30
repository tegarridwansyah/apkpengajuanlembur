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
import com.example.pl.model.DataPMModel;
import com.example.pl.model.DataPegawaiModel;
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

public class FullScreenDialog_DataPM extends DialogFragment {
    TextInputEditText etId_PM;
    AutoCompleteTextView etNama_PM;
    MaterialButton btnHapus;

    DetectConnection detectConnection;
    int cek_button_hapus = 0;
    public static String TAG = "FullScreenDialog_DataPM";
    String id_pm = "", nama_pm;
    ArrayList<DataPMModel>dataPMModelArrayList;
    ArrayList<String> nampung_id = new ArrayList<>();
    ProgressDialog progressDialog;

    public FullScreenDialog_DataPM(){

    }

    public FullScreenDialog_DataPM(String id_pm, String nama_pm){
        this.id_pm = id_pm;
        this.nama_pm = nama_pm;
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
        View view = inflater.inflate(R.layout.dialog_add_pm, container, false);
        etId_PM = (TextInputEditText)view.findViewById(R.id.data_pm_id_pm_fix);
        etNama_PM = (AutoCompleteTextView) view.findViewById(R.id.data_pm_nama_pm_fix);
        btnHapus = (MaterialButton)view.findViewById(R.id.button_hapus_pm);
        detectConnection = new DetectConnection(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Harap Tunggu...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        getDataPegawai();
        MaterialToolbar toolbar = view.findViewById(R.id.materialToolbarPM);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        if(!"".equals(id_pm)){
            etId_PM.setText(id_pm);
            etNama_PM.setText(nama_pm);
            cek_button_hapus = 1;
            toolbar.setTitle("Ubah Data PM");
        } else {
            setId();
            btnHapus.setVisibility(View.INVISIBLE);
            cek_button_hapus = 0;
            toolbar.setTitle("Tambah Data PM");
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
                                    Call<DataPMModel> call = retrofitInterface.deleteDataPM("delete_pm", etId_PM.getText().toString());
                                    call.enqueue(new Callback<DataPMModel>() {
                                        @Override
                                        public void onResponse(Call<DataPMModel> call, Response<DataPMModel> response) {
                                            Toast.makeText(getActivity().getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                            getDialog().dismiss();
                                            getActivity().recreate();
                                            progressDialog.dismiss();
                                        }

                                        @Override
                                        public void onFailure(Call<DataPMModel> call, Throwable t) {
                                            Toast.makeText(getActivity().getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                            getDialog().dismiss();
                                            getActivity().recreate();
                                            progressDialog.dismiss();
                                        }
                                    });
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

    private void getDataPegawai() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                RetrofitClient.BASE_URL + "ControllerKaryawan.php?action=fetch_datapegawai",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj.optString("status").equals("ok")){
                                ArrayList<DataPegawaiModel> dataPegawaiModelArrayList = new ArrayList<>();
                                ArrayList<String> names1 = new ArrayList<>();
                                JSONArray jsonArray = obj.getJSONArray("result");

                                for(int i = 0; i < jsonArray.length(); i++){
                                    DataPegawaiModel pegawaiModel = new DataPegawaiModel();
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    pegawaiModel.setNama(object.getString("nama"));

                                    dataPegawaiModelArrayList.add(pegawaiModel);
                                    names1.add(dataPegawaiModelArrayList.get(i).getNama().toString());
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_data_pegawai_input_divisi,R.id.haeya, names1);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                etNama_PM.setAdapter(adapter);
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void setId() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                RetrofitClient.BASE_URL + "ControllerPM.php?action=auto_incrementPM",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.optString("status").equals("ok")) {
                                dataPMModelArrayList = new ArrayList<>();
                                JSONArray jsonArray = obj.getJSONArray("result");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    DataPMModel dataPMModel = new DataPMModel();
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    dataPMModel.setId_pm(object.getString("id_pm"));

                                    dataPMModelArrayList.add(dataPMModel);
                                    nampung_id.add(dataPMModelArrayList.get(i).getId_pm().toString());

                                    String nip_last = nampung_id.get(i).toString(), tigawal, fix_nip;
                                    tigawal = nip_last.substring(0, 2);
                                    int tigakhir = Integer.parseInt(nip_last.substring(2, 5)) + 1;
                                    if (tigakhir < 10)
                                        fix_nip = tigawal + "00" + tigakhir;
                                    else if (tigakhir > 9 && tigakhir < 100)
                                        fix_nip = tigawal + "0" + tigakhir;
                                    else
                                        fix_nip = tigawal + tigakhir;

                                    etId_PM.setText(fix_nip);
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
                Toast.makeText(getActivity().getApplicationContext(), "Terdapat kesalahan saat mengambil data id pm", Toast.LENGTH_SHORT).show();
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
                    insertPM();
                else
                    updatePM();
                return true;
        }
        return false;
    }

    private void updatePM() {
        progressDialog.show();
        if(detectConnection.isConnectingToInternet()){
            RetrofitInterface retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
            Call<DataPMModel> call = retrofitInterface.updateDataPM("update_pm", etId_PM.getText().toString(), etNama_PM.getText().toString());
            call.enqueue(new Callback<DataPMModel>() {
                @Override
                public void onResponse(Call<DataPMModel> call, Response<DataPMModel> response) {
                    Toast.makeText(getActivity().getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                    getDialog().dismiss();
                    getActivity().recreate();
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<DataPMModel> call, Throwable t) {
                    Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    getDialog().dismiss();
                    getActivity().recreate();
                    progressDialog.dismiss();
                }
            });
        } else
            progressDialog.dismiss();
    }

    private void insertPM() {
        progressDialog.show();
        if(detectConnection.isConnectingToInternet()){
            RetrofitInterface retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
            Call<DataPMModel> call = retrofitInterface.insertDataPM("insert_pm", etId_PM.getText().toString(), etNama_PM.getText().toString());
            call.enqueue(new Callback<DataPMModel>() {
                @Override
                public void onResponse(Call<DataPMModel> call, Response<DataPMModel> response) {
                    Toast.makeText(getActivity().getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                    getDialog().dismiss();
                    getActivity().recreate();
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<DataPMModel> call, Throwable t) {
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
