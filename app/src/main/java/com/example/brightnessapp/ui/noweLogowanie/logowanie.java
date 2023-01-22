package com.example.brightnessapp.ui.noweLogowanie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amplifyframework.core.Amplify;
import com.example.brightnessapp.DashboardActivity;
import com.example.brightnessapp.R;
import com.example.brightnessapp.ui.user.data.model.AmplifyHelper;

import java.util.concurrent.atomic.AtomicReference;

public class logowanie extends AppCompatActivity {

    public static LoggedUser loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logowanie);
    }

    public void login1(View view) {

        if (AmplifyHelper.isLoggedIn()) {
            Amplify.Auth.signOut(
                    error -> Log.e("AuthQuickstart", error.toString())
            );
        }

        TextView login = findViewById(R.id.loginUsername);
        TextView password = findViewById(R.id.loginPassword);
        String loginText = login.getText().toString();
        String passwordText = password.getText().toString();

        Amplify.Auth.signIn(
                loginText,
                passwordText,
                result -> Log.i("AuthQuickstart", result.toString()),
                error -> Log.e("AuthQuickstart", error.toString())
        );

//        Amplify.Auth.confirmSignIn(password,
//                result -> Log.i("AuthQuickstart confirm", result.toString()),
//                error -> Log.e("AuthQuickstart confirm", error.toString()));

        Amplify.Auth.fetchAuthSession(
                result -> Log.i("AmplifyQuickstart", result.toString()),
                error -> Log.e("AmplifyQuickstart", error.toString())
        );
        AtomicReference<String> email = new AtomicReference<>();
        Amplify.Auth.getCurrentUser(
                user -> {
                    Amplify.Auth.fetchUserAttributes(
                            attributes -> {
                                Log.i("AuthQuickstart", attributes.toString());
                                email.set(attributes.get(5).getValue());
                                logowanie.loggedUser = new LoggedUser(user.getUserId(), user.getUsername(), email.get());
                                Log.i("AmplifyQuickstart", logowanie.loggedUser.toString());
                            },
                            error -> Log.e("AuthQuickstart", error.toString()));


                },
                error -> Log.e("AmplifyQuickstart", error.toString())
        );

        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }
}