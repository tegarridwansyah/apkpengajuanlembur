package com.example.pl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pl.R;
import com.example.pl.model.DataPengajuanModel;

import java.util.ArrayList;
import java.util.List;

public class ListPengajuanAdapter extends RecyclerView.Adapter<ListPengajuanAdapter.ViewHolder> {
    public List<DataPengajuanModel> dataPengajuanModelList = new ArrayList<>();
    private Listener listener;
    Context context;

    public ListPengajuanAdapter(){

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_list_pengajuan, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final DataPengajuanModel dataPengajuanModel = dataPengajuanModelList.get(position);
        holder.getNip().setText(dataPengajuanModel.getNip());
        holder.getNama().setText(dataPengajuanModel.getNama());
        holder.getHari().setText(dataPengajuanModel.getHari());
        holder.getJam_mulai().setText(dataPengajuanModel.getJam_mulai());
        holder.getJam_selesai().setText(dataPengajuanModel.getJam_selesai());
        holder.getKeterangan().setText(dataPengajuanModel.getKeterangan());
        holder.getStatus().setText(dataPengajuanModel.getStatus());
        holder.getTanggal().setText(dataPengajuanModel.getTanggal());
        holder.getId_pengajuan().setText(dataPengajuanModel.getId_pengajuan());
        holder.getNama_divisi().setText(dataPengajuanModel.getNama_divisi());
        holder.getNama_pm().setText(dataPengajuanModel.getNama_pm());
        holder.getGaji().setText(dataPengajuanModel.getGaji());
        holder.setListener(new ListPengajuanAdapter.ViewHolder.Listener() {
            @Override
            public void onClick() {
                if(position < dataPengajuanModelList.size()){
                    if(dataPengajuanModel != null && listener != null){
                        listener.onClick(dataPengajuanModel);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataPengajuanModelList.size();
    }

    public ListPengajuanAdapter(Context context, List<DataPengajuanModel> dataPengajuanModelList){
        this.context = context;
        this.dataPengajuanModelList = dataPengajuanModelList;
    }

    public void setDataPengajuanModelList(List<DataPengajuanModel> dataPengajuanModelList) {
        this.dataPengajuanModelList = dataPengajuanModelList;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener{
        void onClick(DataPengajuanModel dataPengajuanModel);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nip, nama, hari, tanggal, jam_mulai, jam_selesai, keterangan, status, id_pengajuan, nama_divisi, nama_pm, gaji;
        private Listener listener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nip = itemView.findViewById(R.id.list_pengajuan_nip);
            nama = itemView.findViewById(R.id.list_pengajuan_nama_pegawai);
            hari = itemView.findViewById(R.id.list_pengajuan_hari);
            jam_mulai = itemView.findViewById(R.id.list_pengajuan_jam_mulai);
            jam_selesai = itemView.findViewById(R.id.list_pengajuan_jam_selesai);
            keterangan = itemView.findViewById(R.id.list_pengajuan_keterangan);
            status = itemView.findViewById(R.id.list_pengajuan_status);
            tanggal = itemView.findViewById(R.id.list_pengajuan_tanggal);
            id_pengajuan = itemView.findViewById(R.id.list_pengajuan_id_pengajuan);
            nama_divisi = itemView.findViewById(R.id.list_pengajuan_nama_divisi);
            nama_pm = itemView.findViewById(R.id.list_pengajuan_nama_pm);
            gaji = itemView.findViewById(R.id.list_gaji);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        listener.onClick();
                    }
                }
            });
        }

        public TextView getNama() {
            return nama;
        }

        public TextView getTanggal() {
            return tanggal;
        }

        public TextView getId_pengajuan() {
            return id_pengajuan;
        }

        public TextView getNama_pm() {
            return nama_pm;
        }

        public TextView getNama_divisi() {
            return nama_divisi;
        }

        public TextView getNip() {
            return nip;
        }

        public TextView getHari() {
            return hari;
        }

        public TextView getJam_mulai() {
            return jam_mulai;
        }

        public TextView getJam_selesai() {
            return jam_selesai;
        }

        public TextView getKeterangan() {
            return keterangan;
        }

        public TextView getStatus() {
            return status;
        }

        public TextView getGaji() {
            return gaji;
        }

        public void setListener(Listener listener) {
            this.listener = listener;
        }

        interface Listener{
            void onClick();
        }
    }
}
