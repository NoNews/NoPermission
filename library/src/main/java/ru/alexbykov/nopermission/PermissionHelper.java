package ru.alexbykov.nopermission;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.ColorRes;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 30.07.2017
 * Time: 18:39
 * Project: NoPermission
 *
 * @author Mike Antipiev @nindzyago
 * @author Alex Bykov @NoNews
 * @version 1.1.2
 */
public class PermissionHelper {


    private static final int PERMISSION_REQUEST_CODE = 98;
    private Activity activity;
    private Fragment fragment;
    private String[] permissions;
    private Runnable successListener;
    private Runnable deniedListener;
    private Runnable neverAskAgainListener;
    private AlertDialog.Builder dialogBeforeRunBuilder;
    private int dialogBeforeAskPositiveButton;
    private int dialogBeforeAskPositiveButtonColor = DIALOG_WITHOUT_CUSTOM_COLOR;
    private final static int DIALOG_WITHOUT_CUSTOM_COLOR = 0;


    /**
     * Default activity constructor
     *
     * @param activity is activity instance. Use it only in activities. Don't use in fragments!
     */
    public PermissionHelper(Activity activity) {
        this.activity = activity;
    }


    /**
     * Default fragment constructor
     *
     * @param fragment is fragment instance. Use it only in fragments
     */
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
     * Setup success callback
     *
     * @param listener called when user deny permission
     * @return current object
     */
    public PermissionHelper onSuccess(Runnable listener) {
        this.successListener = listener;
        return this;
    }

    /**
     * Setup denied callback
     *
     * @param listener called when user deny permission
     * @return current object
     */
    public PermissionHelper onDenied(Runnable listener) {
        this.deniedListener = listener;
        return this;
    }

    /**
     * @deprecated replaced by  {@link #onDenied(Runnable)} ()
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
     * This method setup custom dialog before permissions will be asked.
     * Dialog will be shown only if permissions not granted.
     *
     * @param titleRes          dialog title string resource
     * @param messageRes        dialog message string resource
     * @param positiveButtonRes dialog positive button string resource
     * @return current object
     */

    public PermissionHelper withDialogBeforeRun(@StringRes int titleRes,
                                                @StringRes int messageRes,
                                                @StringRes int positiveButtonRes) {

        this.dialogBeforeAskPositiveButton = positiveButtonRes;
        dialogBeforeRunBuilder = getDialogBuilder(titleRes, messageRes);
        return this;
    }


    /**
     * This method setup custom dialog positive button color
     *
     * @param colorRes dialog positive button string resource
     * @return current object
     */
    public PermissionHelper setDialogPositiveButtonColor(@ColorRes int colorRes) {
        this.dialogBeforeAskPositiveButtonColor = ContextCompat.getColor(getContext(), colorRes);
        return this;
    }


    /**
     * This method return dialog builder with default settings.
     * It is created for the future customization
     *
     * @param titleRes   dialog title string resource
     * @param messageRes dialog message string resource
     * @return new dialog builder object
     */
    private AlertDialog.Builder getDialogBuilder(@StringRes int titleRes, @StringRes int messageRes) {
        final Context context = getContext();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle(context.getString(titleRes));
        dialogBuilder.setMessage(context.getString(messageRes));
        dialogBuilder.setCancelable(false);
        return dialogBuilder;
    }

    /**
     * This method return context, depending on what you use: activity or fragment
     *
     * @return context
     */
    private Context getContext() {
        return activity == null ? fragment.getContext() : activity;
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
     * and run method c{@link #checkPermissions()}, if {@link #isNeedToAskPermissions()} return false
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
     * otherwise {@link #checkDialogAndAskPermissions(String[])} will called
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        final String[] permissionsForRequest = getPermissionsForRequest();
        if (permissionsForRequest.length > 0) {
            checkDialogAndAskPermissions(permissionsForRequest);
        } else {
            successListener.run();
        }
    }

    /**
     * This method check your dialog
     * If you set it, {@link #withDialogBeforeRun}, that dialog will be show before system permission dialog
     * otherwise {@link #askPermissions(String[])} will be called
     * Note, custom dialog will show only if permissions not granted.
     *
     * @param permissionsForRequest = permissions, when currently not granted and will be asked
     */
    @SuppressLint("NewApi")
    private void checkDialogAndAskPermissions(final String[] permissionsForRequest) {
        if (dialogBeforeRunBuilder != null && isNotContainsNeverAskAgain(permissionsForRequest)) {
            showDialogBeforeRun(permissionsForRequest);
        } else {
            askPermissions(permissionsForRequest);
        }
    }

    /**
     * This method check permissions for never again.
     *
     * @param permissionsForRequest = permissions, when currently not granted and will be asked
     * @return false if one of them never ask gain
     */
    private boolean isNotContainsNeverAskAgain(String[] permissionsForRequest) {
        for (String permissions : permissionsForRequest) {
            if (isNeverAskAgain(permissions)) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method set positive button and custom color to your dialog
     * method {@link #askPermissions(String[])} called when positive button clicked
     *
     * @param permissionsForRequest = permissions, when currently not granted and will be asked
     */
    private void showDialogBeforeRun(final String[] permissionsForRequest) {
        dialogBeforeRunBuilder.setPositiveButton(dialogBeforeAskPositiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                askPermissions(permissionsForRequest);
            }
        });
        final AlertDialog dialogBeforeRun = dialogBeforeRunBuilder.create();

        dialogBeforeRun.show();
        if (dialogBeforeAskPositiveButtonColor != DIALOG_WITHOUT_CUSTOM_COLOR) {
            dialogBeforeRun.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(dialogBeforeAskPositiveButtonColor);
        }
    }

    /**
     * This method ask permission
     *
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
    @SuppressLint("NewApi")
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (String permission : permissions) {
                if (isPermissionNotGranted(permission)) {
                    runDeniedOrNeverAskAgain(permission);
                    return;
                }
            }
        }
        successListener.run();
        unbind();
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
        unbind();
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
    @SuppressLint("NewApi")
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
        final Context context = getContext();
        final Intent intent = new Intent();
        final Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(uri);
        context.startActivity(intent);
    }

    /**
     * @deprecated no necessary to use begin the version 1.1.2, it's work automatically
     * after each permissions request.
     * Don't forget setup callbacks again.
     */
    @Deprecated
    public void unsubscribe() {
        unbind();
    }

    /**
     * This method change listeners reference to avoid memory leaks
     * Don't forget setup callbacks and your settings again!
     */
    private void unbind() {
        deniedListener = null;
        successListener = null;
        if (dialogBeforeRunBuilder != null) {
            dialogBeforeRunBuilder = null;
            dialogBeforeAskPositiveButton = DIALOG_WITHOUT_CUSTOM_COLOR;
        }
        if (neverAskAgainListener != null) {
            neverAskAgainListener = null;
        }
    }
}
