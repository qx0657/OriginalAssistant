package fun.qianxiao.originalassistant.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ThreadUtils;

import fun.qianxiao.originalassistant.MainActivity;
import fun.qianxiao.originalassistant.R;
import fun.qianxiao.originalassistant.base.BaseActivity;
import fun.qianxiao.originalassistant.databinding.ActivitySplashBinding;

/**
 * SplashActivity
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class SplashActivity extends BaseActivity<ActivitySplashBinding> {
    private static final long DELAY_TIMES_MILLIS = 10;

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        BarUtils.transparentStatusBar(this);
        ThreadUtils.runOnUiThreadDelayed(this::enterApp, DELAY_TIMES_MILLIS);
    }

    private void enterApp() {
        startActivity(new Intent(context, MainActivity.class));
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    /**
     * 禁用返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
