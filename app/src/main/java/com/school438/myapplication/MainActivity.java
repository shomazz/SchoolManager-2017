package com.school438.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.school438.myapplication.Fragments.NewsFragment;
import com.school438.myapplication.Fragments.RingsFragment;
import com.school438.myapplication.Fragments.SettingsFragment;
import com.school438.myapplication.Fragments.SheduleFragment;
import com.school438.myapplication.SchoolManager.DBManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_RINGS = "Звонки";
    private static final String TAG_NEWS = "Новости";
    private static final String TAG_SETTINGS = "Настройки";
    private static final String TAG_SHEDULE = "Расписание";
    private static final String TAG_CHOOSE_CLASS = "Выбрать класс";

    private RingsFragment ringsFragment;
    private NewsFragment newsFragment;
    private SettingsFragment settingsFragment;
    private SheduleFragment sheduleFragment;

    public static final String APP_PREFERENCES_CURRENT_CLASS = "currentclass";
    public static final String APP_PREFERENCES_CURRENT_TAG = "currenttag";
    public static final String APP_PREFERENCES = "mysettings";
    public static String dbPath;
    public static String CURRENT_TABLE_NAME = DBManager.ELEVENTH_A;
    public static String CURRENT_TAG = TAG_NEWS;
    private Toolbar toolbar;
    private DBManager dbManager;
    private SharedPreferences mSettings;
    private DrawerLayout drawer;
    private View navHeader;
    private FloatingActionButton fab;
    private NavigationView navigationView;
    private TextView txtCLass;
    private Handler mHandler;
    private AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        dbPath = "/data/data/" + getPackageName() + "/databases/";
        dbManager = DBManager.getInstance(this);
        if (!new File("/data/data/" + getPackageName() + "/databases/" + DBManager.LOCAL_DB_NAME).exists())
            dbManager.copyDBFromAssets(this);
        mHandler = new Handler();
        setUpFloatButton();
        recieveSettings();
        setUpNavigationView();
        createFragments();
        loadHomeFragment();
        refreshFloatButton();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final String[] classArr = {"5А", "5Б", "6А", "6Б", "7А", "7Б", "8А", "8Б"
                , "9А", "9Б", "10А", "11А"};

        builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите класс");
        builder.setItems(classArr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        CURRENT_TABLE_NAME = DBManager.FITH_A;
                        break;
                    case 1:
                        CURRENT_TABLE_NAME = DBManager.FITH_B;
                        break;
                    case 2:
                        CURRENT_TABLE_NAME = DBManager.SIXTH_A;
                        break;
                    case 3:
                        CURRENT_TABLE_NAME = DBManager.SIXTH_B;
                        break;
                    case 4:
                        CURRENT_TABLE_NAME = DBManager.SEVENTH_A;
                        break;
                    case 5:
                        CURRENT_TABLE_NAME = DBManager.SEVENTH_B;
                        break;
                    case 6:
                        CURRENT_TABLE_NAME = DBManager.EIGHTTH_A;
                        break;
                    case 7:
                        CURRENT_TABLE_NAME = DBManager.EIGHTTH_B;
                        break;
                    case 8:
                        CURRENT_TABLE_NAME = DBManager.NINE_A;
                        break;
                    case 9:
                        CURRENT_TABLE_NAME = DBManager.NINE_B;
                        break;
                    case 10:
                        CURRENT_TABLE_NAME = DBManager.TENTH_A;
                        break;
                    case 11:
                        CURRENT_TABLE_NAME = DBManager.ELEVENTH_A;
                        break;
                }
                setCurrentClass(CURRENT_TABLE_NAME);
                sheduleFragment.refreshListView();
                txtCLass.setText(getCurrentClassString());
            }
        });
        builder.setCancelable(true);
        return builder.create();
    }

    public void setUpFloatButton() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        refreshFloatButton();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sheduleFragment.saveShedule();
                DownloadDBTask d = new DownloadDBTask();
                d.execute();
            }
        });
    }

    private void createFragments() {
        ringsFragment = new RingsFragment();
        settingsFragment = new SettingsFragment();
        sheduleFragment = new SheduleFragment();
        newsFragment = new NewsFragment();
    }

    private void recieveSettings() {
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(APP_PREFERENCES_CURRENT_CLASS))
            this.CURRENT_TABLE_NAME = getCurrentClassTableName();
        if (mSettings.contains(APP_PREFERENCES_CURRENT_TAG)) {
            this.CURRENT_TAG = getCurrentTag();
            System.out.println("From Settings :" + CURRENT_TAG);
        }
    }

    private Fragment getHomeFragment() {
        switch (CURRENT_TAG) {
            case TAG_RINGS:
                return ringsFragment;
            case TAG_SETTINGS:
                return settingsFragment;
            case TAG_SHEDULE:
                return sheduleFragment;
            default:
                return newsFragment;
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(CURRENT_TAG);
    }

    private void setUpNavigationView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.activity_main);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);
        txtCLass = (TextView) navHeader.findViewById(R.id.txt_class);
        txtCLass.setText(getCurrentClassString());
        System.out.println("setUpNavigationView");
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem.getItemId() != R.id.nav_choose_class) {
                    switch (menuItem.getItemId()) {
                        case R.id.nav_rings:
                            CURRENT_TAG = TAG_RINGS;
                            break;
                        case R.id.nav_news:
                            CURRENT_TAG = TAG_NEWS;
                            break;
                        case R.id.nav_settings:
                            CURRENT_TAG = TAG_SETTINGS;
                            break;
                        case R.id.nav_shedule:
                            CURRENT_TAG = TAG_SHEDULE;
                            break;
                        default:
                    }
                    System.out.println("onNavigationItemSelected " + CURRENT_TAG);
                    loadHomeFragment();
                    refreshFloatButton();
                } else {
                    showDialog(0);
                }
                return true;
            }
        });
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    private void loadHomeFragment() {
        setToolbarTitle();
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            refreshFloatButton();
            return;
        }
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
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
        refreshFloatButton();
        drawer.closeDrawers();
    }

    public void refreshFloatButton() {
        switch (CURRENT_TAG) {
            case TAG_CHOOSE_CLASS:
            case TAG_RINGS:
            case TAG_SETTINGS:
            case TAG_NEWS:
                fab.hide();
                break;
            case TAG_SHEDULE:
                fab.show();
                break;
        }
    }

    public String getCurrentClassString() {
        switch (CURRENT_TABLE_NAME) {
            case DBManager.FITH_A:
                return "5А";
            case DBManager.FITH_B:
                return "5Б";
            case DBManager.SIXTH_A:
                return "6А";
            case DBManager.SIXTH_B:
                return "6Б";
            case DBManager.SEVENTH_A:
                return "7А";
            case DBManager.SEVENTH_B:
                return "7Б";
            case DBManager.EIGHTTH_A:
                return "8А";
            case DBManager.EIGHTTH_B:
                return "8Б";
            case DBManager.NINE_A:
                return "9А";
            case DBManager.NINE_B:
                return "9Б";
            case DBManager.TENTH_A:
                return "10А";
            case DBManager.ELEVENTH_A:
                return "11А";
            default:
                return "11А";
        }
    }

    public void setCurrentClass(String activeTableName) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_CURRENT_CLASS, activeTableName);
        editor.apply();
        this.CURRENT_TABLE_NAME = activeTableName;
    }

    public String getCurrentClassTableName() {
        if (mSettings.contains(APP_PREFERENCES_CURRENT_CLASS)) {
            return mSettings.getString(APP_PREFERENCES_CURRENT_CLASS, "");
        } else
            return null;
    }

    public void setCurrentTag(String currentTag) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_CURRENT_TAG, currentTag);
        editor.apply();
        this.CURRENT_TAG = currentTag;
    }

    public String getCurrentTag() {
        if (mSettings.contains(APP_PREFERENCES_CURRENT_TAG)) {
            return mSettings.getString(APP_PREFERENCES_CURRENT_TAG, "");
        } else
            return null;
    }

    public void onLinkButton(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://telegram.me/school438"));
        startActivity(browserIntent);
    }

    public class DownloadDBTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Гружу расписание классов...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                File file = new File(dbPath, DBManager.LOCAL_DB_NAME);
                URL url = new URL(DBManager.DB_ADRESS + "/" + DBManager.LOCAL_DB_NAME);
                System.out.println("Start Downloading database...");
                URLConnection conection = url.openConnection();
                conection.connect();
                int lenghtOfFile = 500000;
                InputStream input = new BufferedInputStream(url.openStream(), lenghtOfFile);
                OutputStream output = new FileOutputStream(file);
                System.out.println("File's absolute path : \"" + file.getAbsolutePath() + "\";");
                byte data[] = new byte[lenghtOfFile];
                long total = 0;
                int count = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
            System.out.println("DB Downloaded path : \"" + dbPath + DBManager.LOCAL_DB_NAME + "\";");
        }
    }
}
