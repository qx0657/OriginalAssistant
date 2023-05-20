package fun.qianxiao.originalassistant.activity;

import android.annotation.SuppressLint;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.VibrateUtils;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;

import fun.qianxiao.originalassistant.R;
import fun.qianxiao.originalassistant.activity.opensourcelicense.OpenSourceLicenseActivity;
import fun.qianxiao.originalassistant.base.BaseActivity;
import fun.qianxiao.originalassistant.config.AppConfig;
import fun.qianxiao.originalassistant.config.SPConstants;
import fun.qianxiao.originalassistant.databinding.ActivityAboutBinding;
import fun.qianxiao.originalassistant.manager.CheckUpdateManager;
import fun.qianxiao.originalassistant.utils.SysBrowserUtils;
import fun.qianxiao.originalassistant.utils.SysMailUtils;

/**
 * AboutActivity
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class AboutActivity extends BaseActivity<ActivityAboutBinding> {
    private final int OPEN_APP_PRO_CLICK_TIMES = 10;
    private int versionClickTimes;

    @Override
    protected void initListener() {
        binding.content.tvProjectGithub.setOnClickListener(v -> SysBrowserUtils.open(context, AppConfig.PROJECT_GITHUB_URL));
        binding.content.tvOpenSource.setOnClickListener(v -> ActivityUtils.startActivity(OpenSourceLicenseActivity.class));
        binding.content.tvPrivatePolicy.setOnClickListener(v -> BrowserActivity.load(context, "隐私政策", AppConfig.PRIVACY_POLICY_URL));
        binding.fab.setOnClickListener(view -> Snackbar.make(view, "给作者发送邮件？", Snackbar.LENGTH_LONG)
                .setAction("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SysMailUtils.send(context, "1540223760@qq.com", "【From 原创助手App】主题：");
                    }
                }).show());
        binding.content.tvVersion.setOnClickListener(v -> {
            versionClickTimes++;
            if (versionClickTimes >= OPEN_APP_PRO_CLICK_TIMES) {
                SPUtils.getInstance().put(SPConstants.KEY_SWITCH_APP_PRO, true);
                VibrateUtils.vibrate(50);
                ToastUtils.showShort("已开启隐藏功能");
            }
        });
    }

    @Override
    protected void initData() {
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        showBackIcon();
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());

        setAppVersion();
    }

    @SuppressLint("SetTextI18n")
    private void setAppVersion() {
        binding.content.tvVersion.setText("v." + AppUtils.getAppVersionName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.action_check_update) {
            CheckUpdateManager.getInstance().check(context, false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}