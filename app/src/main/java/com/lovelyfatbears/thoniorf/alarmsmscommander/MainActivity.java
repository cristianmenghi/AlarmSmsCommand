package com.lovelyfatbears.thoniorf.alarmsmscommander;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import static android.R.attr.defaultValue;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    private static final int MY_PERMISSIONS_REQUEST_PHONE_CALL =1;
    private SmsManager smsmanager;
    private Button arm,disarm,status;
    private String[] codes = {"ARM","DISARM","CHECK"};
    private EditText phone,passwd;
    private String message;
    private CheckBox remember;
    private ImageButton call;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Intent callIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        smsmanager = SmsManager.getDefault();
        callIntent = new Intent(Intent.ACTION_CALL);
        initWidget();
        initListener();
        readValues();

    }

    protected  void initWidget() {
        arm = (Button) findViewById(R.id.btn_arm);
        disarm = (Button) findViewById(R.id.btn_disarm);
        status = (Button) findViewById(R.id.btn_status);
        phone = (EditText) findViewById(R.id.edittxt_phone);
        passwd = (EditText) findViewById(R.id.edittxt_passwd);
        remember = (CheckBox) findViewById(R.id.chb_remember);
        call = (ImageButton) findViewById(R.id.imagebtn_call);

    }

    protected void initListener() {
        arm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPhoneSetted() && isPasswdSetted()) {
                    generateMessage(codes[0]);
                    askPermissionToSend();
                }
            }
        });
        disarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPhoneSetted() && isPasswdSetted()) {
                    generateMessage(codes[1]);
                    askPermissionToSend();
                }
            }
        });
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPhoneSetted() && isPasswdSetted()) {
                    generateMessage(codes[2]);
                    askPermissionToSend();
                }
            }
        });
        remember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPhoneSetted() && isPasswdSetted() && remember.isChecked()) {
                    storeValues();
                }
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPhoneSetted()) {
                    callIntent.setData(Uri.parse("tel:"+phone.getText().toString()));
                    askPermissionToCall();
                }
            }
        });
    }

    protected boolean isPhoneSetted() {
        return phone.getText().length() != 0;
    }

    protected boolean isPasswdSetted() {
        return  passwd.getText().length() != 0;
    }

    private void sendSMS(String phoneNumber, String message) {
        smsmanager.sendTextMessage(phoneNumber, null, message, null, null);
        Toast.makeText(getApplicationContext(), "SMS sent", Toast.LENGTH_LONG).show();
    }

    private String generateMessage(String code) {
        message = passwd.getText().toString() +code;
        return message;
    }

    private void makeCall() {
        startActivity(callIntent);
    }

    private  void askPermissionToSend() {
        // Here, thisActivity is the current activity
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
        } else {
            sendSMS(phone.getText().toString(),message);
        }
    }
    private  void askPermissionToCall() {
        // Here, thisActivity is the current activity
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
        } else {
            makeCall();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSMS(phone.getText().toString(),message);


                } else {
                    Toast.makeText(getApplicationContext(), "SMS failed, try again", Toast.LENGTH_LONG).show();

                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_PHONE_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeCall();
                } else {
                    Toast.makeText(getApplicationContext(), "Phone call permission denied", Toast.LENGTH_LONG).show();

                }
            }
        }
    }

    protected void storeValues() {
        editor = sharedPref.edit();
        editor.putString(getString(R.string.phone), phone.getText().toString()).putString(getString(R.string.passwd), passwd.getText().toString()).apply();
    }
    protected  void readValues() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String stored_phone = sharedPref.getString(getString(R.string.phone),"");
        String stored_passwd = sharedPref.getString(getString(R.string.passwd),"");
        if(!stored_phone.equals("")) {
            phone.setText(stored_phone);
        }
        if(!stored_passwd.equals("")) {
            passwd.setText(stored_passwd);
        }
    }
}
