package com.example.pl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pl.R;
import com.example.pl.model.DataDivisiModel;

import java.util.ArrayList;
import java.util.List;

public class DataDivisiAdapter extends RecyclerView.Adapter<DataDivisiAdapter.ViewHolder> {
    public List<DataDivisiModel> divisiModelList = new ArrayList<>();
    private Listener listener;
    Context context;

    public DataDivisiAdapter(){

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_list_data_divisi, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final DataDivisiModel divisiModel = divisiModelList.get(position);
        holder.getId_divisi().setText(divisiModel.getId_divisi());
        holder.getNama_divisi().setText(divisiModel.getNama_divisi());
        holder.setListener(new ViewHolder.Listener() {
            @Override
            public void onClick() {
                if(position < divisiModelList.size()){
                    if(divisiModel != null && listener != null){
                        listener.onClick(divisiModel);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return divisiModelList.size();
    }

    public DataDivisiAdapter(Context context, List<DataDivisiModel> list){
        this.context = context;
        this.divisiModelList = list;
    }

    public void setDivisiModelList(List<DataDivisiModel> divisiModelList) {
        this.divisiModelList = divisiModelList;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener{
        void onClick(DataDivisiModel divisiModel);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView id_divisi, nama_divisi;
        private Listener listener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            id_divisi = itemView.findViewById(R.id.data_divisi_id_divisi);
            nama_divisi = itemView.findViewById(R.id.data_divisi_nama_pm);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null)
                        listener.onClick();
                }
            });
        }

        public TextView getId_divisi() {
            return id_divisi;
        }

        public TextView getNama_divisi() {
            return nama_divisi;
        }

        public void setListener(Listener listener) {
            this.listener = listener;
        }

        interface Listener {
            void onClick();
        }
    }
}
