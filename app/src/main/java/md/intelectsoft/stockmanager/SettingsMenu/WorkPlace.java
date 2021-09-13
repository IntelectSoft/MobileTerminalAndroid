package md.intelectsoft.stockmanager.SettingsMenu;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

//import md.intelectsoft.stockmanager.NetworkUtils.Services.CommandService;
import md.intelectsoft.stockmanager.R;
import md.intelectsoft.stockmanager.NetworkUtils.RetrofitResults.AssortmentListResult;
import md.intelectsoft.stockmanager.NetworkUtils.RetrofitResults.Assortment;
import md.intelectsoft.stockmanager.BaseApp;
import md.intelectsoft.stockmanager.TerminalService.TerminalAPI;
import md.intelectsoft.stockmanager.TerminalService.TerminalRetrofitClient;
import md.intelectsoft.stockmanager.app.utils.SPFHelp;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static md.intelectsoft.stockmanager.NetworkUtils.NetworkUtils.GetWareHouseList;
import static md.intelectsoft.stockmanager.NetworkUtils.NetworkUtils.Response_from_GetWareHouse;

public class WorkPlace extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    Button btn_get_workplace,btn_add_folder,btn_delete_all_folder;
    Switch check_stock,show_cod,auto_confirm,show_keyboard, printSales, checkStock_added_assortment,checkInvoiceOnlyPrice;
    TextView txtFolders,txt_user;
    String url_,UserId;
    ProgressDialog pgH;

    ArrayList<HashMap<String, Object>> stock_List_array = new ArrayList<>();
    ArrayList<HashMap<String, Object>> asl_list = new ArrayList<>();
    AlertDialog.Builder builderType;

    String[] kit_lists;
    boolean[] checkedItems;
    Handler handler;

    JSONArray myJSONArray=new JSONArray();
    JSONArray myJSONArrayBool=new JSONArray();
    JSONArray myJSONArrayUid=new JSONArray();
    String mWarehouseGUID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTitle(R.string.header_workplace_activity);
        setContentView(R.layout.activity_work_place);
        Toolbar toolbar = findViewById(R.id.toolbar_workplace);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout_workplace);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        btn_add_folder=findViewById(R.id.btn_add_folder_workplace);
        btn_delete_all_folder=findViewById(R.id.btn_delete_folder_workplace);
        btn_get_workplace = findViewById(R.id.btn_change_workplace);
        check_stock = findViewById(R.id.switch_check_stock_worplace);
        checkInvoiceOnlyPrice = findViewById(R.id.switch_input_invoice_only_price);
        show_cod = findViewById(R.id.switch_show_cod_workplace);
        auto_confirm = findViewById(R.id.switch_autoconfirm_workplace);
        show_keyboard = findViewById(R.id.switch_show_keyboard_workplace);
        txtFolders = findViewById(R.id.txt_show_folders_workplace);
        txt_user = findViewById(R.id.txt_user_workplace);
        printSales = findViewById(R.id.switch_print_sales);
        checkStock_added_assortment = findViewById(R.id.switch_check_stock_add_asl);

        pgH=new ProgressDialog(WorkPlace.this);

        final SPFHelp sharedPrefsInstance =SPFHelp.getInstance();
        final String userName = sharedPrefsInstance.getString("UserName","");
        String selected = sharedPrefsInstance.getString("selected_Name_Array","[]");
        String workplaceName = sharedPrefsInstance.getString("WorkPlaceName","Nedeterminat");
        mWarehouseGUID = sharedPrefsInstance.getString("WorkPlaceId",null);

        if(workplaceName.equals(""))
            workplaceName ="Nedeterminat";
        btn_get_workplace.setText(workplaceName);
        url_ = SPFHelp.getInstance().getString("URI","");

        txt_user.setText(userName);
        View headerLayout = navigationView.getHeaderView(0);
        TextView useremail = (TextView) headerLayout.findViewById(R.id.txt_name_of_user);
        useremail.setText(userName);

        TextView user_workplace = (TextView) headerLayout.findViewById(R.id.txt_workplace_user);
        user_workplace.setText(sharedPrefsInstance.getString("WorkPlaceName","Nedeterminat"));

        boolean autoConfirm = sharedPrefsInstance.getBoolean("AutoConfirmTransfer",false);
        boolean show = sharedPrefsInstance.getBoolean("ShowKeyBoard",false);
        boolean CheckStock = sharedPrefsInstance.getBoolean("CheckStockInput",false);
        boolean ShowCode = sharedPrefsInstance.getBoolean("ShowCode",false);
        boolean printSale = sharedPrefsInstance.getBoolean("PrintSales",false);
        boolean verifyStockAddAssortment = sharedPrefsInstance.getBoolean("CheckStockToServer",false);
        boolean invoiceOnlyPrice = sharedPrefsInstance.getBoolean("InvoiceOnlySum",false);

        show_keyboard.setChecked(show);
        auto_confirm.setChecked(autoConfirm);
        check_stock.setChecked(CheckStock);
        show_cod.setChecked(ShowCode);
        printSales.setChecked(printSale);
        checkStock_added_assortment.setChecked(verifyStockAddAssortment);
        checkInvoiceOnlyPrice.setChecked(invoiceOnlyPrice);

        try {
            myJSONArray = new JSONArray(selected);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        txtFolders.setText("");
        if(myJSONArray.length()>0) {
            for (int i = 0; i < myJSONArray.length(); i++) {
                if (i != 0) {
                    txtFolders.append(",");
                }
                try {
                    txtFolders.append(myJSONArray.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        btn_delete_all_folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtFolders.setText("");
                sharedPrefsInstance.putString("b" +
                        "" +
                        "" +
                        "oolean_Array", "[]");//boolJSON
                sharedPrefsInstance.putString("selected_Uid_Array","[]");//selectedUidJSON
                sharedPrefsInstance.putString("selected_Name_Array", "[]");
            }
        });
        btn_add_folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean dwnlASL =((BaseApp)getApplication()).getDownloadASLVariable();
                if(dwnlASL){
                    int mas=0;
                    BaseApp myapp =(BaseApp)getApplication();
                    asl_list =myapp.get_AssortimentFolders();
                    mas=asl_list.size();
                    kit_lists=new String[mas];
                    checkedItems = new boolean[mas];
                    for (int i=0;i<mas;i++){
                        checkedItems[i]=false;
                        kit_lists[i]=(String)asl_list.get(i).get("Name");
                    }
                    if(asl_list.size()>0){
                        AlertDialog.Builder builder = new AlertDialog.Builder(WorkPlace.this);
                        builder.setTitle("Choose items");
                        String selected = sharedPrefsInstance.getString("selected_Name_Array","[]");
                        String bolrns = sharedPrefsInstance.getString("boolean_Array","[]");
                        String selectedUidJSON = sharedPrefsInstance.getString("selected_Uid_Array","[]");

                        try {
                            myJSONArrayBool = new JSONArray(bolrns);
                            myJSONArray = new JSONArray(selected);
                            myJSONArrayUid = new JSONArray(selectedUidJSON);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(!bolrns.equals("[]")) {
                            checkedItems = new boolean[myJSONArrayBool.length()];
                            for (int o = 0; o < myJSONArrayBool.length(); o++) {
                                try {
                                    checkedItems[o] = myJSONArrayBool.getBoolean(o);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        builder.setMultiChoiceItems(kit_lists,checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                String name_asl = (String)asl_list.get(which).get("Name");
                                String uid_asl =  (String)asl_list.get(which).get("ID");
                                if(isChecked){
                                    myJSONArray.put(name_asl);
                                    myJSONArrayUid.put(uid_asl);
                                }else{
                                    for (int i=0;i<myJSONArray.length();i++){
                                        try {
                                            String namesArray = myJSONArray.getString(i);
                                            if(namesArray.equals(name_asl)){
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                    myJSONArray.remove(i);
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    for (int i=0;i<myJSONArrayUid.length();i++){
                                        try {
                                            String uid_aslArray = myJSONArrayUid.getString(i);
                                            if(uid_aslArray.equals(uid_asl)){
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                    myJSONArrayUid.remove(i);
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        });
                        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                txtFolders.setText("");
                                for(int i=0; i<myJSONArray.length();i++){
                                    if (i!=0){
                                        txtFolders.append(",");
                                    }
                                    try {
                                        txtFolders.append(myJSONArray.getString(i));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                JSONArray booleab = new JSONArray();
                                for (boolean checkedItem : checkedItems) {
                                    booleab.put(checkedItem);
                                }
                                sharedPrefsInstance.putString("boolean_Array", booleab.toString());
                                sharedPrefsInstance.putString("selected_Uid_Array", myJSONArrayUid.toString());
                                sharedPrefsInstance.putString("selected_Name_Array", myJSONArray.toString());
                                dialog.dismiss();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }else{
                    if (UserId != null && mWarehouseGUID!=null){
                        DownloadASL();
                    }
                    else if( UserId == null && mWarehouseGUID == null){
                        AlertDialog.Builder selectWorkPlace = new AlertDialog.Builder(WorkPlace.this);
                        selectWorkPlace.setTitle(getResources().getString(R.string.msg_dialog_title_atentie));
                        selectWorkPlace.setMessage("Nu este ales locul de munca!\nAlegeti locul de munca!");
                        selectWorkPlace.setPositiveButton(getResources().getString(R.string.txt_accept_all), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        selectWorkPlace.show();
                    }
                    else if(UserId == null){
                            Intent Logins = new Intent(".LoginMobile");
                            Logins.putExtra("Activity", 11);
                            startActivityForResult(Logins,11);
                    }
                    else if(mWarehouseGUID == null){
                        AlertDialog.Builder selectWorkPlace = new AlertDialog.Builder(WorkPlace.this);
                        selectWorkPlace.setTitle(getResources().getString(R.string.msg_dialog_title_atentie));
                        selectWorkPlace.setMessage("Nu este ales locul de munca!\nAlegeti locul de munca!");
                        selectWorkPlace.setPositiveButton(getResources().getString(R.string.txt_accept_all), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        selectWorkPlace.show();
                    }
                }
            }
        });
        btn_get_workplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pgH.setMessage("loading..");
                pgH.setIndeterminate(true);
                pgH.setCancelable(false);
                pgH.show();
                UserId = SPFHelp.getInstance().getString("UserId","");
                url_ = SPFHelp.getInstance().getString("URI","");
                if(UserId.equals("")){
                    Intent Logins = new Intent(".LoginMobile");
                    Logins.putExtra("Activity", 6);
                    startActivityForResult(Logins,6);
                }else{
                    getWareHouse();
                }
            }
        });

        show_keyboard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPrefsInstance.putBoolean("ShowKeyBoard",isChecked);
            }
        });
        auto_confirm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPrefsInstance.putBoolean("AutoConfirmTransfer",isChecked);
            }
        });
        check_stock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPrefsInstance.putBoolean("CheckStockInput",isChecked);
            }
        });
        show_cod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPrefsInstance.putBoolean("ShowCode",isChecked);
            }
        });
        printSales.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPrefsInstance.putBoolean("PrintSales",isChecked);
        });
        checkStock_added_assortment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPrefsInstance.putBoolean("CheckStockToServer",isChecked);
            }
        });
        checkInvoiceOnlyPrice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPrefsInstance.putBoolean("InvoiceOnlySum",isChecked);
            }
        });
        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            public void handleMessage(android.os.Message msg) {
                if(msg.what == 10) {
                    if (msg.arg1 == 12) {
                        pgH.dismiss();
                        int mas=0;
                        BaseApp myapp =(BaseApp)getApplication();
                        asl_list =myapp.get_AssortimentFolders();
                        mas=asl_list.size();
                        kit_lists=new String[mas];
                        checkedItems = new boolean[mas];
                        for (int i=0;i<mas;i++){
                            checkedItems[i]=false;
                            kit_lists[i]=(String)asl_list.get(i).get("Name");
                        }
                        if(asl_list.size()>0){
//                            SharedPreferences CheckUidFolder = getSharedPreferences("SaveFolderFilter", MODE_PRIVATE);
                            AlertDialog.Builder builder = new AlertDialog.Builder(WorkPlace.this);
                            builder.setTitle("Choose items");
                            String selected = sharedPrefsInstance.getString("selected_Name_Array","[]");
                            String bolrns = sharedPrefsInstance.getString("boolean_Array","[]");
                            String selectedUidJSON = sharedPrefsInstance.getString("selected_Uid_Array","[]");

                            try {
                                myJSONArrayBool = new JSONArray(bolrns);
                                myJSONArray = new JSONArray(selected);
                                myJSONArrayUid = new JSONArray(selectedUidJSON);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if(!bolrns.equals("[]")) {
                                checkedItems = new boolean[myJSONArrayBool.length()];
                                for (int o = 0; o < myJSONArrayBool.length(); o++) {
                                    try {
                                        checkedItems[o] = myJSONArrayBool.getBoolean(o);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            builder.setMultiChoiceItems(kit_lists,checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    String name_asl = (String)asl_list.get(which).get("Name");
                                    String uid_asl =  (String)asl_list.get(which).get("ID");
                                    if(isChecked){
                                        myJSONArray.put(name_asl);
                                        myJSONArrayUid.put(uid_asl);
                                    }else{
                                        for (int i=0;i<myJSONArray.length();i++){
                                            try {
                                                String namesArray = myJSONArray.getString(i);
                                                if(namesArray.equals(name_asl)){
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                        myJSONArray.remove(i);
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        for (int i=0;i<myJSONArrayUid.length();i++){
                                            try {
                                                String uid_aslArray = myJSONArrayUid.getString(i);
                                                if(uid_aslArray.equals(uid_asl)){
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                        myJSONArrayUid.remove(i);
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            });
                            builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    txtFolders.setText("");
                                    for(int i=0; i<myJSONArray.length();i++){
                                        if (i!=0){
                                            txtFolders.append(",");
                                        }
                                        try {
                                            txtFolders.append(myJSONArray.getString(i));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    ((BaseApp)getApplication()).setDownloadASLVariable(true);
                                    JSONArray bool = new JSONArray();
                                    for (boolean checkedItem : checkedItems) {
                                        bool.put(checkedItem);
                                    }
                                    sharedPrefsInstance.putString("boolean_Array", bool.toString());
                                    sharedPrefsInstance.putString("selected_Uid_Array", myJSONArrayUid.toString());
                                    sharedPrefsInstance.putString("selected_Name_Array", myJSONArray.toString());
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                }else{
                    String t = msg.obj.toString();
                    pgH.dismiss();
                    ((BaseApp)getApplication()).setDownloadASLVariable(false);
                    android.app.AlertDialog.Builder failureAsl = new android.app.AlertDialog.Builder(WorkPlace.this);
                    failureAsl.setCancelable(false);
                    failureAsl.setTitle(getResources().getString(R.string.msg_dialog_title_atentie));
                    failureAsl.setMessage(getResources().getString(R.string.msg_eroare_download_asl) + t + "\n" + getResources().getString(R.string.msg_reload_download_asl));
                    failureAsl.setPositiveButton(getResources().getString(R.string.toggle_btn_check_remain_da), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DownloadASL();
                        }
                    });
                    failureAsl.setNegativeButton(getResources().getString(R.string.toggle_btn_check_remain_nu), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    failureAsl.show();
                }
            }

        };

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==6){
            if(resultCode==RESULT_OK){
//                SharedPreferences LogIn = getSharedPreferences("User", MODE_PRIVATE);MODE_PRIVATE
                UserId = SPFHelp.getInstance().getString("UserId","");
                txt_user.setText(SPFHelp.getInstance().getString("UserName",""));
                getWareHouse();
            }else{
                pgH.dismiss();
            }
        }
        if( requestCode == 11){
            if (resultCode == RESULT_OK){

                UserId = SPFHelp.getInstance().getString("UserId","");
                DownloadASL();
            }
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_workplace);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_workplace, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_close_workplace) {
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
            finish();
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
//            SharedPreferences WorkPlace = getSharedPreferences("Work Place", MODE_PRIVATE);
//            WorkPlace.edit().clear().apply();
            finishAffinity();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout_workplace);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void show_WareHouse(){
        SimpleAdapter simpleAdapterType = new SimpleAdapter(WorkPlace.this, stock_List_array,android.R.layout.simple_list_item_1, new String[]{"Name"}, new int[]{android.R.id.text1});
        builderType = new AlertDialog.Builder(WorkPlace.this);
        builderType.setTitle(getResources().getString(R.string.txt_header_msg_list_depozitelor));
        builderType.setNegativeButton(getResources().getString(R.string.txt_renunt_all), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                stock_List_array.clear();
            }
        });
        builderType.setAdapter(simpleAdapterType, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int wich) {
                String WarehouseGUID= String.valueOf(stock_List_array.get(wich).get("Uid"));
                String WarehouseName= String.valueOf(stock_List_array.get(wich).get("Name"));
                String WarehouseCode= String.valueOf(stock_List_array.get(wich).get("Code"));

                SPFHelp sharedPrefsInstance = SPFHelp.getInstance();
                sharedPrefsInstance.putString("WorkPlaceName",WarehouseName);
                sharedPrefsInstance.putString("WorkPlaceId",WarehouseGUID);
                sharedPrefsInstance.putString("WorkplaceCode",WarehouseCode);
                mWarehouseGUID=WarehouseGUID;

                btn_get_workplace.setText(WarehouseName);
                stock_List_array.clear();
            }
        });
        builderType.setCancelable(false);
        pgH.dismiss();
        builderType.show();
    }
    public void getWareHouse(){
        URL getWareHouse = GetWareHouseList(url_,UserId);
        new AsyncTask_WareHouse().execute(getWareHouse);
    }
    public void DownloadASL(){
        pgH.setMessage(getResources().getString(R.string.msg_dialog_loading));
        pgH.setIndeterminate(true);
        pgH.setCancelable(false);
        pgH.show();

        txtFolders.setText("");
          SPFHelp sharedPrefsInstance = SPFHelp.getInstance();
        sharedPrefsInstance.putString("boolean_Array", "[]");
        sharedPrefsInstance.putString("selected_Uid_Array","[]");
        sharedPrefsInstance.putString("selected_Name_Array", "[]");

        Thread t = new Thread(new Runnable() {
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(3, TimeUnit.MINUTES)
                        .readTimeout(4, TimeUnit.MINUTES)
                        .writeTimeout(2, TimeUnit.MINUTES)
                        .build();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(url_)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(okHttpClient)
                        .build();
                final TerminalAPI assortiment_API = TerminalRetrofitClient.getApiTerminalService(url_);
                final Call<AssortmentListResult> assortiment = assortiment_API.getAssortimentListForStock(UserId,mWarehouseGUID);
                assortiment.enqueue(new Callback<AssortmentListResult>() {
                    @Override
                    public void onResponse(Call<AssortmentListResult> call, Response<AssortmentListResult> response) {

                        AssortmentListResult assortiment_body = response.body();
                        if(assortiment_body != null){
                            List<Assortment> assortmentListData = assortiment_body.getAssortments();

                            BaseApp myapp =((BaseApp)getApplication());

                            //myapp.SaveAsortment(assortmentListData);
                            int mas=0;
                            for (int i=0; i<assortmentListData.size();i++){
                                Boolean is_folder = assortmentListData.get(i).getIsFolder();
                                String barcode = assortmentListData.get(i).getBarCode();
                                String unit = assortmentListData.get(i).getUnit();
                                String unitin_package = assortmentListData.get(i).getUnitInPackage();
                                if (barcode==null)
                                    assortmentListData.get(i).setBarCode("null");
                                if (unit==null)
                                    assortmentListData.get(i).setUnit("null");
                                if (unitin_package==null)
                                    assortmentListData.get(i).setUnitInPackage("null");
                                String uid_asl = assortmentListData.get(i).getAssortimentID();

                                myapp.add_AssortimentID(uid_asl,assortmentListData.get(i));
                                if(is_folder){
                                    HashMap<String, Object> asl_folder = new HashMap<>();

                                    String asl_name = assortmentListData.get(i).getName();

                                    myapp.add_AssortimentID(uid_asl,assortmentListData.get(i));

                                    asl_folder.put("Name", asl_name);
                                    asl_folder.put("ID", uid_asl);
                                    asl_list.add(asl_folder);
                                    mas++;
                                }
                            }
                            kit_lists=new String[mas];
                            checkedItems = new boolean[mas];
                            for (int i=0;i<mas;i++){
                                checkedItems[i]=false;
                                kit_lists[i]=(String)asl_list.get(i).get("Name");
                            }
                            handler.obtainMessage(10, 12, -1).sendToTarget();
                        }else{
                            handler.obtainMessage(20,"Assortment null").sendToTarget();
                        }
                    }

                    @Override
                    public void onFailure(Call<AssortmentListResult> call, Throwable t) {
                        handler.obtainMessage(20,t.getMessage()).sendToTarget();
                    }
                });
            }
        });
        t.start();
    }
    class AsyncTask_WareHouse extends AsyncTask<URL, String, String> {
        @Override
        protected String doInBackground(URL... urls) {
            String response="false";
            try {
                response = Response_from_GetWareHouse(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                ((BaseApp)getApplication()).appendLog(e.getMessage(),WorkPlace.this);
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if(!response.equals("false") || response != null) {
                try {
                    JSONObject responseWareHouse = new JSONObject(response);
                    int ErrorCode = responseWareHouse.getInt("ErrorCode");
                    if (ErrorCode == 0) {
                        try {
                            String WareHouses =  responseWareHouse.getString("Warehouses");
                            if (WareHouses == null || WareHouses.equals("null")){
                                pgH.dismiss();
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(WorkPlace.this);
                                alertDialog.setTitle(getResources().getString(R.string.msg_dialog_title_atentie));
                                alertDialog.setMessage(getResources().getString(R.string.msg_list_warehouses_null));
                                alertDialog.setCancelable(false);
                                alertDialog.setPositiveButton(getResources().getString(R.string.msg_dialog_close), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                        SharedPreferences WorkPlace = getSharedPreferences("Work Place", MODE_PRIVATE);
//                                        WorkPlace.edit().clear().apply();
                                        finishAffinity();
                                    }
                                });
                                alertDialog.setNegativeButton(getResources().getString(R.string.msg_dialog_close_ramine), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                alertDialog.show();
                            }else{
                                JSONArray ListWare = responseWareHouse.getJSONArray("Warehouses");
                                for (int i = 0; i < ListWare.length(); i++) {
                                    JSONObject object = ListWare.getJSONObject(i);
                                    String WareCode = object.getString("Code");
                                    String WareName = object.getString("Name");
                                    String WareUid = object.getString("WarehouseID");
                                    HashMap<String, Object> WareHouse = new HashMap<>();
                                    WareHouse.put("Name", WareName);
                                    WareHouse.put("Code", WareCode);
                                    WareHouse.put("Uid", WareUid);
                                    stock_List_array.add(WareHouse);
                                }
                                show_WareHouse();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ((BaseApp)getApplication()).appendLog(e.getMessage(),WorkPlace.this);
                        }
                    }else{
                        pgH.dismiss();
                        Toast.makeText(WorkPlace.this,getResources().getString(R.string.msg_error_code) + ErrorCode, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ((BaseApp)getApplication()).appendLog(e.getMessage(),WorkPlace.this);
                }
            }else{
                pgH.dismiss();
                Toast.makeText(WorkPlace.this,getResources().getString(R.string.msg_nu_raspuns_server), Toast.LENGTH_SHORT).show();
            }

        }
    }
    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                v.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            }
        }

        return super.dispatchTouchEvent(event);
    }
}