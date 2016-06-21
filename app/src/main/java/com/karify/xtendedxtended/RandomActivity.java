package com.karify.xtendedxtended;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RandomActivity extends AppCompatActivity {
    private FirebaseRemoteConfig firebaseRemoteConfig;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        firebaseRemoteConfig = ((XtendedApplication) getApplication()).getFirebaseRemoteConfig();
        setupRemoteConfig();
    }

    private void setupRemoteConfig() {
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
                openFragment();
            }
        });
    }

    private void openFragment() {
        if (firebaseRemoteConfig.getString(RemoteConfigConstants.TEST_FRAGMENT).equals(RemoteConfigConstants.RED_FRAGMENT)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, RedFragment.newInstance()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, BlueFragment.newInstance()).commit();
        }
    }
}
