package com.lovelyfatbears.thoniorf.alarmsmscommander;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements AddAlarmDialog.NoticeDialogListener {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private static final int MY_PERMISSIONS_REQUEST_PHONE_CALL = 1;
    List<Alarm> alarms;
    String prefName = "mysharedpref";
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Intent callIntent;
    RecyclerView recyView;
    AlarmRecyclerAdapter recyAdapter;
    private RecyclerView.LayoutManager recyLayManager;
    private AddAlarmDialog addDialog;
    private FloatingActionButton btn_fa_add;
    private SmsManager smsmanager;

    @Override
    public void onDialogPositiveClick(AddAlarmDialog dialog) {
        alarms.add(new Alarm(dialog.number.getText().toString(), dialog.password.getText().toString(),dialog.name.getText().toString()));
        storeValues();
        recyAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDialogNegativeClick(AddAlarmDialog dialog) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);

        recyView = (RecyclerView) findViewById(R.id.alarm_list);

        recyLayManager = new LinearLayoutManager(this);
        recyView.setLayoutManager(recyLayManager);

        sharedPref = getSharedPreferences(prefName,MODE_PRIVATE);

        alarms = new ArrayList<>();
        readValues();

        recyAdapter = new AlarmRecyclerAdapter(alarms);
        recyView.setAdapter(recyAdapter);

        smsmanager = SmsManager.getDefault();
        callIntent = new Intent(Intent.ACTION_CALL);

        initWidget();
        initListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        storeValues();
    }

    protected void initWidget() {
        btn_fa_add = (FloatingActionButton) findViewById(R.id.btn_fa_add);
    }

    protected void initListener() {
        btn_fa_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDialog = new AddAlarmDialog();
                addDialog.show(getSupportFragmentManager(), "add_alarm");
            }
        });
    }

    private void sendSMS(String phoneNumber, String message) {
        smsmanager.sendTextMessage(phoneNumber, null, message, null, null);
        Toast.makeText(getApplicationContext(), getString(R.string.send), Toast.LENGTH_LONG).show();
    }


    private void makeCall(String phone) {
        callIntent.setData(Uri.parse("tel:" + phone));
        startActivity(callIntent);
    }

    protected void trySend(String phone,String passwd, String code) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            sendSMS(phone, passwd+code);
        }

    }

    protected void tryCall(String phone) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_PHONE_CALL);
            }
        }
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            makeCall(phone);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), getString(R.string.sms_permission_failed), Toast.LENGTH_LONG).show();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_PHONE_CALL: {
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), getString(R.string.call_permission_failed), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    protected void storeValues() {
        editor = sharedPref.edit();
        Set<String> phones = new ArraySet<>();
        for (int i = 0; i < alarms.size(); i++) {
            phones.add(alarms.get(i).number);
        }
        Set<String> names = new ArraySet<>();
        for (int i = 0; i < alarms.size(); i++) {
            names.add(alarms.get(i).name);
        }
        Set<String> passwds = new ArraySet<>();
        for (int i = 0; i < alarms.size(); i++) {
            passwds.add(alarms.get(i).password);
        }

        editor.putStringSet(getString(R.string.phones),phones);
        editor.putStringSet(getString(R.string.names),names);
        editor.putStringSet(getString(R.string.passwds),passwds);
        editor.apply();
    }

    protected void readValues() {
        Set<String> phones = new ArraySet<>(sharedPref.getStringSet(getString(R.string.phones),null));
        Set<String> names = new ArraySet<>(sharedPref.getStringSet(getString(R.string.names),null));
        Set<String> passwds = new ArraySet<>(sharedPref.getStringSet(getString(R.string.passwds),null));
        String phones_array[] =  Arrays.copyOf(phones.toArray(),phones.size(),String[].class);
        String names_array[] = Arrays.copyOf(names.toArray(),names.size(),String[].class);
        String passwds_array[] = Arrays.copyOf(passwds.toArray(),passwds.size(),String[].class);

        if(phones.size() == names.size() && names.size() == passwds.size()) {
            for(int i = 0; i < phones.size(); i ++){
                alarms.add(new Alarm(phones_array[i],passwds_array[i],names_array[i]));
            }
        }
    }
}
