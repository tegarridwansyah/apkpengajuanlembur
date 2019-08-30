package com.example.pl.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.pl.R;
import com.example.pl.model.LoginPegawaiModel;
import com.example.pl.retrofit.RetrofitClient;
import com.example.pl.retrofit.RetrofitInterface;
import com.example.pl.utils.DetectConnection;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ActivityLogin extends AppCompatActivity {
    TextInputEditText inputUsername, inputPassword;
    Button btnLogin;
    ImageView logo;
    DetectConnection detectConnection;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Harap Tunggu...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            if(Build.VERSION.SDK_INT >= 23){
                if(checkPermission()){

                } else {
                    requestPermission();
                }
            }
        }
         pref = getApplicationContext().getSharedPreferences("data", 0); // 0 - for private mode
         editor = pref.edit();

        String cek_nip = pref.getString("nip", null);
        if(cek_nip != null && cek_nip.equalsIgnoreCase(pref.getString("nip", null))){
            startActivity(new Intent(ActivityLogin.this, ActivityHome.class));
            ActivityLogin.this.finish();
        }
        inputUsername = (TextInputEditText)findViewById(R.id.material_edittext_username);
        inputPassword = (TextInputEditText)findViewById(R.id.material_edittext_password);
        btnLogin = (Button) findViewById(R.id.material_button_login_beres);
        logo = (ImageView)findViewById(R.id.logo_dilogin);
        detectConnection = new DetectConnection(ActivityLogin.this);

        Resources resources = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.logotiketux1);
        Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, 790, 250, false);
        logo.setImageBitmap(bitmap1);
        logo.bringToFront();
        Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_up1);
        logo.setAnimation(slide_up);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                if(detectConnection.isConnectingToInternet()){
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(RetrofitClient.BASE_URL).addConverterFactory(ScalarsConverterFactory.create()).build();
                    RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
                    Call<String> call = retrofitInterface.loginPegawai("login", inputUsername.getText().toString(), inputPassword.getText().toString());
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if(response.isSuccessful()){
                                if(response.body() != null){
                                    String jsonResponse = response.body().toString();
                                    getForShared(jsonResponse);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void getForShared(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            if(jsonObject.optString("status").equals("ok")){
                ArrayList<LoginPegawaiModel> loginPegawaiModelArrayList = new ArrayList<>();
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                for(int i = 0; i < jsonArray.length(); i++){
                    LoginPegawaiModel loginPegawaiModel = new LoginPegawaiModel();
                    JSONObject dataObj = jsonArray.getJSONObject(i);

                    loginPegawaiModel.setNip(dataObj.getString("nip"));
                    loginPegawaiModel.setUsername(dataObj.getString("username"));
                    loginPegawaiModel.setLevel_pegawai(dataObj.getString("level_pegawai"));
                    loginPegawaiModel.setNama_divisi(dataObj.getString("nama_divisi"));

                    loginPegawaiModelArrayList.add(loginPegawaiModel);
                    String nip = loginPegawaiModelArrayList.get(i).getNip(),
                            nama = loginPegawaiModelArrayList.get(i).getUsername(),
                            level = loginPegawaiModelArrayList.get(i).getLevel_pegawai(),
                            nama_divisi = loginPegawaiModelArrayList.get(i).getNama_divisi();

                    editor.putString("nip", nip);
                    editor.putString("nama", nama);
                    editor.putString("level", level);
                    editor.putString("nama_divisi", nama_divisi);
                    editor.apply();
                    System.out.println("YANG MASUK : " + pref.getString("nama", null));
                    progressDialog.dismiss();
                    startActivity(new Intent(ActivityLogin.this, ActivityHome.class));
                    ActivityLogin.this.finish();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(ActivityLogin.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ActivityLogin.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(ActivityLogin.this, "Write External Storage permission allows us to create files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(ActivityLogin.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }
}
