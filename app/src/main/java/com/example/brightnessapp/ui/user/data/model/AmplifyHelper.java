package com.example.brightnessapp.ui.user.data.model;

import android.util.Log;

import com.amplifyframework.auth.cognito.AWSCognitoAuthSession;
import com.amplifyframework.core.Amplify;
import com.example.brightnessapp.ui.user.data.LoginDataSource;
import com.example.brightnessapp.ui.user.data.LoginRepository;

import java.util.concurrent.atomic.AtomicBoolean;

public class AmplifyHelper {
    //class to check if user is logged in, and if is, return the LoggedInUser user
    public static LoggedInUser user = null;

    public synchronized static boolean isLoggedIn() {
        AtomicBoolean isLoggedIn = new AtomicBoolean(true);
        Amplify.Auth.fetchAuthSession(
                result -> {
                    if (result.isSignedIn()) {
                        Log.d("AmplifyQuickstart", "User is signed in");
                        Amplify.Auth.getCurrentUser(
                                user -> {
                                    AmplifyHelper.user = new LoggedInUser(user.getUserId(), user.getUsername(), user.getUsername());
                                },

                        error -> {
                            isLoggedIn.set(false);

                            Log.e("AmplifyQuickstart", error.toString());
                                    AmplifyHelper.user = null;
                                }
                        );};
                },
                error -> System.out.println("Fetch session failed with error " + error)
        );
        System.out.println("Is logged in: " + isLoggedIn.get());
        return isLoggedIn.get();
    }
    public synchronized static LoggedInUser getUser() {
        LoginRepository.getInstance().setLoggedInUser(user);
        return user;
    }
}
