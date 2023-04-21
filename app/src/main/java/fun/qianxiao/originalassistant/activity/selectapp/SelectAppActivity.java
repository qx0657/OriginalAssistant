package fun.qianxiao.originalassistant.activity.selectapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Build;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.AttachListPopupView;
import com.lxj.xpopup.interfaces.OnSelectListener;

import java.io.File;
import java.util.List;

import fun.qianxiao.originalassistant.R;
import fun.qianxiao.originalassistant.activity.selectapp.adapter.AppInfoAdapter;
import fun.qianxiao.originalassistant.base.BaseActivity;
import fun.qianxiao.originalassistant.base.BaseAdapter;
import fun.qianxiao.originalassistant.bean.AppInfo;
import fun.qianxiao.originalassistant.config.AppConfig;
import fun.qianxiao.originalassistant.config.SPConstants;
import fun.qianxiao.originalassistant.databinding.ActivitySelectAppBinding;
import fun.qianxiao.originalassistant.manager.PermissionManager;
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

    private float touchX;
    private float touchY;

    private final BaseAdapter.ItemClickListener<AppInfo> itemClickListener = (v, pos, bean) -> {
        Intent data = new Intent();
        data.putExtra(KEY_APP_NAME, bean.getAppName());
        data.putExtra(KEY_APP_PACKAGE_NAME, bean.getPackageName());
        SelectAppActivity.this.setResult(RESULT_CODE_SELECT_APP_OK, data);
        SelectAppActivity.this.finish();
    };

    private final BaseAdapter.ItemLongClickListener<AppInfo> itemLongClickListener = (v, pos, bean) -> {
        v.getParent().requestDisallowInterceptTouchEvent(true);
        AttachListPopupView attachListPopupView = new XPopup.Builder(context)
                .asAttachList(new String[]{bean.getAppName().toString(), "提取安装包"}, null,
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                if (position == 1) {
                                    exportApk(bean);
                                }
                            }
                        });
        attachListPopupView.popupInfo.touchPoint = new PointF(touchX, touchY);
        attachListPopupView.show();
        return true;
    };

    private void exportApkInner(AppInfo bean) {
        String appPath = AppUtils.getAppPath(bean.getPackageName());
        String storePath = SPUtils.getInstance().getString(SPConstants.KEY_APK_EXPORT_DIR, AppConfig.DEFAULT_APK_EXPORT_DIR);
        if (!storePath.endsWith(File.separator)) {
            storePath = storePath + File.separator;
        }
        String outPath = storePath + bean.getPackageName() + "_" + bean.getVersionName() + "_" + bean.getVersionCode() + ".apk";
        LogUtils.i(appPath, outPath);
        if (FileUtils.copy(appPath, outPath)) {
            ToastUtils.showShort("Apk导出成功: " + outPath);
        } else {
            LogUtils.e("Apk导出失败");
            ToastUtils.showShort("Apk导出失败");
        }
    }

    private void exportApk(AppInfo bean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                exportApkInner(bean);
            } else {
                ToastUtils.showShort("请给予" + AppUtils.getAppName() + "所有文件权限");
                PermissionManager.getInstance().requestManageExternalStoragePermission(context);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                exportApkInner(bean);
            } else {
                ToastUtils.showShort("请给予" + AppUtils.getAppName() + "文件读写权限");
                PermissionManager.getInstance().requestReadWritePermission();
            }
        }
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        setTitle("选择游戏");
        showBackIcon();
        binding.rvAppList.setLayoutManager(new LinearLayoutManager(context));
        getAppList();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN || ev.getAction() == MotionEvent.ACTION_MOVE) {
            touchX = ev.getRawX();
            touchY = ev.getRawY();
        }
        return super.dispatchTouchEvent(ev);
    }

    private void initRecycleViewAnim() {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.app_info_item_anim);
        LayoutAnimationController layoutAnimationController = new LayoutAnimationController(animation);
        layoutAnimationController.setOrder(LayoutAnimationController.ORDER_NORMAL);
        layoutAnimationController.setDelay(0.1f);
        binding.rvAppList.setLayoutAnimation(layoutAnimationController);
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
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<AppInfo> appInfoList) {
                        if (!appInfoList.isEmpty()) {
                            final long endTime = TimeUtils.getNowMills();
                            long spendTime = endTime - startTime;
                            LogUtils.i("getAppList size " + appInfoList.size() + " spend time: " + spendTime + "ms");

                            showAppList(appInfoList);
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
        AppInfoAdapter adapter = new AppInfoAdapter(context, appInfoList);
        adapter.setItemClickListener(itemClickListener);
        adapter.setItemLongClickListener(itemLongClickListener);
        binding.rvAppList.setAdapter(adapter);
        initRecycleViewAnim();
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
