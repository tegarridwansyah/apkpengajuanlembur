package com.example.pl.response;

import com.example.pl.model.DataPengajuanModel;

import java.util.List;

public class ListPengajuanResponse {
    private String status, pesan, url, time;
    private List<DataPengajuanModel> result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<DataPengajuanModel> getResult() {
        return result;
    }

    public void setResult(List<DataPengajuanModel> result) {
        this.result = result;
    }
}
