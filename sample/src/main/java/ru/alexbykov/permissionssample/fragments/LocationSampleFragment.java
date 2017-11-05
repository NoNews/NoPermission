package ru.alexbykov.permissionssample.fragments;


import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.alexbykov.nopermission.PermissionHelper;
import ru.alexbykov.permissionssample.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationSampleFragment extends Fragment {


    private static final int LAYOUT = R.layout.activity_location_sample;
    private final String TAG = "PermissionResult: ";
    private View view;
    private TextView tvResult;
    private PermissionHelper permissionHelper;

    public static Fragment newInstance() {
        return new LocationSampleFragment();
    }


    public LocationSampleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        setupPermissionHelper();
        setupUI();
        setupUX();
        return view;
    }

    private void setupUI() {
        tvResult = view.findViewById(R.id.tv_result);
    }


    private void setupUX() {
        view.findViewById(R.id.btnAskPermission).setOnClickListener(view -> askLocationPermission());
    }

    private void askLocationPermission() {
        permissionHelper.check(Manifest.permission.ACCESS_COARSE_LOCATION)
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
        permissionHelper.startApplicationSettingsActivity();
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


    @Override
    public void onDestroy() {
        permissionHelper.unsubscribe();
        super.onDestroy();
    }
}
