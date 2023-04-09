package fun.qianxiao.originalassistant.activity.test;

import static fun.qianxiao.originalassistant.config.AppConfig.HULUXIA_APP_PACKAGE_NAME;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.viewbinding.ViewBinding;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.IntentUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.List;

import fun.qianxiao.originalassistant.activity.selectapp.SelectAppActivity;
import fun.qianxiao.originalassistant.base.BaseActivity;
import fun.qianxiao.originalassistant.bean.AppInfo;

/**
 * BaseTestActivity
 *
 * @Author QianXiao
 * @Date 2023/3/17
 */
public abstract class BaseTestActivity<DB extends ViewBinding> extends BaseActivity<DB> {
    private ActivityResultLauncher<Intent> activityResultLauncher;

    protected final static int SMS_PERMISSION_HAS_REMOVED = 1;
    protected final static int SMS_PERMISSION_HAS_NOT_REMOVED = 0;
    protected final static int SMS_PERMISSION_CHECK_FAILED = -1;

    protected final static String SIM_CMCC_TEXT = "移动";
    protected final static String SIM_CUCC_TEXT = "联通";
    protected final static String SIM_CTCC_TEXT = "电信";

    protected final static int SIM_CMCC = 0;
    protected final static int SIM_CUCC = 1;
    protected final static int SIM_CTCC = 2;
    protected final static int SIM_NONE = 3;

    @Override
    protected void initData() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setTitle(getTestTitle());

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int resultCode = result.getResultCode();
                Intent data = result.getData();
                if (resultCode == SelectAppActivity.RESULT_CODE_SELECT_APP_OK) {
                    if (data != null) {
                        try {
                            AppInfo appInfo = new AppInfo();
                            String appName = data.getStringExtra(SelectAppActivity.KEY_APP_NAME);
                            String appPackageName = data.getStringExtra(SelectAppActivity.KEY_APP_PACKAGE_NAME);
                            appInfo.setAppName(appName);
                            appInfo.setPackageName(appPackageName);
                            PackageManager packageManager = getPackageManager();
                            PackageInfo packageInfo = packageManager.getPackageInfo(appPackageName, 0);
                            appInfo.setVersionName(packageInfo.versionName);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                appInfo.setVersionCode(packageInfo.getLongVersionCode());
                            } else {
                                appInfo.setVersionCode(packageInfo.versionCode);
                            }
                            onSelectApp(appInfo);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    /**
     * onSelectApp
     *
     * @param appInfo {@link AppInfo}
     */
    protected abstract void onSelectApp(AppInfo appInfo);

    /**
     * getTestTitle
     *
     * @return title
     */
    protected abstract CharSequence getTestTitle();

    protected void selectApp() {
        activityResultLauncher.launch(new Intent(this, SelectAppActivity.class));
    }

    protected void gotoApp() {
        String appPackageNameInstalled = null;
        for (String s : HULUXIA_APP_PACKAGE_NAME) {
            if (AppUtils.isAppInstalled(s)) {
                appPackageNameInstalled = s;
                break;
            }
        }
        if (!TextUtils.isEmpty(appPackageNameInstalled)) {
            startActivity(IntentUtils.getLaunchAppIntent(appPackageNameInstalled));
        } else {
            ToastUtils.showShort("没有安装3楼");
        }
    }

    protected int isRemoveSmsPermission(String packetName) {
        if (TextUtils.isEmpty(packetName)) {
            return SMS_PERMISSION_CHECK_FAILED;
        }
        List<String> permissions = PermissionUtils.getPermissions(packetName);
        if (permissions == null || permissions.size() == 0) {
            return SMS_PERMISSION_CHECK_FAILED;
        }
        for (String permission : permissions) {
            if (permission.equals(Manifest.permission.SEND_SMS)) {
                return SMS_PERMISSION_HAS_NOT_REMOVED;
            }
        }
        return SMS_PERMISSION_HAS_REMOVED;
    }

    protected String getTextBySmsPermissionRemoveResult(int result) {
        if (result == SMS_PERMISSION_HAS_REMOVED) {
            return "已去除发送短信权限";
        } else if (result == SMS_PERMISSION_HAS_NOT_REMOVED) {
            return "未去除发送短信权限";
        } else {
            return "发送短信权限检查失败";
        }
    }

    protected int getSimChannelIndex() {
        String simOperatorName = PhoneUtils.getSimOperatorByMnc();
        if (TextUtils.isEmpty(simOperatorName)) {
            return 0;
        }
        if (simOperatorName.contains(SIM_CMCC_TEXT)) {
            return SIM_CMCC;
        } else if (simOperatorName.contains(SIM_CUCC_TEXT)) {
            return SIM_CUCC;
        } else if (simOperatorName.contains(SIM_CTCC_TEXT)) {
            return SIM_CTCC;
        }
        return SIM_NONE;
    }
}
