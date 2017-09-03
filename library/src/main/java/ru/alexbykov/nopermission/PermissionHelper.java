package ru.alexbykov.nopermission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

/**
 * Date: 30.07.2017
 * Time: 18:39
 * Project: NoPermission
 *
 * @author Mike Antipiev @nindzyago
 * @author Alex Bykov @NoNews
 */
public class PermissionHelper {

    private static final int PERMISSION_REQUEST_CODE = 1005001;
    private Activity activity;
    private String[] permissions;
    private OnPermissionSuccessListener successListener;
    private OnPermissionFailureListener failureListener;

    public PermissionHelper(Activity activity) {
        permissions = new String[1];
        this.activity = activity;
    }

    public PermissionHelper check(String permission) {
        this.permissions[0] = permission;
        return this;
    }

    public PermissionHelper check(String... permissions) {
        this.permissions = permissions;
        return this;
    }

    public PermissionHelper onSuccess(OnPermissionSuccessListener listener) {
        this.successListener = listener;
        return this;
    }

    public PermissionHelper onFailure(OnPermissionFailureListener listener) {
        this.failureListener = listener;
        return this;
    }

    public void run() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (successListener != null && failureListener != null) {
                activity.requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            } else
                throw new RuntimeException("OnPermissionSuccessListener or OnPermissionFailureListener not implemented. Use methods: onSuccess and onFailure");
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    failureListener.onFailure();
                    return;
                }
            }
            successListener.onSuccess();
        }
    }


    public void unsubscribe() {
        if (failureListener != null) {
            failureListener = null;
        }
        if (successListener != null) {
            successListener = null;
        }
    }

    public interface OnPermissionSuccessListener {
        void onSuccess();
    }

    public interface OnPermissionFailureListener {
        void onFailure();
    }
}
