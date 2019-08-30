package com.example.pl.retrofit;

import com.example.pl.model.DataDivisiModel;
import com.example.pl.model.DataPMModel;
import com.example.pl.model.DataPegawaiModel;
import com.example.pl.model.DataPengajuanModel;
import com.example.pl.model.LoginPegawaiModel;
import com.example.pl.model.PushNotifModel;
import com.example.pl.response.DataDivisiResponse;
import com.example.pl.response.DataPMResponse;
import com.example.pl.response.DataPegawaiResponse;
import com.example.pl.response.ListPengajuanResponse;

import java.sql.Blob;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitInterface {

    //L O G I N  P E G A W A I
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("ControllerKaryawan.php")
    Call<String> loginPegawai(
            @Field("action") String action,
            @Field("username") String username,
            @Field("pass") String pass
    );

    //D A T A  P E G A W A I
    @GET("ControllerKaryawan.php?action=fetch_datapegawai")
    Call<DataPegawaiResponse> getDataPegawai();

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("ControllerKaryawan.php")
    Call<DataPegawaiModel> insertPegawai(
            @Field("action") String action,
            @Field("nip") String nip,
            @Field("nama") String nama,
            @Field("alamat") String alamat,
            @Field("no_telp") String no_telp,
            @Field("id_divisi") String id_divisi,
            @Field("gaji") String gaji
    );

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("ControllerKaryawan.php")
    Call<DataPegawaiModel> updateDataPegawai(
            @Field("action") String action,
            @Field("nip") String nip,
            @Field("nama") String nama,
            @Field("alamat") String alamat,
            @Field("no_telp") String no_telp,
            @Field("id_divisi") String id_divisi,
            @Field("gaji") String gaji
    );

    @GET("ControllerKaryawan.php")
    Call<DataPegawaiModel> deleteDataPegawai(
            @Query("action") String action,
            @Query("nip") String nip
    );

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("ControllerKaryawan.php")
    Call<String> searchDataPegawai(
            @Field("action") String action,
            @Field("nip") String nip
    );

    //D A T A  D I V I S I
    @GET("ControllerDivisi.php?action=fetch_datadivisi")
    Call<DataDivisiResponse> getDataDivisi();

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("ControllerDivisi.php")
    Call<DataDivisiModel> insertDataDivisi(
            @Field("action") String action,
            @Field("id_divisi") String id_divisi,
            @Field("nama_divisi") String nama_divisi
    );

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("ControllerDivisi.php")
    Call<DataDivisiModel> updateDataDivisi(
            @Field("action") String action,
            @Field("id_divisi") String id_divisi,
            @Field("nama_divisi") String nama_divisi
    );

    @GET("ControllerDivisi.php")
    Call<DataDivisiModel> deleteDataDivisi(
            @Query("action") String action,
            @Query("id_divisi") String id_divisi
    );

    @GET("ControllerDivisi.php")
    Call<String> searchIdDivisi(
            @Query("action") String action,
            @Query("nama_divisi") String nama_divisi
    );

    //D  A T A  P M
    @GET("ControllerPM.php?action=fetch_datapm")
    Call<DataPMResponse> getDataPM();

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("ControllerPM.php")
    Call<DataPMModel> insertDataPM(
            @Field("action") String action,
            @Field("id_pm") String id_pm,
            @Field("nama_pm") String nama_pm
    );

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("ControllerPM.php")
    Call<DataPMModel> updateDataPM(
            @Field("action") String action,
            @Field("id_pm") String id_pm,
            @Field("nama_pm") String nama_pm
    );

    @GET("ControllerPM.php")
    Call<DataPMModel> deleteDataPM(
            @Query("action") String action,
            @Query("id_pm") String id_pm
    );

    @GET("ControllerPM.php")
    Call<String>searchIDPMDIV(
            @Query("action") String action,
            @Query("nama_pm") String nama_pm,
            @Query("nama_divisi") String nama_divisi
    );

    //D A T A  P E N G A J U A N
    @GET("ControllerPengajuan.php")
    Call<ListPengajuanResponse> getAllListPengajuan(
            @Query("action") String action
    );

    @GET("ControllerPengajuan.php")
    Call<ListPengajuanResponse> getSingleListPengajuan(
            @Query("action") String action,
            @Query("nip") String nip
    );

    @GET("ControllerPengajuan.php")
    Call<String> getSingleListPengajuanByIdPengajuan(
            @Query("action") String action,
            @Query("id_pengajuan") String id_pengajuan,
            @Query("divisi") String divisi,
            @Query("leader") String leader
    );

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("ControllerPengajuan.php")
    Call<DataPengajuanModel> insertDataPengajuan(
            @Field("action") String action,
            @Field("id_pengajuan") String id_pengajuan,
            @Field("nip") String nip,
            @Field("nama") String nama,
            @Field("divisi") String divisi,
            @Field("hari") String hari,
            @Field("tanggal") String tanggal,
            @Field("jam_mulai") String jam_mulai,
            @Field("jam_selesai") String jam_selesai,
            @Field("leader") String leader,
            @Field("keterangan") String keterangan,
            @Field("tanggal_selesai") String tanggal_selesai
            );

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("ControllerPengajuan.php")
    Call<DataPengajuanModel> terimaDataPengajuan(
            @Field("action") String action,
            @Field("id_pengajuan") String id_pengajuan
    );

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("ControllerPengajuan.php")
    Call<DataPengajuanModel> tolakDataPegawai(
            @Field("action") String action,
            @Field("id_pengajuan") String id_pengajuan
    );

    @GET("ControllerPengajuan.php")
    Call<ListPengajuanResponse> getAllConfirmed(
            @Query("action") String action
    );

    @GET("ControllerPengajuan.php")
    Call<ListPengajuanResponse> getSingleConfirmed(
            @Query("action") String action,
            @Query("nip") String nip
    );

    @GET("ControllerPengajuan.php")
    Call<String> getDataPerbulan(
            @Query("action") String action,
            @Query("tanggal") String tanggal
    );

    //P U S H  N O T I F I C A T I O N
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("ControllerPushNotif.php")
    Call<PushNotifModel> registerPushNotif(
            @Field("action") String action,
            @Field("reg_id") String reg_id,
            @Field("reg_device") String reg_device,
            @Field("reg_device_id") String reg_device_id,
            @Field("nip") String nip
    );

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("ControllerPushNotif.php")
    Call<PushNotifModel> sendPush(
            @Field("action") String action,
            @Field("title") String title,
            @Field("message") String message,
            @Field("nip") String nip
    );
}
