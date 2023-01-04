package com.example.brightnessapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.AttachPolicyRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.UUID;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static final String LOG_TAG = DashboardActivity.class.getCanonicalName();

    String ipAddress;
    String threshold;

    TextView TextBrightness;
    TextView TextToday;

    Handler brightnessMonitorHandler;
    private IotKeyHelper iotKeyHelper;

    // AWS IOT

    private static final String CUSTOMER_SPECIFIC_ENDPOINT = "a2rgmisuazfyyc-ats.iot.eu-central-1.amazonaws.com";
    private static final String COGNITO_POOL_ID = "eu-central-1:d9a075f8-2b1a-406e-8a6c-943cd1c14af2";
    private static final Regions MY_REGION = Regions.EU_CENTRAL_1;
    private static final String topic = "bright";

    public static String OUT = "";
    public static String BRIGHTNESS = "0.0";

    AWSIotMqttManager mqttManager;
    String clientId;
    CognitoCachingCredentialsProvider credentialsProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.brightnessMonitorHandler = new Handler();

        TextBrightness = (TextView) findViewById(R.id.textBrightness);
        clientId = UUID.randomUUID().toString();

        /*
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                COGNITO_POOL_ID,
                MY_REGION
        );

        mqttManager = new AWSIotMqttManager(clientId, CUSTOMER_SPECIFIC_ENDPOINT);

        Log.d(LOG_TAG, "clientId = " + clientId);

        brightnessMonitorRunnable.run();
        new BrightnessMonitorAsyncTask().execute();

        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        String dateString = simpleDateFormat.format(now);

        TextToday.setText(dateString);
         */


        // Filename of KeyStore file on the filesystem
        String KEYSTORE_NAME = "certificate.crt";
        // Password for the private key in the KeyStore
        String KEYSTORE_PASSWORD = "private.key";
        // Certificate and key aliases in the KeyStore
        String CERTIFICATE_ID = "default";


        AWSIotMqttManager mqttManager = new AWSIotMqttManager(
                clientId,
                CUSTOMER_SPECIFIC_ENDPOINT);

        String certificate_content = "";
        String private_key_content = "";

        try {
            certificate_content = new String(Files.readAllBytes(Paths.get(
                    getFilesDir().getAbsolutePath() + "/certificate.crt"
            )));
            private_key_content = new String(Files.readAllBytes(Paths.get(
                    getFilesDir().getAbsolutePath() + "/private.key"
            )));
        } catch (IOException e) {
            e.printStackTrace();
        }

        iotKeyHelper = new IotKeyHelper(getFilesDir().getAbsolutePath());

        Log.i(LOG_TAG, certificate_content);
        Log.i(LOG_TAG, private_key_content);
        iotKeyHelper.manageKeyStore(
                clientId,
                certificate_content,
                private_key_content
        );

        KeyStore ks = iotKeyHelper.getAWSIotKeyStore(clientId);

        mqttManager.connect(ks, (status, throwable) -> Log.d(LOG_TAG, "Status = " + status));
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}