package com.example.brightnessapp.ui.user.data;

import android.util.Log;

import com.amplifyframework.core.Amplify;
import com.example.brightnessapp.ui.user.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {
            //check if user is signed in
//            Amplify.Auth.getCurrentUser( d -> {
//                Amplify.Auth.signOut(b -> Log.e("AuthQuickstart", b.toString()));
//            },a -> Log.i("jadn", String.valueOf(a)));
//            Amplify.Auth.wait();
                Amplify.Auth.signIn(
                        username,
                        password,
                        result -> Log.i("AuthQuickstart", result.toString()),
                        error -> Log.e("AuthQuickstart", error.toString())
                );

            Amplify.Auth.confirmSignIn(password,
                    result -> Log.i("AuthQuickstart confirm", result.toString()),
                    error -> Log.e("AuthQuickstart confirm", error.toString()));

            Amplify.Auth.fetchAuthSession(
                    result -> Log.i("AmplifyQuickstart", result.toString()),
                    error -> Log.e("AmplifyQuickstart", error.toString())
            );
            Amplify.Auth.wait();
            final LoggedInUser[] loggedInUser = new LoggedInUser[1];
            final String[] email = new String[1];


            Amplify.Auth.getCurrentUser(
                    user -> {
                        Amplify.Auth.fetchUserAttributes(
                                attributes -> {
                                    Log.i("AuthQuickstart", attributes.toString());
                                    email[0] = attributes.get(5).getValue();
                                },
                                error -> Log.e("AuthQuickstart", error.toString()));
                        loggedInUser[0] = new LoggedInUser(user.getUserId(), user.getUsername(), email[0]);
                        Log.i("AmplifyQuickstart", user.getUsername());

                    },
                    error -> Log.e("AmplifyQuickstart", error.toString())
            );

            if (loggedInUser[0] == null) {
                return new Result.Error(new IOException("Error logging in", new Throwable("Error logging in")));
            } else {
                return new Result.Success<>(loggedInUser[0]);
            }

        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {

    }
}