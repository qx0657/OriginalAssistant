package fun.qianxiao.originalassistant.activity.test;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.List;

import fun.qianxiao.originalassistant.R;
import fun.qianxiao.originalassistant.activity.selectapp.SelectAppActivity;
import fun.qianxiao.originalassistant.base.BaseActivity;
import fun.qianxiao.originalassistant.bean.AppInfo;
import fun.qianxiao.originalassistant.config.Constants;
import fun.qianxiao.originalassistant.config.SPConstants;
import fun.qianxiao.originalassistant.utils.HLXUtils;

/**
 * BaseTestActivity
 *
 * @Author QianXiao
 * @Date 2023/3/17
 */
public abstract class BaseTestActivity<DB extends ViewBinding> extends BaseActivity<DB> {
    private ActivityResultLauncher<Intent> activityResultLauncher;
    protected FloatingActionsMenu famTest;

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
    protected void initListener() {
        famTest = binding.getRoot().findViewById(R.id.fam_test);
        setFloatingActionButtonListener();

        KeyboardUtils.registerSoftInputChangedListener(this, height -> {
            if (height != 0) {
                famTest.collapse();
                famTest.setVisibility(View.GONE);
            } else {
                ThreadUtils.runOnUiThreadDelayed(() -> famTest.setVisibility(View.VISIBLE), 50);
            }
        });
    }

    private void setFloatingActionButtonListener() {
        binding.getRoot().findViewById(R.id.fab_select_app).setOnClickListener(view -> {
            famTest.collapse();
            ThreadUtils.runOnUiThreadDelayed(this::selectApp, 100);
        });
        binding.getRoot().findViewById(R.id.fab_clean_content).setOnClickListener(view -> {
            famTest.collapse();
            cleanAllInputContent();
        });
        binding.getRoot().findViewById(R.id.fab_copy_content).setOnClickListener(view -> {
            famTest.collapse();
            copyContent();
        });
        binding.getRoot().findViewById(R.id.fab_goto_app).setOnClickListener(view -> {
            famTest.collapse();
            gotoApp();
        });
    }

    /**
     * cleanAllInputContent
     */
    protected abstract void cleanAllInputContent();

    /**
     * copyContent
     */
    protected abstract void copyContent();

    @Override
    protected void initData() {
        setTitle(getTestTitle());
        showBackIcon();
        initActivityResultLauncher();
        setAppMode(SPUtils.getInstance().getInt(SPConstants.KEY_TEST_APP_MODE, Constants.APP_MODE_GAME));
    }

    private void initActivityResultLauncher() {
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
        HLXUtils.gotoHLX();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_test, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItemAppModeGame = menu.findItem(R.id.menu_item_app_mode_game);
        MenuItem menuItemAppModeSoftware = menu.findItem(R.id.menu_item_app_mode_software);
        int appMode = SPUtils.getInstance().getInt(SPConstants.KEY_TEST_APP_MODE, Constants.APP_MODE_GAME);
        if (appMode == Constants.APP_MODE_GAME) {
            menuItemAppModeGame.setChecked(true);
        } else {
            menuItemAppModeSoftware.setChecked(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_item_app_mode_game) {
            SPUtils.getInstance().put(SPConstants.KEY_TEST_APP_MODE, Constants.APP_MODE_GAME);
            item.setChecked(true);
            setAppMode(Constants.APP_MODE_GAME);
            return true;
        } else if (item.getItemId() == R.id.menu_item_app_mode_software) {
            SPUtils.getInstance().put(SPConstants.KEY_TEST_APP_MODE, Constants.APP_MODE_SOFTWARE);
            item.setChecked(true);
            setAppMode(Constants.APP_MODE_SOFTWARE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * setAppMode
     *
     * @param mode mode {@link Constants#APP_MODE_GAME} or {@link Constants#APP_MODE_SOFTWARE}
     */
    protected abstract void setAppMode(int mode);

    @Override
    public void onDestroy() {
        KeyboardUtils.unregisterSoftInputChangedListener(getWindow());
        super.onDestroy();
    }
}
