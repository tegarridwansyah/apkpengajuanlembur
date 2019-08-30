package com.example.pl.fragment;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.pl.R;
import com.example.pl.activity.ActivityHome;
import com.example.pl.adapter.DataDivisiAdapter;
import com.example.pl.adapter.RecyclerItemClickListener;
import com.example.pl.dialog.FullScreenDialog_DataDivisi;
import com.example.pl.response.DataDivisiResponse;
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
public class FragmentDataDivisi extends Fragment {

    DataDivisiAdapter divisiAdapter = new DataDivisiAdapter();
    DetectConnection detectConnection;

    FloatingActionButton buttonAdd;
    RecyclerView rvDataDivisi;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int cek_hrd = 0;
    ProgressDialog progressDialog;
    SwipeRefreshLayout refreshDataDivisi;
    ImageView networkError;
    public FragmentDataDivisi() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_data_divisi, container, false);
        pref = getActivity().getApplicationContext().getSharedPreferences("data", 0); // 0 - for private mode
        editor = pref.edit();
        buttonAdd = (FloatingActionButton)view.findViewById(R.id.button_add_divisi);
        rvDataDivisi = (RecyclerView)view.findViewById(R.id.rvDataDivisi);
        refreshDataDivisi = (SwipeRefreshLayout)view.findViewById(R.id.refreshDataDivisi);
        networkError = (ImageView)view.findViewById(R.id.data_divisi_error);
        detectConnection = new DetectConnection(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Harap Tunggu...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        networkError.setVisibility(View.INVISIBLE);

        if(!"HRD".equalsIgnoreCase(pref.getString("level", null))){
            buttonAdd.setVisibility(View.GONE);
            cek_hrd = 1;
        }

        rvDataDivisi.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        rvDataDivisi.setAdapter(divisiAdapter);

        refreshDataDivisi.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDataDivisi.setRefreshing(true);
                getDataDivisi();
                refreshDataDivisi.setRefreshing(false);
            }
        });

        refreshDataDivisi.post(new Runnable() {
            @Override
            public void run() {
                refreshDataDivisi.setRefreshing(true);
                getDataDivisi();
                refreshDataDivisi.setRefreshing(false);
            }
        });

        getDataDivisi();

        rvDataDivisi.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String id_divisi = ((TextView)rvDataDivisi.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.data_divisi_id_divisi)).getText().toString(),
                        nama_divisi = ((TextView)rvDataDivisi.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.data_divisi_nama_pm)).getText().toString();

                if(cek_hrd == 0) {
                    FullScreenDialog_DataDivisi dialog = new FullScreenDialog_DataDivisi(id_divisi, nama_divisi);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    dialog.show(ft, FullScreenDialog_DataDivisi.TAG);
                }
            }
        }));

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullScreenDialog_DataDivisi dialog = new FullScreenDialog_DataDivisi();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, FullScreenDialog_DataDivisi.TAG);
            }
        });
        return view;
    }

    private void getDataDivisi() {
        progressDialog.show();
        if(detectConnection.isConnectingToInternet()){
            RetrofitInterface retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
            Call<DataDivisiResponse> call = retrofitInterface.getDataDivisi();
            call.enqueue(new Callback<DataDivisiResponse>() {
                @Override
                public void onResponse(Call<DataDivisiResponse> call, Response<DataDivisiResponse> response) {
                    if (cek_hrd == 0)
                        buttonAdd.setVisibility(View.VISIBLE);
                    rvDataDivisi.setVisibility(View.VISIBLE);
                    networkError.setVisibility(View.INVISIBLE);
                    DataDivisiResponse dataDivisiResponse = response.body();
                    divisiAdapter.setDivisiModelList(dataDivisiResponse.getResult());
                    divisiAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<DataDivisiResponse> call, Throwable t) {
                    buttonAdd.setVisibility(View.INVISIBLE);
                    rvDataDivisi.setVisibility(View.INVISIBLE);
                    networkError.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity().getApplicationContext(), "Something Wrong", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } else
            progressDialog.dismiss();
    }

}
