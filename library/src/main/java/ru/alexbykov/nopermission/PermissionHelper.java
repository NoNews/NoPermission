package ru.alexbykov.nopermission;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 30.07.2017
 * Time: 18:39
 * Project: NoPermission
 *
 * @author Mike Antipiev @nindzyago
 * @author Alex Bykov @NoNews
 * @version 1.1.1
 */
public class PermissionHelper {


    private static final int PERMISSION_REQUEST_CODE = 98;
    private Activity activity;
    private Fragment fragment;
    private String[] permissions;
    private Runnable successListener;
    private Runnable deniedListener;
    private Runnable neverAskAgainListener;

    public PermissionHelper(Activity activity) {
        this.activity = activity;
    }

    public PermissionHelper(Fragment fragment) {
        this.fragment = fragment;
    }


    /**
     * @param permission is single permission, which you want to ask
     * @return current object
     */
    public PermissionHelper check(String permission) {
        this.permissions = new String[1];
        this.permissions[0] = permission;
        return this;
    }


    /**
     * @param permissions is array of permissions, which you want to ask
     * @return current object
     */
    public PermissionHelper check(String... permissions) {
        this.permissions = permissions;
        return this;
    }


    /**
     * Setup failure callback
     *
     * @param listener called when user deny permission
     * @return current object
     */
    public PermissionHelper onSuccess(Runnable listener) {
        this.successListener = listener;
        return this;
    }

    /**
     * Setup failure callback
     *
     * @param listener called when user deny permission
     * @return current object
     */
    public PermissionHelper onDenied(Runnable listener) {
        this.deniedListener = listener;
        return this;
    }

    /**
     * @deprecated use method onFailure instead that
     *
     */
    @Deprecated
    public PermissionHelper onFailure(Runnable listener) {
        this.deniedListener = listener;
        return this;
    }

    /**
     * This method setup never ask again callback
     *
     * @param listener called when permission in status "never ask again"
     * @return current object
     */
    public PermissionHelper onNeverAskAgain(Runnable listener) {
        this.neverAskAgainListener = listener;
        return this;
    }


    /**
     * This method check API-version and listeners
     *
     * @throws RuntimeException if isListenersCorrect return false
     */
    public void run() {
        if (isListenersCorrect()) {
            runSuccessOrAskPermissions();
        } else {
            throw new RuntimeException("permissionSuccessListener or permissionDeniedListener have null reference. You must realize onSuccess and onDenied methods");
        }
    }


    /**
     * This method run successListener if all permissions granted,
     * and run method checkPermissions, if needToAskPermissions return false
     */
    private void runSuccessOrAskPermissions() {
        if (isNeedToAskPermissions()) {
            checkPermissions();
        } else {
            successListener.run();
        }
    }


    /**
     * This method request only those permissions that are not granted.
     * If all are granted, success callback called
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        final String[] permissionsForRequest = getPermissionsForRequest();
        if (permissionsForRequest.length > 0) {
            askPermissions(permissionsForRequest);
        } else {
            successListener.run();
        }
    }


    /**
     * This method ask permission
     * @param permissionsForRequest array of permissions which you want to ask
     */

    @SuppressLint("NewApi")
    private void askPermissions(String[] permissionsForRequest) {
        if (activity != null) {
            activity.requestPermissions(permissionsForRequest, PERMISSION_REQUEST_CODE);
        } else {
            fragment.requestPermissions(permissionsForRequest, PERMISSION_REQUEST_CODE);
        }
    }


    /**
     * This method check listeners for null
     *
     * @return true if you realized method onSuccess and onDenied
     */
    private boolean isListenersCorrect() {
        return successListener != null && deniedListener != null;
    }


    /**
     * This method ckeck api version
     *
     * @return true if API >=23
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
     * if permission not granted, check neverAskAgain, else call failure
     * if permission grander, call success
     *
     * @param grantResults Permissions, which granted
     * @param permissions  Permissions, which you asked
     * @param requestCode  requestCode of out request
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && isNeedToAskPermissions()) {
            for (String permission : permissions) {
                if (isPermissionNotGranted(permission)) {
                    runDeniedOrNeverAskAgain(permission);
                    return;
                }
            }
        }
        successListener.run();
    }

    /**
     * This method run denied or neverAskAgain callbacks
     *
     * @param permission Permissions, which granted
     */

    @SuppressLint("NewApi")
    private void runDeniedOrNeverAskAgain(String permission) {
        if (isNeverAskAgain(permission)) {
            runNeverAskAgain();
        } else {
            deniedListener.run();
        }
    }


    /**
     * This method run neverAskAgain callback if neverAskAgainListener not null
     */
    private void runNeverAskAgain() {
        if (neverAskAgainListener != null) {
            neverAskAgainListener.run();
        }
    }


    /**
     * @param permission for check
     * @return true if permission granted and false if permission not granted
     */
    private boolean isPermissionNotGranted(String permission) {
        if (activity != null) {
            return ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED;
        } else {
            return ActivityCompat.checkSelfPermission(fragment.getContext(), permission) != PackageManager.PERMISSION_GRANTED;
        }
    }


    /**
     * @param permission for check neverAskAgain
     * @return true if user checked "Never Ask Again"
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isNeverAskAgain(String permission) {
        if (activity != null) {
            return !activity.shouldShowRequestPermissionRationale(permission);
        } else {
            return !fragment.shouldShowRequestPermissionRationale(permission);
        }
    }


    /**
     * This method start application settings activity
     * Note: is not possible to open at once screen with application permissions.
     */
    public void startApplicationSettingsActivity() {
        final Context context = activity == null ? fragment.getContext() : activity;
        final Intent intent = new Intent();
        final Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(uri);
        context.startActivity(intent);
    }

    /**
     * This method change listeners reference to avoid memory leaks
     */
    public void unsubscribe() {
        deniedListener = null;
        successListener = null;

        if (activity != null) {
            activity = null;
        }
        if (fragment != null) {
            fragment = null;
        }
        if (neverAskAgainListener != null) {
            neverAskAgainListener = null;
        }
    }
}
