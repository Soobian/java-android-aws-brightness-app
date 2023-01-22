package com.example.brightnessapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.AttachPolicyRequest;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.AmplifyConfiguration;
import com.example.brightnessapp.ui.noweLogowanie.logowanie;
import com.example.brightnessapp.ui.user.data.model.AmplifyHelper;
import com.example.brightnessapp.ui.user.ui.login.LoginActivity;
import com.example.brightnessapp.ui.wifi.wifiScanActivity;

import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line, to include the Auth plugin.
        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.configure(getApplicationContext());
            Log.i("AmplifyQuickstart", "Initialized Amplify");

        } catch (AmplifyException e) {
            e.printStackTrace();
        }





//        Amplify.Auth.signUp("test", "Test12345!!!", AuthSignUpOptions.builder().userAttribute(AuthUserAttributeKey.email(), "f.udalibartek@gmail.com").build(),
//                result -> Log.i("AmplifyQuickstart", "Result: " + result.toString()),
//                error -> Log.e("AmplifyQuickstart", "Sign up failed", error)
//        );
//        Amplify.Auth.confirmSignUp("test", "893308",
//                result -> Log.i("AmplifyQuickstart", result.isSignUpComplete() ? "Confirm signUp succeeded" : "Confirm sign up not complete"),
//                error -> Log.e("AmplifyQuickstart", "Confirm sign up failed", error)
//        );
//        Amplify.Auth.signOut(
//                error -> Log.e("AmplifyQuickstart", error.toString())
//        );
//        String identityId = AWSMobileClient.getInstance().getIdentityId();
//        Log.d("Identity Id", identityId);
//
//
//        AttachPolicyRequest attachPolicyReq = new AttachPolicyRequest();
//        attachPolicyReq.setPolicyName("myIOTPolicy"); // name of your IOT AWS policy
//        attachPolicyReq.setTarget(AWSMobileClient.getInstance().getIdentityId());
//        AWSIotClient mIotAndroidClient = new AWSIotClient(AWSMobileClient.getInstance());
//        mIotAndroidClient.setRegion(Region.getRegion(Regions.EU_CENTRAL_1)); // name of your IoT Region such as "us-east-1"
//        mIotAndroidClient.attachPolicy(attachPolicyReq);

        setContentView(R.layout.activity_main);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#6dd5ed"));

//        Intent nexIntent;
//        if(AmplifyHelper.isLoggedIn()){
//            AmplifyHelper.getUser();
//            nexIntent = new Intent(getBaseContext(), DashboardActivity.class);
//        }else {
//            nexIntent = new Intent(getBaseContext(), logowanie.class);
//        }

        Thread splash = new Thread() {
            public void run() {
                try {
                    StartAnimations();
                    sleep(1000);
                    Intent dashboard = new Intent(getBaseContext(), DashboardActivity.class);
                    startActivity(dashboard);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        splash.start();
    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        ConstraintLayout l = (ConstraintLayout) findViewById(R.id.backGround);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.main);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.imageView);
        TextView splashBanTxt = (TextView) findViewById(R.id.email);
        TextView splashTitle = (TextView) findViewById(R.id.textView2);

        iv.clearAnimation();
        iv.startAnimation(anim);

        splashBanTxt.clearAnimation();
        splashBanTxt.startAnimation(anim);

        splashTitle.clearAnimation();
        splashTitle.startAnimation(anim);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}