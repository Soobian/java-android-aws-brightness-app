package com.example.brightnessapp;

import android.util.Log;

import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper;

import java.security.KeyStore;

public class IotKeyHelper {
    public final String KEYSTORE_ALIAS = "default";
    public final String KEYSTORE_PW = "abc";
    public final String KEYSTORE_NAME = "abc";

    private KeyStore clientKeyStore;
    private String keyStorePath;

    public IotKeyHelper(String keyStorePath){
        this.keyStorePath = keyStorePath;
    }

    public void manageKeyStore(String userName, String cert, String pk){
        if(clientKeyStore == null){
            if(AWSIotKeystoreHelper.isKeystorePresent(keyStorePath, KEYSTORE_NAME+userName)){
                Log.e("ia4test", "key exist");
                clientKeyStore = getAWSIotKeyStore(userName);
            }
            else{
                Log.e("ia4test", "create key " + cert);
                AWSIotKeystoreHelper.saveCertificateAndPrivateKey(
                        KEYSTORE_ALIAS,
                        cert,
                        pk,
                        keyStorePath,
                        KEYSTORE_NAME+userName,
                        KEYSTORE_PW
                );
                clientKeyStore = getAWSIotKeyStore(userName);
            }
        }
    }

    public KeyStore getAWSIotKeyStore(String userName){
        return AWSIotKeystoreHelper.getIotKeystore(
                KEYSTORE_ALIAS,
                keyStorePath,
                KEYSTORE_NAME+userName,
                KEYSTORE_PW
        );
    }

    public boolean isClientKeystoreExist(){
        return clientKeyStore != null;
    }

    public KeyStore getClientKeyStore() {
        return clientKeyStore;
    }
}
