package ru.alexbykov.nopermission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 30.07.2017
 * Time: 18:39
 * Project: NoPermission
 *
 * @author Mike Antipiev @nindzyago
 * @author Alex Bykov @NoNews
 * @version 1.0.8
 */
public class PermissionHelper {


    private static final int PERMISSION_REQUEST_CODE = 1005001;
    private Activity activity;
    private String[] permissions;
    private Runnable successListener;
    private Runnable failureListener;
    private Runnable neverAskAgainListener;

    public PermissionHelper(Activity activity) {
        this.activity = activity;
    }


    /**
     * @param permission is single permission, which you want to ask
     */
    public PermissionHelper check(String permission) {
        this.permissions = new String[1];
        this.permissions[0] = permission;
        return this;
    }


    /**
     * @param permissions is array of permissions, which you want to ask
     */
    public PermissionHelper check(String... permissions) {
        this.permissions = permissions;
        return this;
    }


    /**
     * Setup failure callback
     *
     * @param listener called when user deny permission
     */
    public PermissionHelper onSuccess(Runnable listener) {
        this.successListener = listener;
        return this;
    }

    /**
     * Setup failure callback
     *
     * @param listener called when user deny permission
     */
    public PermissionHelper onFailure(Runnable listener) {
        this.failureListener = listener;
        return this;
    }


    /**
     * Setup never ask again callback
     *
     * @param listener called when permission in status "never ask again"
     */
    public PermissionHelper onNeverAskAgain(Runnable listener) {
        this.neverAskAgainListener = listener;
        return this;
    }


    /**
     * Check API-version and listeners
     *
     * @throws RuntimeException if one of the listeners null
     */
    public void run() {
        if (isListenersCorrect()) {
            if (isNeedToAskPermissions()) {
                checkPermissions();
            } else {
                successListener.run();
            }
        } else {
            throw new RuntimeException("OnPermissionSuccessListener or OnPermissionFailureListener or OnPermissionNewerAskAgainListener not implemented. Use methods: onSuccess, onFailure and onNewerAskAgain");
        }
    }


    /**
     * Request only those permissions that are not granted.
     * If all are granted, the  success  method is triggered
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        final String[] permissionsForRequest = getPermissionsForRequest();
        if (permissionsForRequest.length > 0) {
            activity.requestPermissions(permissionsForRequest, PERMISSION_REQUEST_CODE);
        } else {
            successListener.run();
        }
    }


    /**
     * Check listeners for null
     */
    private boolean isListenersCorrect() {
        return successListener != null && failureListener != null && neverAskAgainListener != null;
    }


    /**
     * We need to ask permission only if API >=23
     */
    private boolean isNeedToAskPermissions() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }


    /**
     * @return Array of permissions, that will be request
     */
    private String[] getPermissionsForRequest() {
        List<String> permissionsForRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (isPermissionNotGranted(permission)) {
                permissionsForRequest.add(permission);
            }
        }
        return permissionsForRequest.toArray(new String[permissionsForRequest.size()]);
    }


    /**
     * if permission not granted, check newerAskAgain, else call failure
     * if permission grander, call success
     *
     * @param grantResults Permissions, which granted
     * @param permissions  Permissions, which you asked
     * @param requestCode  requestCode of out request
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (String permission : permissions) {
                if (isNeedToAskPermissions()) {
                    if (isPermissionNotGranted(permission)) {
                        if (isNeverAskAgain(permission)) {
                            neverAskAgainListener.run();
                        } else {
                            failureListener.run();
                        }
                        return;
                    }
                }
            }
        }
        successListener.run();
    }


    /**
     * @param permission for check
     * @return true if permission granted and false if permission not granted
     */
    private boolean isPermissionNotGranted(String permission) {
        return ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED;
    }


    /**
     * Check neverAskAgain
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isNeverAskAgain(String permission) {
        return !activity.shouldShowRequestPermissionRationale(permission);
    }

    /**
     * To avoid memory leaks, you must change reference to null
     */
    public void unsubscribe() {
        activity = null;
        if (failureListener != null) {
            failureListener = null;
        }
        if (successListener != null) {
            successListener = null;
        }
        if (neverAskAgainListener != null) {
            neverAskAgainListener = null;
        }
    }
}
