package md.intelectsoft.stockmanager.SettingsMenu;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import md.intelectsoft.stockmanager.BuildConfig;
import md.intelectsoft.stockmanager.R;
import md.intelectsoft.stockmanager.Utils.UpdateHelper;
import md.intelectsoft.stockmanager.Utils.UpdateInformation;
import md.intelectsoft.stockmanager.BaseApp;
import md.intelectsoft.stockmanager.app.utils.SPFHelp;

public class Securitate extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,  UpdateHelper.OnUpdateCheckListener  {

    TextView txtCod;//limba;
    EditText et_limit;//et_pin,key_input;
    Button btn_check_update;//btn_verific,
    ProgressDialog pDialog;
    ImageButton btn_ro,btn_ru,btn_en;
    public static final int progress_bar_type = 11;
    private static String file_url_apk = "https://md.intelectsoft/androidapps/MobileTerminal.apk";
    private static String file_url_apk_old = "https://md.intelectsoft/androidapps/MobileTerminalOld.apk";
    private static String file_version_url = "https://md.intelectsoft/androidapps/MobileTerminalVersion.txt";
    private static String file_version_url_old = "https://md.intelectsoft/androidapps/MobileTerminalVersionOld.txt";
    private Locale myLocale;

    UpdateHelper.OnUpdateCheckListener checkListener;

    private ProgressDialog pgH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTitle(R.string.header_securitate_activity);
        setContentView(R.layout.activity_securitate);
        Toolbar toolbar = findViewById(R.id.toolbar_securitate);
        setSupportActionBar(toolbar);

        txtCod= findViewById(R.id.txt_cod_licenta);
        //key_input = findViewById(R.id.et_input_licenta);
       // btn_verific = findViewById(R.id.btn_verific_licenta);
        et_limit = findViewById(R.id.et_limit_count_sales_securitate);
        //et_pin = findViewById(R.id.et_input_pin_securitate);
        btn_check_update = findViewById(R.id.btn_chek_update2);

        btn_en = findViewById(R.id.select_lng_en);
        btn_ru = findViewById(R.id.select_lng_ru);
        btn_ro = findViewById(R.id.select_lng_ro);

        load_button();

        checkListener = this;

        DrawerLayout drawer = findViewById(R.id.drawer_layout_securitate);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        final SPFHelp sharedPrefsInstance = SPFHelp.getInstance();
        View headerLayout = navigationView.getHeaderView(0);
        TextView useremail = (TextView) headerLayout.findViewById(R.id.txt_name_of_user);
        useremail.setText(sharedPrefsInstance.getString("UserName",""));


        TextView user_workplace = (TextView) headerLayout.findViewById(R.id.txt_workplace_user);
        user_workplace.setText(sharedPrefsInstance.getString("WorkPlaceName",""));
        //et_pin.setText(sharedPrefsInstance.getString("PinCod",""));

        pgH = new ProgressDialog(this,R.style.ThemeOverlay_AppCompat_Dialog_Alert_TestDialogTheme);

        final TelephonyManager tm = (TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(this), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            }
        }

        tmDevice = "KitKatABCDEFGHIJKLMNOPQRSTUVWXYZMars";
        androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID( androidId.hashCode(), tmDevice.hashCode());
        String deviceId = deviceUuid.toString();
        deviceId=deviceId.replace("-","");
        deviceId=deviceId.replace("f","");
        deviceId=deviceId.replace("1","t");
        deviceId=deviceId.replace("3","s");
        deviceId=deviceId.replace("6","a");
//TODO:this not working !!!
        String code =  sharedPrefsInstance.getString("LicenseCode","");
//        if(code.equals("")){
//            for (int k = 0; k < deviceId.length(); k++) {
//                if (Character.isLetter(deviceId.charAt(k))) {
//                    code = code + deviceId.charAt(k);
//                }
//            }
//            code = code.substring(0, 8);
//            txtCod.setText(code.toUpperCase());
//
//
//            sharedPrefsInstance.putString("LicenseCode",code.toUpperCase());
//        }
//        else{
            txtCod.setText(code.toUpperCase());
//        }
       // key_input.setText(sharedPrefsInstance.getString("KeyText",""));
        et_limit.setText(sharedPrefsInstance.getString("LimitSales","0"));

       // final String internKey = md5(code.toUpperCase() + "ENCEFALOMIELOPOLIRADICULONEVRITA");
//        btn_verific.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String key = key_input.getText().toString().toUpperCase();
//                if (Test(code,key_input.getText().toString().toUpperCase())){
//                    txtCod.setText(code);
//                    key_input.setBackgroundResource(R.drawable.ping_true_conect);
//                    sharedPrefsInstance.putBoolean("Key",true);
//                    sharedPrefsInstance.putString("KeyText",key);
//                }
//                else{
//                    key_input.setBackgroundResource(R.drawable.ping_false_connect);
//                    sharedPrefsInstance.putBoolean("Key",false);
//                    sharedPrefsInstance.putString("KeyText",key_input.getText().toString().toUpperCase());
//                }
//            }
//        });
        et_limit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(et_limit.getText().toString().equals("")) {

                    sharedPrefsInstance.putString("LimitSales", "0");
                }else{
                    sharedPrefsInstance.putString("LimitSales", et_limit.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        et_pin.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                sharedPrefsInstance.putString("PinCod",et_pin.getText().toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        btn_check_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new DownloadVersionFileFromURL().execute(file_version_url);

                UpdateHelper.with(Securitate.this).onUpdateCheck(checkListener).check();
            }
        });

        btn_ru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_en.isSelected()){
                    btn_en.setSelected(false);
                }
                if(btn_ro.isSelected()){
                    btn_ro.setSelected(false);
                }
                sharedPrefsInstance.putString("Language","ru");
                btn_ru.setSelected(true);
                ((BaseApp) getApplication()).setRecreate(true);
                changeLang("ru");
            }
        });
        btn_ro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_en.isSelected()){
                    btn_en.setSelected(false);
                }
                if(btn_ru.isSelected()){
                    btn_ru.setSelected(false);
                }
                sharedPrefsInstance.putString("Language","ro");
                btn_ro.setSelected(true);
                ((BaseApp) getApplication()).setRecreate(true);
                changeLang("ro");
            }
        });
        btn_en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_ru.isSelected()){
                    btn_ru.setSelected(false);
                }
                if(btn_ro.isSelected()){
                    btn_ro.setSelected(false);
                }
                sharedPrefsInstance.putString("Language","en");
                btn_en.setSelected(true);
                ((BaseApp) getApplication()).setRecreate(true);
                changeLang("en");
            }
        });
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_securitate);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_securitate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_close_securitate) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.menu_conect) {
//            Intent MenuConnect = new Intent(".MenuConnect");
//            startActivity(MenuConnect);
//            finish();
//        } else
            if (id == R.id.menu_workplace) {
            Intent Logins = new Intent(".LoginMobile");
            Logins.putExtra("Activity", 8);
            startActivity(Logins);
            finish();
        } else if (id == R.id.menu_printers) {
            Intent Logins = new Intent(".LoginMobile");
            Logins.putExtra("Activity", 9);
            startActivity(Logins);
        } else if (id == R.id.menu_securitate) {
            Intent Logins = new Intent(".LoginMobile");
            Logins.putExtra("Activity", 10);
            startActivity(Logins);
            finish();
        } else if (id == R.id.menu_about) {
            Intent MenuConnect = new Intent(".MenuAbout");
            startActivity(MenuConnect);
            finish();
        } else if (id == R.id.menu_exit) {
            SharedPreferences WorkPlace = getSharedPreferences("Work Place", MODE_PRIVATE);
            WorkPlace.edit().clear().apply();
            finishAffinity();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout_securitate);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    public static String md5(final String s) {
//        final String MD5 = "MD5";
//        try {
//            // Create MD5 Hash
//            MessageDigest digest = java.security.MessageDigest
//                    .getInstance(MD5);
//            digest.update(s.getBytes());
//            byte messageDigest[] = digest.digest();
//            byte[] encode = Base64.encode(messageDigest,0);
//            String respencode = new String(encode).toUpperCase();
//            // Create String
//            String digits="";
//            for (int i = 0; i < respencode.length(); i++) {
//                char chrs = respencode.charAt(i);
//                if (!Character.isDigit(chrs))
//                    digits = digits+chrs;
//            }
//            String keyLic = "";
//            for (int k=0;k<digits.length();k++){
//                if (Character.isLetter(digits.charAt(k))){
//                    keyLic=keyLic + digits.charAt(k);
//                }
//            }
//            keyLic=keyLic.substring(0,8);
//
//            return keyLic;
//
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
//    public boolean Test (String key,String entern_key){
//        if (key.equals(entern_key)){
//            return true;
//        }
//        return false;
//    }
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    @Override
    public void onUpdateCheckListener(UpdateInformation information) {
        boolean update = information.isUpdate();

        if(update && !information.getNewVerion().equals(information.getCurrentVersion())){
            android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this,R.style.ThemeOverlay_AppCompat_Dialog_Alert_TestDialogTheme)
                    .setTitle("New version " + information.getNewVerion() + " available")
                    .setMessage("Please update to new version to continue use.Current version: " + information.getCurrentVersion())
                    .setPositiveButton("UPDATE",(dialogInterface, i) -> {
                        pgH.setMessage("download new version...");
                        pgH.setIndeterminate(true);
                        pgH.show();
                        downloadAndInstallApk(information.getUrl());
                    })
                    .setNegativeButton("No,thanks", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    })
                    .create();
            alertDialog.show();
        }
        else{
            Toast.makeText(this, "Is actually version!", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadAndInstallApk(String url){
        //get destination to update file and set Uri
        //TODO: First I wanted to store my update .apk file on internal storage for my app but apparently android does not allow you to open and install
        //aplication with existing package from there. So for me, alternative solution is Download directory in external storage. If there is better

        String destination = Environment.getExternalStorageDirectory()+ "/IntelectSoft";
        String fileName = "/mobileterminal.apk";
        destination += fileName;
        final Uri uri = Uri.parse("file://" + destination);

        //Delete update file if exists
        File file = new File(destination);
        if (file.exists())
            //file.delete() - test this, I think sometimes it doesnt work
            file.delete();

        //set download manager
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Download new version...");
        request.setTitle("MobileTerminal update");

        //set destination
        request.setDestinationUri(uri);

        // get download service and enqueue file
        final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        final long downloadId = manager.enqueue(request);

        //set BroadcastReceiver to install app when .apk is downloaded
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                pgH.dismiss();
                File file = new File(Environment.getExternalStorageDirectory()+ "/IntelectSoft","/mobileterminal.apk"); // mention apk file path here

                Uri uri = FileProvider.getUriForFile(Securitate.this, BuildConfig.APPLICATION_ID + ".provider",file);
                if(file.exists()){
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setDataAndType(uri, "application/vnd.android.package-archive");
                    install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(install);
                }
                unregisterReceiver(this);
                finish();

            }
        };
        //register receiver for when .apk download is compete
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();

                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                // Output stream
                OutputStream output = new FileOutputStream(Environment
                        .getExternalStorageDirectory().toString()
                        + "/IntelectSoft/MobileTerminal.apk");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);
            pDialog.dismiss();

            File file = new File(Environment.getExternalStorageDirectory()+ "/IntelectSoft","/MobileTerminal.apk"); // mention apk file path here
            Uri uri = FileProvider.getUriForFile(Securitate.this, BuildConfig.APPLICATION_ID + ".provider",file);
            if (file.exists()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } else {
                Toast.makeText(Securitate.this, "file not exist", Toast.LENGTH_SHORT).show();
            }


        }

    }
    class DownloadFileOLDFromURL extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                // Output stream
                OutputStream output = new FileOutputStream(Environment
                        .getExternalStorageDirectory().toString()
                        + "/IntelectSoft/MobileTerminalOld.apk");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);
            pDialog.dismiss();

            File file = new File(Environment.getExternalStorageDirectory()+ "/IntelectSoft","/MobileTerminalOld.apk"); // mention apk file path here
            Uri uri = FileProvider.getUriForFile(Securitate.this, BuildConfig.APPLICATION_ID + ".provider",file);
            if (file.exists()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } else {
                Toast.makeText(Securitate.this, "file not exist", Toast.LENGTH_SHORT).show();
            }


        }

    }
    class DownloadVersionFileFromURL extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.setConnectTimeout(2000);
                conection.connect();

                    int lenghtOfFile = conection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                OutputStream output = new FileOutputStream(Environment
                        .getExternalStorageDirectory().toString()
                        + "/IntelectSoft/MobileTerminalVersion.txt");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }finally{
                pDialog.dismiss();
            }
            return null;
        }
        protected void onProgressUpdate(String... progress) {
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(final String file_url) {
            dismissDialog(progress_bar_type);
            File file = new File(Environment.getExternalStorageDirectory()+ "/IntelectSoft","/MobileTerminalVersion.txt"); // mention apk file path here
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            }
            catch (IOException e) {
                Toast.makeText(Securitate.this, "Exception read file", Toast.LENGTH_SHORT).show();
            }
            pDialog.dismiss();
            String version ="0.0";
            try {
                PackageInfo pInfo = Securitate.this.getPackageManager().getPackageInfo(getPackageName(), 0);
                version = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                ((BaseApp)getApplication()).appendLog(e.getMessage(),Securitate.this);
            }
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Securitate.this);
            alertDialog.setTitle(getResources().getString(R.string.msg_dialog_title_atentie));
            alertDialog.setMessage(getResources().getString(R.string.versiune_server_securitate) + text.toString()+ "\n"+getResources().getString(R.string.versiune_locala_securitate) + version);
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton(getResources().getString(R.string.securitate_download_version), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new DownloadFileFromURL().execute(file_url_apk);
                }
            });
            alertDialog.setNegativeButton(getResources().getString(R.string.txt_renunt_all), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    pDialog.dismiss();
                }
            });
            alertDialog.show();
        }

    }
    class DownloadVersionOLDFileFromURL extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.setConnectTimeout(2000);
                conection.connect();

                int lenghtOfFile = conection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                OutputStream output = new FileOutputStream(Environment
                        .getExternalStorageDirectory().toString()
                        + "/IntelectSoft/MobileTerminalVersionOld.txt");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }finally{
                pDialog.dismiss();
            }
            return null;
        }
        protected void onProgressUpdate(String... progress) {
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(final String file_url) {
            dismissDialog(progress_bar_type);
            File file = new File(Environment.getExternalStorageDirectory()+ "/IntelectSoft","/MobileTerminalVersionOld.txt"); // mention apk file path here
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            }
            catch (IOException e) {
                Toast.makeText(Securitate.this, "Exception read file", Toast.LENGTH_SHORT).show();
            }
            pDialog.dismiss();
            String version ="0.0";
            try {
                PackageInfo pInfo = Securitate.this.getPackageManager().getPackageInfo(getPackageName(), 0);
                version = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                ((BaseApp)getApplication()).appendLog(e.getMessage(),Securitate.this);
            }
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Securitate.this);
            alertDialog.setTitle(getResources().getString(R.string.msg_dialog_title_atentie));
            alertDialog.setMessage(getResources().getString(R.string.versiune_server_securitate) + text.toString()+ "\n"+getResources().getString(R.string.versiune_locala_securitate) + version);
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton(getResources().getString(R.string.securitate_download_version), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new DownloadFileOLDFromURL().execute(file_url_apk_old);
                }
            });
            alertDialog.setNegativeButton(getResources().getString(R.string.txt_renunt_all), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    pDialog.dismiss();
                }
            });
            alertDialog.show();
        }

    }
    public void changeLang(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        Configuration config = new Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        recreate();
    }

    public void load_button(){
        SharedPreferences Setting = getSharedPreferences("Settings", MODE_PRIVATE);
        String lang = Setting.getString("Language", "defoult");
        switch (lang) {
            case "ro":
                btn_ro.setSelected(true);
                break;
            case "ru":
                btn_ru.setSelected(true);
                break;
            case "en":
                btn_en.setSelected(true);
                break;
        }
    }
}