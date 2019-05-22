package com.app.hidroponik;

import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.app.hidroponik.connection.API;
import com.app.hidroponik.connection.RestAdapter;
import com.app.hidroponik.connection.callbacks.CallbackDevice;
import com.app.hidroponik.data.SharedPref;
import com.app.hidroponik.model.DeviceInfo;
import com.app.hidroponik.utils.NetworkCheck;
import com.app.hidroponik.utils.Tools;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThisApplication extends Application {

    private static ThisApplication mInstance;
    private SharedPref sharedPref;
    private FirebaseAnalytics mFirebaseAnalytics;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            obtainFirebaseToken();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        sharedPref = new SharedPref(this);

        obtainFirebaseToken();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    public static synchronized ThisApplication getInstance() {
        return mInstance;
    }

    private void obtainFirebaseToken() {
        String token = FirebaseInstanceId.getInstance().getToken();
        if (TextUtils.isEmpty(token)) {
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 10 * 1000);
        } else {
            sendRegistrationToServer(token);
        }
    }

    private void sendRegistrationToServer(String token) {
        Log.d("FCM_TOKEN", token + "");
        if (NetworkCheck.isConnect(this) && !TextUtils.isEmpty(token) && sharedPref.isOpenAppCounterReach()) {
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.device = Tools.getDeviceName();
            deviceInfo.os_version = Tools.getAndroidVersion();
            deviceInfo.app_version = Tools.getVersionCode(this) + " (" + Tools.getVersionNamePlain(this) + ")";
            deviceInfo.serial = Tools.getDeviceID(this);
            deviceInfo.regid = token;

            API api = RestAdapter.createAPI();
            Call<CallbackDevice> callbackCall = api.registerDevice(deviceInfo);
            callbackCall.enqueue(new Callback<CallbackDevice>() {
                @Override
                public void onResponse(Call<CallbackDevice> call, Response<CallbackDevice> response) {
                    CallbackDevice resp = response.body();
                    if (resp != null && resp.status.equals("success")) {
                        sharedPref.setOpenAppCounter(0);
                    }
                }

                @Override
                public void onFailure(Call<CallbackDevice> call, Throwable t) {
                    Log.e("onFailure", t.getMessage());
                }
            });
        }
    }

    public void saveLogEvent(long id, String name, String type) {
        Bundle bundle = new Bundle();
        bundle.putLong(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public void saveCustomLogEvent(String event, String key, String value) {
        Bundle params = new Bundle();
        params.putString(key, value);
        mFirebaseAnalytics.logEvent(event, params);
    }
}
