package com.example.pl.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.pl.R;
import com.example.pl.app.Config;
import com.example.pl.fragment.FragmentDataDivisi;
import com.example.pl.fragment.FragmentDataPM;
import com.example.pl.fragment.FragmentDataPegawai;
import com.example.pl.fragment.FragmentFormPengajuanLembur;
import com.example.pl.fragment.FragmentLaporan;
import com.example.pl.fragment.FragmentListPengajuan;
import com.example.pl.fragment.FragmetWelcome;
import com.example.pl.model.PushNotifModel;
import com.example.pl.retrofit.RetrofitClient;
import com.example.pl.retrofit.RetrofitInterface;
import com.example.pl.utils.DetectConnection;
import com.example.pl.utils.NotificationUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityHome extends AppCompatActivity {
    public NavigationView navigationView;
    private DrawerLayout drawer;
    public View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    public static final String TAG_HOME = "Home";
    public static final String TAG_DATA_PEGAWAI = "Data Pegawai";
    public static final String TAG_DATA_DIVISI = "Data Divisi";
    public static final String TAG_PM = "Data PM";
    public static final String TAG_FORM_PENGAJUAN_LEMBUR = "Form Pengajuan";
    public static final String TAG_LIST_PENGAJUAN_LEMBUR = "List Pengajuan";
    public static final String TAG_LAPORAN = "Laporan";
    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    DetectConnection detectConnection;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final String TAG = ActivityHome.class.getSimpleName();
    ProgressDialog progressDialog;
    String nip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pref = getApplicationContext().getSharedPreferences("data", 0); // 0 - for private mode
        editor = pref.edit();
        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        detectConnection = new DetectConnection(ActivityHome.this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Harap Tunggu...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        imgProfile = (ImageView)navHeader.findViewById(R.id.img_profile);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        nip = pref.getString("nip", null);
        txtName.setText(pref.getString("nama", null) + " - " + pref.getString("nip", null).substring(3, 6));
        txtWebsite.setText(pref.getString("nama_divisi", null));
        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //cek untuk tipe intent filter
                if(intent.getAction().equals(Config.REGISTRATION_COMPLETE)){
                    //gcm successfully registered
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();
                } else if(intent.getAction().equals(Config.PUSH_NOTIFICATION)){
                    //new push notif is received
                    String message = intent.getStringExtra("message");
                    new MaterialAlertDialogBuilder(ActivityHome.this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                            .setMessage(message)
                            .setPositiveButton("YA", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            }
        };

        displayFirebaseRegId();

    }

    private void displayFirebaseRegId() {
        SharedPreferences pref1 = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref1.getString("regId", null);

        Log.e(TAG, "Firebase reg id : " + regId);
        Log.e(TAG, "Device : " + Build.MODEL);
        Log.e(TAG, "Device id : " + Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));

        if (detectConnection.isConnectingToInternet()){
            progressDialog.show();
            RetrofitInterface retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
            Call<PushNotifModel> call = retrofitInterface.registerPushNotif("register",regId, Build.MODEL, Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID), pref.getString("nip", null));
            call.enqueue(new Callback<PushNotifModel>() {
                @Override
                public void onResponse(Call<PushNotifModel> call, Response<PushNotifModel> response) {
                    Toast.makeText(getApplicationContext(), "Berhasil Register", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Berhasil");
                }

                @Override
                public void onFailure(Call<PushNotifModel> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Gagal Register", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Gagal");
                }
            });
        } else
            Toast.makeText(getApplicationContext(), "No Intenet", Toast.LENGTH_LONG).show();
        progressDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void loadNavHeader() {
        imgProfile.setImageResource(R.drawable.ic_person_white);
    }

    public void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }


        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                FragmetWelcome fragmetWelcome = new FragmetWelcome();
                return fragmetWelcome;
            case 1:
                // photos
                FragmentDataPegawai homeFragment = new FragmentDataPegawai();
                return homeFragment;
            case 2:
                // movies fragment
                FragmentDataDivisi photosFragment = new FragmentDataDivisi();
                return photosFragment;
            case 3:
                // notifications fragment
                FragmentDataPM moviesFragment = new FragmentDataPM();
                return moviesFragment;
            case 4:
                // settings fragment
                FragmentFormPengajuanLembur notificationsFragment = new FragmentFormPengajuanLembur();
                return notificationsFragment;
            case 5:
                FragmentListPengajuan settingsFragment = new FragmentListPengajuan();
                return settingsFragment;
            case 6:
                FragmentLaporan fragmentLaporan = new FragmentLaporan();
                return fragmentLaporan;
            default:
                return new FragmetWelcome();
        }
    }

    private void setToolbarTitle() {
            getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    public void selectNavMenu() {
            navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_data_pegawai:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_DATA_PEGAWAI;
                        break;
                    case R.id.nav_data_divisi:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_DATA_DIVISI;
                        break;
                    case R.id.nav_data_pm:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_PM;
                        break;
                    case R.id.nav_form_pengajuan:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_FORM_PENGAJUAN_LEMBUR;
                        break;
                    case R.id.nav_list_pengajuan:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_LIST_PENGAJUAN_LEMBUR;
                        break;
                    case R.id.nav_laporan:
                        navItemIndex = 6;
                        CURRENT_TAG = TAG_LAPORAN;
                        break;
                    case R.id.nav_logout:
                        new MaterialAlertDialogBuilder(ActivityHome.this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                        .setMessage("Apakah kamu yakin ingin keluar?")
                        .setPositiveButton("YA", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editor.clear();
                                editor.commit();
                                startActivity(new Intent(ActivityHome.this, ActivityLogin.class));
                                ActivityHome.this.finish();
                            }
                        })
                        .setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                        return true;
                    default:
                        navItemIndex = 0;
                        return false;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
                if (navItemIndex != 0) {
                    navItemIndex = 0;
                    CURRENT_TAG = TAG_HOME;
                    loadHomeFragment();
                    return;
                }
        }

        super.onBackPressed();
    }
}
