package com.example.pl.fragment;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.pl.R;
import com.example.pl.activity.ActivityHome;
import com.example.pl.adapter.ListPengajuanAdapter;
import com.example.pl.adapter.RecyclerItemClickListener;
import com.example.pl.model.DataPengajuanModel;
import com.example.pl.model.SearchIDPMDIVModel;
import com.example.pl.response.ListPengajuanResponse;
import com.example.pl.retrofit.RetrofitClient;
import com.example.pl.retrofit.RetrofitInterface;
import com.example.pl.utils.DetectConnection;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

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

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentListPengajuan extends Fragment {

    private ListPengajuanAdapter listPengajuanAdapter = new ListPengajuanAdapter();
    private DetectConnection detectConnection;

    RecyclerView rvListPengajuan;
    ArrayList<String> nampungDetailData = new ArrayList<>();
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int cek_hrd = 0;
    ProgressDialog progressDialog;
    SwipeRefreshLayout refreshListPengajuan;
    public FragmentListPengajuan() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_pengajuan, container, false);
        pref = getActivity().getApplicationContext().getSharedPreferences("data", 0); // 0 - for private mode
        editor = pref.edit();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Harap Tunggu...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        rvListPengajuan = (RecyclerView)view.findViewById(R.id.rvListPengajuan);
        refreshListPengajuan = (SwipeRefreshLayout)view.findViewById(R.id.refreshListPengajuan);
        detectConnection = new DetectConnection(getActivity());

        if(!"HRD".equalsIgnoreCase(pref.getString("level", null))){
            getSingleListPengajuan(pref.getString("nip", null));
            cek_hrd = 1;
        } else {
            getAllListPengajuan();
        }

        rvListPengajuan.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        rvListPengajuan.setAdapter(listPengajuanAdapter);

        refreshListPengajuan.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshListPengajuan.setRefreshing(true);
                if(cek_hrd == 0)
                    getAllListPengajuan();
                else
                    getSingleListPengajuan(pref.getString("nip", null));
                refreshListPengajuan.setRefreshing(false);
            }
        });

        refreshListPengajuan.post(new Runnable() {
            @Override
            public void run() {
                refreshListPengajuan.setRefreshing(true);
                if(cek_hrd == 0)
                    getAllListPengajuan();
                else
                    getSingleListPengajuan(pref.getString("nip", null));
                refreshListPengajuan.setRefreshing(false);
            }
        });

        rvListPengajuan.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                final String id_pengajuan = ((MaterialTextView)rvListPengajuan.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.list_pengajuan_id_pengajuan)).getText().toString(),
                nama_divisi = ((MaterialTextView)rvListPengajuan.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.list_pengajuan_nama_divisi)).getText().toString(),
                nama_pm = ((MaterialTextView)rvListPengajuan.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.list_pengajuan_nama_pm)).getText().toString(),
                nip = ((MaterialTextView)rvListPengajuan.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.list_pengajuan_nip)).getText().toString(),
                        nama = ((MaterialTextView)rvListPengajuan.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.list_pengajuan_nama_pegawai)).getText().toString(),
                        hari = ((MaterialTextView)rvListPengajuan.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.list_pengajuan_hari)).getText().toString(),
                        tanggal = ((MaterialTextView)rvListPengajuan.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.list_pengajuan_tanggal)).getText().toString(),
                        jam_mulai = ((MaterialTextView)rvListPengajuan.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.list_pengajuan_jam_mulai)).getText().toString(),
                        jam_selesai = ((MaterialTextView)rvListPengajuan.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.list_pengajuan_jam_selesai)).getText().toString(),
                        keterangan = ((MaterialTextView)rvListPengajuan.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.list_pengajuan_keterangan)).getText().toString(),
                        status = ((MaterialTextView)rvListPengajuan.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.list_pengajuan_status)).getText().toString(),
                        gaji = ((MaterialTextView)rvListPengajuan.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.list_gaji)).getText().toString();

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
                if(cek_hrd == 0){
                    alert.setPositiveButton("Terima", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.show();
                            if(detectConnection.isConnectingToInternet()){
                                RetrofitInterface retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
                                Call<DataPengajuanModel> call = retrofitInterface.terimaDataPengajuan("terima_pengajuan",  id_pengajuan);
                                call.enqueue(new Callback<DataPengajuanModel>() {
                                    @Override
                                    public void onResponse(Call<DataPengajuanModel> call, Response<DataPengajuanModel> response) {
                                        Toast.makeText(getActivity().getApplicationContext(), "Lembur berhasil diterima", Toast.LENGTH_SHORT).show();
                                        getAllListPengajuan();
                                    }

                                    @Override
                                    public void onFailure(Call<DataPengajuanModel> call, Throwable t) {
                                        Toast.makeText(getActivity().getApplicationContext(), "Konfirmasi gagal", Toast.LENGTH_SHORT).show();
                                        getAllListPengajuan();
                                    }
                                });
                            }
                            dialog.dismiss();
                        }
                    })
                            .setNegativeButton("Tolak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressDialog.show();
                                    if(detectConnection.isConnectingToInternet()){
                                        RetrofitInterface retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
                                        Call<DataPengajuanModel> call = retrofitInterface.tolakDataPegawai("tolak_pengajuan", id_pengajuan);
                                        call.enqueue(new Callback<DataPengajuanModel>() {
                                            @Override
                                            public void onResponse(Call<DataPengajuanModel> call, Response<DataPengajuanModel> response) {
                                                Toast.makeText(getActivity().getApplicationContext(), "Lembur berhasil ditolak", Toast.LENGTH_SHORT).show();
                                                getAllListPengajuan();
                                            }

                                            @Override
                                            public void onFailure(Call<DataPengajuanModel> call, Throwable t) {
                                                Toast.makeText(getActivity().getApplicationContext(), "Konfirmasi Gagal", Toast.LENGTH_SHORT).show();
                                                getAllListPengajuan();
                                            }
                                        });
                                    }
                                    dialog.dismiss();
                                }
                            });
                } else {
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
                 alert.show();
            }
        }));
        return view;
    }

    private void getAllListPengajuan(){
        progressDialog.show();
        if(detectConnection.isConnectingToInternet()){
            RetrofitInterface retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
            Call<ListPengajuanResponse> call = retrofitInterface.getAllListPengajuan("datapengajuan_menunggu");
            call.enqueue(new Callback<ListPengajuanResponse>() {
                @Override
                public void onResponse(Call<ListPengajuanResponse> call, Response<ListPengajuanResponse> response) {
                    if(response.body() != null){
                        ListPengajuanResponse pengajuanResponse = response.body();
                        listPengajuanAdapter.setDataPengajuanModelList(pengajuanResponse.getResult());
                        listPengajuanAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    } else {
                        progressDialog.dismiss();
                        new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                .setMessage("Tidak Ada Data Pengajuan")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                    }
                }

                @Override
                public void onFailure(Call<ListPengajuanResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                            .setMessage("Tidak Ada Data Pengajuan")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                }
            });
        } else
            progressDialog.dismiss();
    }

    private void getSingleListPengajuan(final String nip) {
        progressDialog.show();
        if(detectConnection.isConnectingToInternet()){
            RetrofitInterface retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
            Call<ListPengajuanResponse> call = retrofitInterface.getSingleListPengajuan("singlepengajuan_menunggu", nip);
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
                    progressDialog.dismiss();
                    new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                            .setMessage("Tidak Ada Data Pengajuan")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                }
            });
        } else
            progressDialog.dismiss();
    }

}
