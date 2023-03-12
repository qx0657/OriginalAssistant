package fun.qianxiao.originalassistant.utils;

import android.Manifest;
import android.content.Context;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.PermissionUtils;
import com.orhanobut.logger.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * TODO
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class PermissionManager {
    private Context context;
    private int hasReRequestTimes = 0;
    private final int MAX_TRY_REQUEST_TIMES = 3;

    public PermissionManager(Context context) {
        this.context = context;
    }

    private final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,

            //Manifest.permission.READ_PHONE_STATE,

            //Manifest.permission.ACCESS_FINE_LOCATION,
            //Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    public void requestNeeded() {
        PermissionUtils.permission(NEEDED_PERMISSIONS)
                .callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> granted) {
                        Logger.i("Permission onGranted\n", Arrays.toString(granted.toArray()));
                    }

                    @Override
                    public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                        Logger.e("Permission onDenied: \n" +
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

    private Context getContext() {
        return context;
    }

    public boolean hasAllPermission() {
        boolean ret = PermissionUtils.isGranted(NEEDED_PERMISSIONS);
        if (ret) {
            Logger.i("hasAllPermission");
        }
        return ret;
    }
}
