package com.example.pl.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.pl.R;
import com.example.pl.activity.ActivityHome;
import com.example.pl.activity.ActivityLogin;
import com.example.pl.adapter.DataPegawaiAdapter;
import com.example.pl.adapter.RecyclerItemClickListener;
import com.example.pl.dialog.FullScreenDialog_DataPegawai;
import com.example.pl.response.DataPegawaiResponse;
import com.example.pl.retrofit.RetrofitClient;
import com.example.pl.retrofit.RetrofitInterface;
import com.example.pl.utils.DetectConnection;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDataPegawai extends Fragment {

    private DataPegawaiAdapter pegawaiAdapter = new DataPegawaiAdapter();
    public DetectConnection detectConnection;

    FloatingActionButton buttonAdd;
    RecyclerView rvDataPegawai;
    TextView nip, nama;
    public ImageView networkError;
    Context mContext = null;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int cek_hrd = 0;
    ProgressDialog progressDialog;
    SwipeRefreshLayout refreshDataPegawai;
    public FragmentDataPegawai() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_data_pegawai, container, false);
        pref = getActivity().getApplicationContext().getSharedPreferences("data", 0); // 0 - for private mode
        editor = pref.edit();
        detectConnection = new DetectConnection(getActivity());
        buttonAdd = (FloatingActionButton)view.findViewById(R.id.button_add_data_pegawai);
        rvDataPegawai = (RecyclerView)view.findViewById(R.id.recyclerViewDataPegawai);
        networkError = (ImageView)view.findViewById(R.id.no_connection);
        refreshDataPegawai = (SwipeRefreshLayout)view.findViewById(R.id.refreshDataPegawai);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Harap Tunggu...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        if(!"HRD".equalsIgnoreCase(pref.getString("level", null))){
            buttonAdd.setVisibility(View.GONE);
            cek_hrd = 1;
        }

        rvDataPegawai.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        rvDataPegawai.setAdapter(pegawaiAdapter);

        refreshDataPegawai.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDataPegawai.setRefreshing(true);
                getDataPegawai();
                refreshDataPegawai.setRefreshing(false);
            }
        });

        refreshDataPegawai.post(new Runnable() {
            @Override
            public void run() {
                refreshDataPegawai.setRefreshing(true);
                getDataPegawai();
                refreshDataPegawai.setRefreshing(false);
            }
        });

        getDataPegawai();
        networkError.setVisibility(View.INVISIBLE);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullScreenDialog_DataPegawai dialog = new FullScreenDialog_DataPegawai();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, FullScreenDialog_DataPegawai.TAG);
            }
        });

        rvDataPegawai.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String tvNip = ((TextView)rvDataPegawai.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.data_pegawai_nip_pegawai)).getText().toString(),
                        tvNama = ((TextView)rvDataPegawai.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.data_pegawai_nama_pegawai)).getText().toString(),
                        tvDivisi = ((TextView)rvDataPegawai.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.data_pegawai_nama_divisi)).getText().toString(),
                        tvNo_telp = ((TextView)rvDataPegawai.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.data_pegawai_no_telp)).getText().toString(),
                        tvAlamat = ((TextView)rvDataPegawai.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.data_pegawai_alamat)).getText().toString(),
                        tvGaji = ((TextView)rvDataPegawai.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.data_pegawai_gaji)).getText().toString();
                if(cek_hrd == 0) {
                    FullScreenDialog_DataPegawai dialog = new FullScreenDialog_DataPegawai(tvNip, tvNama, tvDivisi, tvNo_telp, tvAlamat, tvGaji);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    dialog.show(ft, FullScreenDialog_DataPegawai.TAG);
                }
            }
        }));
        return view;
    }

    public void getDataPegawai() {
        progressDialog.show();
        if(detectConnection.isConnectingToInternet()){
            RetrofitInterface retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
            Call<DataPegawaiResponse> call = retrofitInterface.getDataPegawai();
            call.enqueue(new Callback<DataPegawaiResponse>() {
                @Override
                public void onResponse(Call<DataPegawaiResponse> call, Response<DataPegawaiResponse> response) {
                    rvDataPegawai.setVisibility(View.VISIBLE);
                    if (cek_hrd == 0)
                        buttonAdd.setVisibility(View.VISIBLE);
                    networkError.setVisibility(View.INVISIBLE);
                    DataPegawaiResponse dataPegawaiResponse = response.body();
                    pegawaiAdapter.setDataPegawaiList(dataPegawaiResponse.getResult());
                    pegawaiAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<DataPegawaiResponse> call, Throwable t) {
                    networkError.setVisibility(View.VISIBLE);
                    rvDataPegawai.setVisibility(View.INVISIBLE);
                    buttonAdd.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "Something Wrong", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            networkError.setVisibility(View.VISIBLE);
            rvDataPegawai.setVisibility(View.INVISIBLE);
            buttonAdd.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity().getApplicationContext(), "Network Error", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

}
