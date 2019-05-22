package com.app.hidroponik;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.app.hidroponik.connection.API;
import com.app.hidroponik.connection.RestAdapter;
import com.app.hidroponik.connection.callbacks.CallbackInfo;
import com.app.hidroponik.data.SharedPref;
import com.app.hidroponik.model.Info;
import com.app.hidroponik.utils.CallbackDialog;
import com.app.hidroponik.utils.DialogUtils;
import com.app.hidroponik.utils.NetworkCheck;
import com.app.hidroponik.utils.PermissionUtil;
import com.app.hidroponik.utils.Tools;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitySplash extends AppCompatActivity {

    private SharedPref sharedPref;
    private boolean on_permission_result = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPref = new SharedPref(this);
        sharedPref.clearInfoData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Tools.needRequestPermission() && !on_permission_result) {
            String[] permission = PermissionUtil.getDeniedPermission(this);
            if (permission.length != 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(permission, 200);
                }
            } else {
                startProcess();
            }
        } else {
            startProcess();
        }
    }

    private void startProcess() {
        if (!NetworkCheck.isConnect(this)) {
            dialogNoInternet();
        } else {
            requestInfo();
        }
    }

    private void startActivityMainDelay() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent i = new Intent(ActivitySplash.this, ActivityMain.class);
                startActivity(i);
                finish();
            }
        };
        new Timer().schedule(task, 4000);
    }

    private void requestInfo() {
        API api = RestAdapter.createAPI();
        Call<CallbackInfo> callbackCall = api.getInfo(Tools.getVersionCode(this));
        callbackCall.enqueue(new Callback<CallbackInfo>() {
            @Override
            public void onResponse(Call<CallbackInfo> call, Response<CallbackInfo> response) {
                CallbackInfo resp = response.body();
                if (resp != null && resp.status.equals("success") && resp.info != null) {
                    Info info = sharedPref.setInfoData(resp.info);
                    checkAppVersion(info);
                } else {
                    dialogServerNotConnect();
                }
            }

            @Override
            public void onFailure(Call<CallbackInfo> call, Throwable t) {
                Log.e("onFailure", t.getMessage());
                dialogServerNotConnect();
            }
        });
    }

    private void checkAppVersion(Info info) {
        if (!info.active) {
            dialogOutDate();
        } else {
            startActivityMainDelay();
        }
    }

    public void dialogServerNotConnect() {
        Dialog dialog = new DialogUtils(this).buildDialogWarning(R.string.title_unable_connect, R.string.msg_unable_connect, R.string.TRY_AGAIN, R.string.CLOSE, R.drawable.img_no_connect, new CallbackDialog() {
            @Override
            public void onPositiveClick(Dialog dialog) {
                dialog.dismiss();
                retryOpenApplication();
            }

            @Override
            public void onNegativeClick(Dialog dialog) {
                finish();
            }
        });
        dialog.show();
    }

    public void dialogOutDate() {
        Dialog dialog = new DialogUtils(this).buildDialogInfo(R.string.title_info, R.string.msg_app_out_date, R.string.UPDATE, R.drawable.img_app_outdate, new CallbackDialog() {
            @Override
            public void onPositiveClick(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onNegativeClick(Dialog dialog) {
            }
        });
        dialog.show();
    }

    public void dialogNoInternet() {
        Dialog dialog = new DialogUtils(this).buildDialogWarning(R.string.title_no_internet, R.string.msg_no_internet, R.string.TRY_AGAIN, R.string.CLOSE, R.drawable.img_no_internet, new CallbackDialog() {
            @Override
            public void onPositiveClick(Dialog dialog) {
                dialog.dismiss();
                retryOpenApplication();
            }

            @Override
            public void onNegativeClick(Dialog dialog) {
                finish();
            }
        });
        dialog.show();
    }
    private void retryOpenApplication() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startProcess();
            }
        }, 2000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 200) {
            for (String perm : permissions) {
                boolean rationale = false;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    rationale = shouldShowRequestPermissionRationale(perm);
                }
                sharedPref.setNeverAskAgain(perm, !rationale);
            }
            on_permission_result = true;
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
