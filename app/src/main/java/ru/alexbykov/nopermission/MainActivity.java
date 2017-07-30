package ru.alexbykov.nopermission;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ru.alexbykov.nopermission.PermissionHelper;

public class MainActivity extends AppCompatActivity {


    private PermissionHelper permissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupPermissions();
    }

    private void setupPermissions() {


        permissionHelper = new PermissionHelper(this);

        permissionHelper.check("test")
                .onSuccess(new PermissionHelper.OnPermissionSuccessListener() {
                    @Override
                    public void onSuccess() {
                        success();
                    }


                })
                .onFailure(new PermissionHelper.OnPermissionFailureListener() {
                    @Override
                    public void onFailure() {
                        error();
                    }
                })
                .run();
    }

    private void success() {

    }

    private void error() {

    }
}
