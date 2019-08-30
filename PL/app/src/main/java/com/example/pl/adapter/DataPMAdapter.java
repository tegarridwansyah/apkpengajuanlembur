package com.example.pl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pl.R;
import com.example.pl.model.DataPMModel;

import java.util.ArrayList;
import java.util.List;

public class DataPMAdapter extends RecyclerView.Adapter<DataPMAdapter.ViewHolder> {
    public List<DataPMModel> dataPMModelList = new ArrayList<>();
    private Listener listener;
    Context context;

    public DataPMAdapter(){

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_list_data_pm, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final DataPMModel dataPMModel = dataPMModelList.get(position);
        holder.getId_pm().setText(dataPMModel.getId_pm());
        holder.getNama_pm().setText(dataPMModel.getNama_pm());
        holder.setListener(new ViewHolder.Listener() {
            @Override
            public void onClick() {
                if(position < dataPMModelList.size()){
                    if(dataPMModel != null && listener != null){
                        listener.onClick(dataPMModel);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataPMModelList.size();
    }

    public DataPMAdapter(Context context, List<DataPMModel> dataPMModelList){
        this.context = context;
        this.dataPMModelList = dataPMModelList;
    }

    public void setDataPMModelList(List<DataPMModel> dataPMModelList) {
        this.dataPMModelList = dataPMModelList;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener{
        void onClick(DataPMModel dataPMModel);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView id_pm, nama_pm;
        private Listener listener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            id_pm = itemView.findViewById(R.id.list_pengajuan_nama_pegawai);
            nama_pm = itemView.findViewById(R.id.list_pengajuan_tanggal);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        listener.onClick();
                    }
                }
            });
        }

        public TextView getId_pm() {
            return id_pm;
        }

        public TextView getNama_pm() {
            return nama_pm;
        }

        public void setListener(Listener listener) {
            this.listener = listener;
        }

        interface Listener{
            void onClick();
        }
    }
}
