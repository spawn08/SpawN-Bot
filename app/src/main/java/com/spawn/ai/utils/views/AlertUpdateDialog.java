package com.spawn.ai.utils.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.spawn.ai.R;

public class AlertUpdateDialog extends Dialog implements View.OnClickListener {

    public Activity activity;
    private Button update;

    public AlertUpdateDialog(@NonNull Activity context) {
        super(context);
        this.activity = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCancelable(false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alert_update_app);
        update = (Button) findViewById(R.id.update_app);
        update.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update_app:
                try {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.spawn.ai")));
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(e.getMessage());
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.spawn.ai")));
                }
                break;
        }
    }
}
