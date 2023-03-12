package fun.qianxiao.originalassistant.activity.selectapp;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.List;

import fun.qianxiao.originalassistant.activity.selectapp.adapter.AppInfoAdapter;
import fun.qianxiao.originalassistant.base.BaseActivity;
import fun.qianxiao.originalassistant.bean.AppInfo;
import fun.qianxiao.originalassistant.databinding.ActivitySelectAppBinding;
import fun.qianxiao.originalassistant.utils.AppListTool;
import fun.qianxiao.originalassistant.view.loading.ILoadingView;
import fun.qianxiao.originalassistant.view.loading.MyLoadingDialog;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * SelectAppActivity
 *
 * @Author QianXiao
 * @Date 2023/3/12
 */
public class SelectAppActivity extends BaseActivity<ActivitySelectAppBinding> implements ILoadingView {
    public static final int RESULT_CODE_SELECT_APP_NONE = -1;
    public static final int RESULT_CODE_SELECT_APP_OK = 0;
    public static final String KEY_APP_NAME = "KEY_APP_NAME";
    public static final String KEY_APP_PACKAGE_NAME = "KEY_APP_PACKAGE_NAME";

    private MyLoadingDialog loadingDialog;

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setTitle("选择游戏");
        binding.rvAppList.setLayoutManager(new LinearLayoutManager(context));
        getAppList();
    }

    private void getAppList() {
        final long startTime = TimeUtils.getNowMills();
        Observable.create((ObservableOnSubscribe<List<AppInfo>>) emitter -> {
            List<AppInfo> appInfoList = AppListTool.getAppList(context);
            emitter.onNext(appInfoList);
            emitter.onComplete();
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<AppInfo>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        openLoadingDialog("加载中");
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<AppInfo> s) {
                        if (!s.isEmpty()) {
                            final long endTime = TimeUtils.getNowMills();
                            long spendTIme = endTime - startTime;
                            LogUtils.i("getAppList size " + s.size() + " spend time: " + spendTIme + "ms");

                            showAppList(s);
                        } else {
                            ToastUtils.showShort("获取App列表为空");
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                        ToastUtils.showShort("获取App列表出错: " + e.toString());
                    }

                    @Override
                    public void onComplete() {
                        closeLoadingDialog();
                    }
                });
    }

    private void showAppList(List<AppInfo> appInfoList) {
        AppInfoAdapter adapter = new AppInfoAdapter(appInfoList);
        adapter.setItemClickListener(bean -> {
            Intent data = new Intent();
            data.putExtra(KEY_APP_NAME, bean.getAppName());
            data.putExtra(KEY_APP_PACKAGE_NAME, bean.getPackageName());
            setResult(RESULT_CODE_SELECT_APP_OK, data);
            finish();
        });
        binding.rvAppList.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_CODE_SELECT_APP_NONE);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_CODE_SELECT_APP_NONE);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void openLoadingDialog(String msg) {
        if (loadingDialog == null) {
            loadingDialog = new MyLoadingDialog(context);
        }
        if (!loadingDialog.isShowing()) {
            loadingDialog.setMessage(msg);
            loadingDialog.show();
        }
    }

    @Override
    public void closeLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}
