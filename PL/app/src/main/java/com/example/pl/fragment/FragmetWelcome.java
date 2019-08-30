package com.example.pl.fragment;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.pl.R;
import com.example.pl.activity.ActivityHome;
import com.example.pl.adapter.DataPegawaiAdapter;
import com.example.pl.response.DataDivisiResponse;
import com.example.pl.response.DataPMResponse;
import com.example.pl.response.DataPegawaiResponse;
import com.example.pl.retrofit.RetrofitClient;
import com.example.pl.retrofit.RetrofitInterface;
import com.example.pl.utils.DetectConnection;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmetWelcome extends Fragment {

    private DataPegawaiAdapter pegawaiAdapter = new DataPegawaiAdapter();

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    TextView tvNama, tvDivisi, tvTotalPegawai, tvTotalDivisi, tvTotalPM;
    CardView cardViewPegawai, cardViewDivisi, cardViewPM;
    ProgressDialog progressDialog;
    DetectConnection detectConnection;
    public FragmetWelcome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        pref = getActivity().getApplicationContext().getSharedPreferences("data", 0); // 0 - for private mode
        editor = pref.edit();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Harap Tunggu...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        tvNama = (TextView)view.findViewById(R.id.nama_welcome);
        tvDivisi = (TextView)view.findViewById(R.id.divisi_welcome);
        tvTotalPegawai = (TextView)view.findViewById(R.id.total_pegawai_welcome);
        tvTotalDivisi = (TextView)view.findViewById(R.id.total_divisi_welcome);
        tvTotalPM = (TextView)view.findViewById(R.id.total_pm_welcome);
        cardViewPegawai = (CardView)view.findViewById(R.id.cardViewTotalPegawai);
        cardViewDivisi = (CardView)view.findViewById(R.id.cardViewTotalDivisi);
        cardViewPM = (CardView)view.findViewById(R.id.cardViewTotalPM);
        detectConnection = new DetectConnection(getActivity());

        if (detectConnection.isConnectingToInternet()){
            progressDialog.show();
            final RetrofitInterface retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
            Call<DataPegawaiResponse> call = retrofitInterface.getDataPegawai();
            call.enqueue(new Callback<DataPegawaiResponse>() {
                @Override
                public void onResponse(Call<DataPegawaiResponse> call, Response<DataPegawaiResponse> response) {
                    tvTotalPegawai.setText(String.valueOf(response.body().getResult().size()));

                    Call<DataDivisiResponse> call1 = retrofitInterface.getDataDivisi();
                    call1.enqueue(new Callback<DataDivisiResponse>() {
                        @Override
                        public void onResponse(Call<DataDivisiResponse> call, Response<DataDivisiResponse> response) {
                            tvTotalDivisi.setText(String.valueOf(response.body().getResult().size()));

                            Call<DataPMResponse> call2 = retrofitInterface.getDataPM();
                            call2.enqueue(new Callback<DataPMResponse>() {
                                @Override
                                public void onResponse(Call<DataPMResponse> call, Response<DataPMResponse> response) {
                                    tvTotalPM.setText(String.valueOf(response.body().getResult().size()));
                                }

                                @Override
                                public void onFailure(Call<DataPMResponse> call, Throwable t) {

                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<DataDivisiResponse> call, Throwable t) {

                        }
                    });
                }

                @Override
                public void onFailure(Call<DataPegawaiResponse> call, Throwable t) {

                }
            });

            progressDialog.dismiss();
        } else {
            progressDialog.dismiss();
            Toast.makeText(getActivity().getApplicationContext(), "Tidak ada koneksi internet", Toast.LENGTH_LONG).show();
        }

        tvNama.setText(pref.getString("nama", null) + " - " + pref.getString("nip", null).substring(3, 6));
        tvDivisi.setText(pref.getString("nama_divisi", null));

        cardViewPegawai.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                NavigationView navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view);
                navigationView.getMenu().getItem(1).setChecked(true);
                MaterialToolbar toolbar = (MaterialToolbar) getActivity().findViewById(R.id.toolbar);
                toolbar.setTitle("Data Pegawai");
                ActivityHome.navItemIndex = 1;
                ActivityHome.CURRENT_TAG = ActivityHome.TAG_DATA_PEGAWAI;

                Fragment fragment = new FragmentDataPegawai();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment);
                fragmentTransaction.commitAllowingStateLoss();
            }
        });

        cardViewDivisi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationView navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view);
                navigationView.getMenu().getItem(2).setChecked(true);
                MaterialToolbar toolbar = (MaterialToolbar) getActivity().findViewById(R.id.toolbar);
                toolbar.setTitle("Data Divisi");
                ActivityHome.navItemIndex = 2;
                ActivityHome.CURRENT_TAG = ActivityHome.TAG_DATA_DIVISI;

                Fragment fragment = new FragmentDataDivisi();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment);
                fragmentTransaction.commitAllowingStateLoss();
            }
        });

        cardViewPM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationView navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view);
                navigationView.getMenu().getItem(3).setChecked(true);
                MaterialToolbar toolbar = (MaterialToolbar) getActivity().findViewById(R.id.toolbar);
                toolbar.setTitle("Data PM");
                ActivityHome.navItemIndex = 3;
                ActivityHome.CURRENT_TAG = ActivityHome.TAG_PM;

                Fragment fragment = new FragmentDataPM();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment);
                fragmentTransaction.commitAllowingStateLoss();
            }
        });

        return view;
    }

}
