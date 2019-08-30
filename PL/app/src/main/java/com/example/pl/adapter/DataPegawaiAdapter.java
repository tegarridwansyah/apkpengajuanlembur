package com.example.pl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pl.R;
import com.example.pl.model.DataPegawaiModel;

import java.util.ArrayList;
import java.util.List;

public class DataPegawaiAdapter extends RecyclerView.Adapter<DataPegawaiAdapter.ViewHolder> {
    public List<DataPegawaiModel> dataPegawaiList = new ArrayList<>();
    private Listener listener;
    Context context;

    public DataPegawaiAdapter(){

    }

    public DataPegawaiAdapter(Context context, List<DataPegawaiModel> list){
        this.context = context;
        this.dataPegawaiList = list;
    }

    public void setDataPegawaiList(List<DataPegawaiModel> dataPegawaiList) {
        this.dataPegawaiList = dataPegawaiList;
    }

    public void setListener (Listener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_list_data_pegawai, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final DataPegawaiModel pegawaiModel = dataPegawaiList.get(position);
        holder.getNip().setText(pegawaiModel.getNip());
        holder.getNama().setText(pegawaiModel.getNama());
        holder.getDivisi().setText(pegawaiModel.getNama_divisi());
        holder.getNo_telp().setText(pegawaiModel.getNo_telp());
        holder.getAlamat().setText(pegawaiModel.getAlamat());
        holder.getGaji().setText(pegawaiModel.getGaji());
        holder.setListener(new ViewHolder.Listener() {
            @Override
            public void onClick() {
                if (position < dataPegawaiList.size()){
                    if(pegawaiModel != null && listener != null){
                        listener.onClick(pegawaiModel);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataPegawaiList.size();
    }

    public interface Listener {
        void onClick(DataPegawaiModel dataPegawai);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nip, nama, divisi, no_telp, alamat, gaji;
        private Listener listener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nip = itemView.findViewById(R.id.data_pegawai_nip_pegawai);
            nama = itemView.findViewById(R.id.data_pegawai_nama_pegawai);
            divisi = itemView.findViewById(R.id.data_pegawai_nama_divisi);
            no_telp = itemView.findViewById(R.id.data_pegawai_no_telp);
            alamat = itemView.findViewById(R.id.data_pegawai_alamat);
            gaji = itemView.findViewById(R.id.data_pegawai_gaji);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null)
                        listener.onClick();
                }
            });
        }

        public TextView getNip() {
            return nip;
        }

        public TextView getNama() {
            return nama;
        }

        public TextView getDivisi() {
            return divisi;
        }

        public TextView getNo_telp() {
            return no_telp;
        }

        public TextView getAlamat() {
            return alamat;
        }

        public TextView getGaji() {
            return gaji;
        }

        public void setListener(Listener listener) {
            this.listener = listener;
        }

        interface Listener {
            void onClick();
        }
    }
}
