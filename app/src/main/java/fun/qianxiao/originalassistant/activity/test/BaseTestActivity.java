package fun.qianxiao.originalassistant.activity.test;

import androidx.appcompat.app.ActionBar;
import androidx.viewbinding.ViewBinding;

import fun.qianxiao.originalassistant.base.BaseActivity;

/**
 * BaseTestActivity
 *
 * @Author QianXiao
 * @Date 2023/3/17
 */
public abstract class BaseTestActivity<DB extends ViewBinding> extends BaseActivity<DB> {
    @Override
    protected void initData() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setTitle(getTestTitle());
    }

    protected abstract CharSequence getTestTitle();
}
