package ru.alexbykov.permissionssample.activities;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import ru.alexbykov.nopermission.PermissionHelper;
import ru.alexbykov.permissionssample.R;

public class LocationSampleActivity extends AppCompatActivity {


    private final String TAG = "PermissionResult: ";
    private static final int LAYOUT = R.layout.activity_location_sample;
    private PermissionHelper permissionHelper;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        setupPermissionHelper();
        setupUI();
        setupUX();
    }

    private void setupUI() {
        tvResult = findViewById(R.id.tv_result);
    }


    private void setupUX() {
        findViewById(R.id.btnAskPermission).setOnClickListener(view -> askLocationPermission());
    }

    private void askLocationPermission() {
        permissionHelper.check(Manifest.permission.ACCESS_COARSE_LOCATION)
                .withDialogBeforeRun(R.string.dialog_before_run_title, R.string.dialog_before_run_message, R.string.dialog_positive_button)
                .setDialogPositiveButtonColor(android.R.color.holo_orange_dark)
                .onSuccess(this::onSuccess)
                .onDenied(this::onDenied)
                .onNeverAskAgain(this::onNeverAskAgain)
                .run();
    }


    private void onSuccess() {
        Log.d(TAG, "LocationSuccess");
        tvResult.setText(R.string.result_success);
    }


    private void onNeverAskAgain() {
        Log.d(TAG, "LocationNeverAskAgain");
        tvResult.setText(R.string.result_never_ask_again);
    }

    private void onDenied() {
        Log.d(TAG, "LocationDenied");
        tvResult.setText(R.string.result_denied);
    }

    private void setupPermissionHelper() {
        permissionHelper = new PermissionHelper(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
