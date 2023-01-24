package com.example.brightnessapp;

import android.os.Bundle;
import android.os.Handler;
import android.telecom.ConnectionRequest;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.amazonaws.ClientConfiguration;

import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentity;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClient;
import com.amazonaws.services.cognitoidentity.model.GetIdRequest;
import com.amazonaws.services.cognitoidentity.model.GetIdResult;
import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenRequest;
import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenResult;
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProviderClient;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.AttachPolicyRequest;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult;
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession;
import com.amplifyframework.auth.cognito.options.AWSCognitoAuthSignInOptions;
import com.amplifyframework.auth.cognito.options.AuthFlowType;
import com.amplifyframework.auth.result.step.AuthNextSignInStep;
import com.amplifyframework.core.Amplify;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;


import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


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
    private static final String COGNITO_USER_POOL_ID = "eu-central-1_zhw75TjiC";
    private static final Regions MY_REGION = Regions.EU_CENTRAL_1;
    private static final String topic = "ESP32/pub";

    public static String OUT = "";
    public static String BRIGHTNESS = "0.0";
    //    AWSIotMqttManager mqttManager = new AWSIotMqttManager("clientid", CUSTOMER_SPECIFIC_ENDPOINT);
    String clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView username = (TextView) findViewById(R.id.username);
        TextView email = (TextView) findViewById(R.id.email);

//        Amplify.Auth.signIn(
//                "fudalibartek@gmail.com",
//                "Bartek951753!",
//                result -> Log.i("AuthQuickstart", result.isSignedIn()? "Sign in succeeded" : "Sign in not complete"),
//                error -> Log.e("AuthQuickstart", error.toString())
//        );
        try {
            AWSCognitoAuthSignInOptions options = AWSCognitoAuthSignInOptions.builder().authFlowType(AuthFlowType.USER_SRP_AUTH).build();
            Amplify.Auth.signIn(
                    "fudalibartek@gmail.com",
                    "Bartek951753!",
                    options,
                    result ->
                    {
                        AuthNextSignInStep nextStep = result.getNextStep();
                        switch (nextStep.getSignInStep()) {
                            case CONFIRM_SIGN_IN_WITH_SMS_MFA_CODE: {
                                Log.i("AuthQuickstart", "SMS code sent to " + nextStep.getCodeDeliveryDetails().getDestination());
                                Log.i("AuthQuickstart", "Additional Info :" + nextStep.getAdditionalInfo());
                                // Prompt the user to enter the SMS MFA code they received
                                // Then invoke `confirmSignIn` api with the code
                                break;
                            }
                            case CONFIRM_SIGN_IN_WITH_CUSTOM_CHALLENGE: {
                                Log.i("AuthQuickstart", "Custom challenge, additional info: " + nextStep.getAdditionalInfo());
                                // Prompt the user to enter custom challenge answer
                                // Then invoke `confirmSignIn` api with the answer
                                break;
                            }
                            case CONFIRM_SIGN_IN_WITH_NEW_PASSWORD: {
                                Log.i("AuthQuickstart", "Sign in with new password, additional info: " + nextStep.getAdditionalInfo());
                                // Prompt the user to enter a new password
                                // Then invoke `confirmSignIn` api with new password
                                break;
                            }
                            case RESET_PASSWORD: {
                                Log.i("AuthQuickstart", "Reset password, additional info: " + nextStep.getAdditionalInfo());
                                // User needs to reset their password.
                                // Invoke `resetPassword` api to start the reset password
                                // flow, and once reset password flow completes, invoke
                                // `signIn` api to trigger signIn flow again.
                                break;
                            }
                            case CONFIRM_SIGN_UP: {
                                Log.i("AuthQuickstart", "Confirm signup, additional info: " + nextStep.getAdditionalInfo());
                                // User was not confirmed during the signup process.
                                // Invoke `confirmSignUp` api to confirm the user if
                                // they have the confirmation code. If they do not have the
                                // confirmation code, invoke `resendSignUpCode` to send the
                                // code again.
                                // After the user is confirmed, invoke the `signIn` api again.
                                break;
                            }
                            case DONE: {
                                Log.i("AuthQuickstart", "SignIn complete");
                                // User has successfully signed in to the app
                                break;
                            }
                        }
                    },
                    error -> Log.e("AuthQuickstart", "SignIn failed: " + error)
            );
        } catch (Exception error) {
            Log.e("AuthQuickstart", "Unexpected error occurred: " + error);
        }
        AmazonCognitoIdentityProviderClient identityProviderClient = new
                AmazonCognitoIdentityProviderClient(new AnonymousAWSCredentials(), new ClientConfiguration());
//
        identityProviderClient.setRegion(Region.getRegion(Regions.EU_CENTRAL_1));
        CognitoUserPool userPool = new CognitoUserPool(getApplicationContext(), "eu-central-1_zhw75TjiC", "j4b48fg4542njbiqj5birp41m", null, identityProviderClient);
        CognitoUser cogUser = userPool.getUser();
        Map<String, String> logins = new HashMap<>();

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "eu-central-1:d9a075f8-2b1a-406e-8a6c-943cd1c14af2", // Identity pool ID
                Regions.EU_CENTRAL_1 // Region
        );

        AuthenticationHandler authenticationHandler = new AuthenticationHandler() {


            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                String ids = userSession.getIdToken().getJWTToken();
                Log.d("MyToken", "session id___ " + userSession.getIdToken().getExpiration() + "___" + userSession.getIdToken().getIssuedAt() + "___" + userSession.getIdToken().getJWTToken());
                String idToken = userSession.getIdToken().getJWTToken();
                logins.put("cognito-idp:eu-central-1:004319199759:userpool/eu-central-1_zhw75TjiC", idToken);
                credentialsProvider.setLogins(logins);
//                Intent pubSub = new Intent(MainActivity.this, PubSubActivity.class);
//                pubSub.putExtra("token",""+ids);
//                startActivity(pubSub);

                //MainActivity.this.finish();

            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
                Log.d("MyToken", "getAuthenticationDetails");
                AuthenticationDetails authenticationDetails = new AuthenticationDetails("bf420", "Bartek951753!", logins);

                authenticationContinuation.setAuthenticationDetails(authenticationDetails);
                authenticationContinuation.continueTask();
            }

            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
                Log.d("MyToken", "getMFACode");
                multiFactorAuthenticationContinuation.continueTask();
            }

            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) {
                Log.d("MyToken", "authenticationChallenge" + continuation.getChallengeName());
            }

            @Override
            public void onFailure(Exception exception) {
                exception.printStackTrace();
                Log.d("MyToken", "onFailure");
            }
        };
//        AmazonCognitoIdentity cognitoIdentity = new AmazonCognitoIdentityClient(credentialsProvider);
//        cognitoIdentity.setRegion(Region.getRegion(Regions.EU_CENTRAL_1));
//        cogUser.getSession(authenticationHandler);


//        cogUser.getSession(authenticationHandler);
//        credentialsProvider.setLogins(logins);
//
//        GetIdRequest getIdReq = new GetIdRequest();
//        getIdReq.setLogins(credentialsProvider.getLogins()); //or if you have already set provider logins just use credentialsProvider.getLogins()
//        getIdReq.setIdentityPoolId("eu-central-1:d9a075f8-2b1a-406e-8a6c-943cd1c14af2");

//        GetIdResult getIdRes = cognitoIdentity.getId(getIdReq);

//            AttachPrincipalPolicyRequest attachPrincipalPolicyRequest = new AttachPrincipalPolicyRequest();
//            attachPrincipalPolicyRequest.setPrincipal("userPool");
//            attachPrincipalPolicyRequest.setPrincipal(getIdRes.getIdentityId());
//            new AWSIotClient(credentialsProvider).attachPrincipalPolicy(attachPrincipalPolicyRequest);

//        AttachPolicyRequest attachPolicyReq = new AttachPolicyRequest();
//        attachPolicyReq.setPolicyName("myIOTPolicy"); // name of your IoT AWS policy
////        attachPolicyReq.setTarget(getIdRes.getIdentityId());
//        AWSIotClient mIotAndroidClient = new AWSIotClient(AWSMobileClient.getInstance());
//        mIotAndroidClient.setRegion(Region.getRegion(Regions.EU_CENTRAL_1)); // name of your aws region such as "us-east-1"
//        mIotAndroidClient.attachPolicy(attachPolicyReq);

//       AmazonCognitoIdentity client = new AmazonCognitoIdentityClient(credentialsProvider);
//        new AWSIotClient(credentialsProvider);
//        mqttManager.connect(credentialsProvider, new AWSIotMqttClientStatusCallback() {
//            @Override
//            public void onStatusChanged(AWSIotMqttClientStatus status, Throwable throwable) {
//                Log.d(LOG_TAG, "Status = " + String.valueOf(status));
//            }
//        });
        Amplify.Auth.fetchAuthSession(
                result -> {
                    AWSCognitoAuthSession cognitoAuthSession = (AWSCognitoAuthSession) result;
                    switch (cognitoAuthSession.getIdentityIdResult().getType()) {
                        case SUCCESS:
                            Log.i("AmplifyQuickstartHALOHALO", cognitoAuthSession.getIdentityIdResult().getValue());
                            break;
                        case FAILURE:
                            Log.e("AmplifyQuickstart", cognitoAuthSession.getIdentityIdResult().getError().toString());
                            break;
                    }
                },
                error -> Log.e("AmplifyQuickstart", error.toString())
        );

//        credentialsProvider.getIdentityId();
//        username.setText(logowanie.loggedUser.getUsername());
//        email.setText(logowanie.loggedUser.getEmail());
//        AmazonCognitoIdentityClient cognitoClient = new AmazonCognitoIdentityClient(credentialsProvider);


        Amplify.Auth.fetchAuthSession(
                result -> {

                    AWSCognitoAuthSession cognitoAuthSession = (AWSCognitoAuthSession) result;
                    switch (cognitoAuthSession.getIdentityIdResult().getType()) {
                        case SUCCESS:
                            Log.i("AuthQuickStart", "IdentityId: " + cognitoAuthSession.getIdentityIdResult().getValue());
                            break;
                        case FAILURE:
                            Log.i("AuthQuickStart", "IdentityId not present because: " + cognitoAuthSession.getIdentityIdResult().getError().toString());
                    }
                },
                error -> Log.e("AuthQuickStart", error.toString())
        );
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


//        AttachPolicyRequest attachPolicyRequest = new AttachPolicyRequest();
//        attachPolicyRequest.setPolicyName("userPolicy");
//        attachPolicyRequest.setTarget(credentialsProvider.getIdentityPoolId());
//
//        AWSIotClient awsIotClient = new AWSIotClient(credentialsProvider);
//        awsIotClient.setRegion(Region.getRegion(Regions.EU_CENTRAL_1));
//        awsIotClient.attachPolicy(attachPolicyRequest);


        AWSIotMqttManager manager = new AWSIotMqttManager(clientId, CUSTOMER_SPECIFIC_ENDPOINT);


        manager.connect(credentialsProvider, new AWSIotMqttClientStatusCallback() {
            @Override
            public void onStatusChanged(AWSIotMqttClientStatus status, Throwable throwable) {
                Log.d("MQTT", "onStatusChanged: " + status);
                AWSIotMqttQos QoS = AWSIotMqttQos.QOS0;
                if (status == AWSIotMqttClientStatus.Connected) {
                    manager.subscribeToTopic(topic, QoS, new AWSIotMqttNewMessageCallback() {
                        @Override
                        public void onMessageArrived(String topic, byte[] data) {
                            try {
                                String message = new String(data, "UTF-8");
                                Log.d("MQTT", "Message arrived:");
                                Log.d("MQTT", "   Topic: " + topic);
                                Log.d("MQTT", " Message: " + message);
                            } catch (UnsupportedEncodingException e) {
                                Log.e("MQTT", "Message encoding error.", e);
                            }
                        }
                    });
                }
            }
        });

//
//        brightnessMonitorRunnable.run();
//        new BrightnessMonitorAsyncTask().execute();

//        Date now = new Date();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
//        String dateString = simpleDateFormat.format(now);

//        TextToday.setText(dateString);


//        // Filename of KeyStore file on the filesystem
//        String KEYSTORE_NAME = "java/com/example/brightnessapp/certificate.crt";
//        // Password for the private key in the KeyStore
//        String KEYSTORE_PASSWORD = "private.key";
//        // Certificate and key aliases in the KeyStore
//        String CERTIFICATE_ID = "default";


//        AWSIotMqttManager mqttManager = new AWSIotMqttManager(
//                clientId,
//                CUSTOMER_SPECIFIC_ENDPOINT);
//
//        String certificate_content = "";
//        String private_key_content = "";
//
//        try {
//            System.out.println(Paths.get(getFilesDir().getAbsolutePath()));
//            certificate_content = new String(Files.readAllBytes(Paths.get(
//                    getFilesDir().getAbsolutePath() + "/certificate.crt"
//            )));
//            private_key_content = new String(Files.readAllBytes(Paths.get(
//                    getFilesDir().getAbsolutePath() + "/private.key"
//            )));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        iotKeyHelper = new IotKeyHelper(getFilesDir().getAbsolutePath());
//
//        Log.i(LOG_TAG, certificate_content);
//        Log.i(LOG_TAG, private_key_content);
//        iotKeyHelper.manageKeyStore(
//                clientId,
//                certificate_content,
//                private_key_content
//        );
//
//        KeyStore ks = iotKeyHelper.getAWSIotKeyStore(clientId);
//        mqttManager.connect(ks, (status, throwable) -> Log.d(LOG_TAG, "Status = " + status));
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