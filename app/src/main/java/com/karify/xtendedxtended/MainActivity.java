package com.karify.xtendedxtended;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private FirebaseRemoteConfig firebaseRemoteConfig;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_default_text)
    TextView defaultTextView;

    @BindView(R.id.cb_default_bool)
    CheckBox defaultCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        firebaseRemoteConfig = ((XtendedApplication) getApplication()).getFirebaseRemoteConfig();
        setUpViews();
    }

    private void setUpViews() {
        defaultTextView.setText(firebaseRemoteConfig.getString(RemoteConfigConstants.DEFAULT_TEXT));
        defaultCheckBox.setChecked(firebaseRemoteConfig.getBoolean(RemoteConfigConstants.DEFAULT_BOOLEAN));
        firebaseRemoteConfig.fetch(BuildConfig.REMOTE_CONFIG_CACHE_EXPIRATION).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    firebaseRemoteConfig.activateFetched();
                } else {
                    final Exception exception = task.getException();
                    final String errorMsg = exception != null ? exception.getMessage() : "some how don't have an error message";
                    if (exception != null) {
                        FirebaseCrash.report(exception);
                    }
                    FirebaseCrash.logcat(Log.INFO, "TAG?", errorMsg);

                }
                updateViews();
            }
        });
    }

    @OnClick(R.id.bt_crash)
    void onClickCrash() {
        throw new RuntimeException("This is bad, now we have a real crash");
    }

    @OnClick(R.id.bt_report)
    void onClickReport() {
        FirebaseCrash.report(new Exception("My first Android non-fatal error"));
    }

    @OnClick(R.id.bt_random_screen)
    void onClickNewActivity() {
        Intent intent = new Intent(this, RandomActivity.class);
        startActivity(intent);
    }

    private void updateViews() {
        defaultTextView.setText(firebaseRemoteConfig.getString(RemoteConfigConstants.DEFAULT_TEXT));
        defaultCheckBox.setChecked(firebaseRemoteConfig.getBoolean(RemoteConfigConstants.DEFAULT_BOOLEAN));
    }
}
