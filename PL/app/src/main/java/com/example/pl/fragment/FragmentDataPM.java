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
import com.example.pl.adapter.DataPMAdapter;
import com.example.pl.adapter.RecyclerItemClickListener;
import com.example.pl.dialog.FullScreenDialog_DataPM;
import com.example.pl.response.DataPMResponse;
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
public class FragmentDataPM extends Fragment {

    DetectConnection detectConnection;
    DataPMAdapter dataPMAdapter = new DataPMAdapter();

    RecyclerView rvDataPM;
    FloatingActionButton buttonAdd;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int cek_hrd_pm = 0;
    ProgressDialog progressDialog;
    SwipeRefreshLayout refreshDataPM;
    ImageView networkError;
    public FragmentDataPM() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_data_pm, container, false);
        pref = getActivity().getApplicationContext().getSharedPreferences("data", 0); // 0 - for private mode
        editor = pref.edit();
        rvDataPM = (RecyclerView)view.findViewById(R.id.rvDataPM);
        buttonAdd = (FloatingActionButton)view.findViewById(R.id.button_add_pm);
        refreshDataPM = (SwipeRefreshLayout)view.findViewById(R.id.refreshDataPM);
        networkError = (ImageView)view.findViewById(R.id.data_pm_error);
        detectConnection = new DetectConnection(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Harap Tunggu...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        networkError.setVisibility(View.INVISIBLE);
        if(!"HRD".equalsIgnoreCase(pref.getString("level", null))){
            buttonAdd.setVisibility(View.GONE);
            cek_hrd_pm = 1;
        }
        
        rvDataPM.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        rvDataPM.setAdapter(dataPMAdapter);

        refreshDataPM.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDataPM.setRefreshing(true);
                getDataPM();
                refreshDataPM.setRefreshing(false);
            }
        });

        refreshDataPM.post(new Runnable() {
            @Override
            public void run() {
                refreshDataPM.setRefreshing(true);
                getDataPM();
                refreshDataPM.setRefreshing(false);
            }
        });
        
        getDataPM();
        
        rvDataPM.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String id_pm = ((TextView)rvDataPM.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.list_pengajuan_nama_pegawai)).getText().toString(),
                nama_pm = ((TextView)rvDataPM.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.list_pengajuan_tanggal)).getText().toString();

                if (cek_hrd_pm == 0) {
                    FullScreenDialog_DataPM dialog = new FullScreenDialog_DataPM(id_pm, nama_pm);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    dialog.show(ft, FullScreenDialog_DataPM.TAG);
                }
            }
        }));

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullScreenDialog_DataPM dialog = new FullScreenDialog_DataPM();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, FullScreenDialog_DataPM.TAG);
            }
        });
        
        return view;
    }

    private void getDataPM() {
        progressDialog.show();
        if(detectConnection.isConnectingToInternet()){
            RetrofitInterface retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
            Call<DataPMResponse> call = retrofitInterface.getDataPM();
            call.enqueue(new Callback<DataPMResponse>() {
                @Override
                public void onResponse(Call<DataPMResponse> call, Response<DataPMResponse> response) {
                    if (cek_hrd_pm == 0)
                        buttonAdd.setVisibility(View.VISIBLE);
                    rvDataPM.setVisibility(View.VISIBLE);
                    networkError.setVisibility(View.INVISIBLE);
                    DataPMResponse dataPMResponse = response.body();
                    dataPMAdapter.setDataPMModelList(dataPMResponse.getResult());
                    dataPMAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<DataPMResponse> call, Throwable t) {
                    buttonAdd.setVisibility(View.INVISIBLE);
                    rvDataPM.setVisibility(View.INVISIBLE);
                    networkError.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity().getApplicationContext(), "Something Wrong", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } else
            progressDialog.dismiss();
    }

}
