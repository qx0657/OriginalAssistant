package fun.qianxiao.originalassistant.manager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * PermissionManager
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class PermissionManager {
    private int hasReRequestTimes = 0;
    private final int MAX_TRY_REQUEST_TIMES = 1;

    private static PermissionManager instance;

    private final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,

            //Manifest.permission.READ_PHONE_STATE,

            //Manifest.permission.ACCESS_FINE_LOCATION,
            //Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    private PermissionManager() {

    }

    public static PermissionManager getInstance() {
        if (instance == null) {
            synchronized (PermissionManager.class) {
                if (instance == null) {
                    instance = new PermissionManager();
                }
            }
        }
        return instance;
    }

    public void requestReadWritePermission() {
        PermissionUtils.permission(new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        }).request();
    }

    public boolean hasRequestReadWritePermission() {
        return PermissionUtils.isGranted(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        );
    }

    public void requestManageExternalStoragePermission(Context context) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

    public void requestNeeded() {
        hasReRequestTimes = 0;
        PermissionUtils.permission(NEEDED_PERMISSIONS)
                .callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> granted) {
                        LogUtils.i("Permission onGranted\n", Arrays.toString(granted.toArray()));
                    }

                    @Override
                    public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                        LogUtils.e("Permission onDenied: \n" +
                                "denied:\n" +
                                "%s", Arrays.toString(denied.toArray()));
                        hasReRequestTimes++;
                        if (hasReRequestTimes < MAX_TRY_REQUEST_TIMES) {
                            requestNeeded();
                        }
                    }
                })
                .rationale((activity, shouldRequest) -> shouldRequest.again(true))
                .request();
    }

    public boolean hasAllPermission() {
        boolean ret = PermissionUtils.isGranted(NEEDED_PERMISSIONS);
        if (ret) {
            LogUtils.i("hasAllPermission");
        }
        return ret;
    }
}
