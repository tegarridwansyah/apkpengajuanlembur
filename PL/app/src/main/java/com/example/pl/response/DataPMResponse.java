package com.example.pl.response;

import com.example.pl.model.DataPMModel;

import java.util.List;

public class DataPMResponse {
    private String status, pesan, url, time;
    private List<DataPMModel> result;

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

    public List<DataPMModel> getResult() {
        return result;
    }

    public void setResult(List<DataPMModel> result) {
        this.result = result;
    }
}
