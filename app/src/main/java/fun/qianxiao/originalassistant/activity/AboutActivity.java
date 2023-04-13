package fun.qianxiao.originalassistant.activity;

import android.annotation.SuppressLint;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import fun.qianxiao.originalassistant.R;
import fun.qianxiao.originalassistant.activity.opensourcelicense.OpenSourceLicenseActivity;
import fun.qianxiao.originalassistant.base.BaseActivity;
import fun.qianxiao.originalassistant.checkupdate.CheckUpdateManager;
import fun.qianxiao.originalassistant.config.AppConfig;
import fun.qianxiao.originalassistant.databinding.ActivityAboutBinding;
import fun.qianxiao.originalassistant.utils.SysBrowserUtils;
import fun.qianxiao.originalassistant.utils.SysMailUtils;

/**
 * AboutActivity
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class AboutActivity extends BaseActivity<ActivityAboutBinding> {

    @Override
    protected void initListener() {
        binding.content.tvProjectGithub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SysBrowserUtils.open(context, AppConfig.PROJECT_GITHUB_URL);
            }
        });
        binding.content.tvOpenSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startActivity(OpenSourceLicenseActivity.class);
            }
        });
        binding.content.tvPrivatePolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrowserActivity.load(context, "隐私政策", AppConfig.PRIVACY_POLICY_URL);
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

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "给作者发送邮件？", Snackbar.LENGTH_LONG)
                        .setAction("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SysMailUtils.send(context, "1540223760@qq.com", "【From 原创助手App】主题：");
                            }
                        }).show();
            }
        });
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
            new CheckUpdateManager(context).check(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}