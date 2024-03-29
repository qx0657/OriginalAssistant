package fun.qianxiao.originalassistant.manager;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnDownloadListener;
import com.hjq.http.model.HttpMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import fun.qianxiao.originalassistant.R;
import fun.qianxiao.originalassistant.api.CheckUpdateApi;
import fun.qianxiao.originalassistant.manager.net.ApiServiceManager;
import fun.qianxiao.originalassistant.utils.SysBrowserUtils;
import fun.qianxiao.originalassistant.view.loading.MyLoadingDialog;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;


/**
 * CheckUpdateManager
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public final class CheckUpdateManager {
    private static CheckUpdateManager instance;

    private MyLoadingDialog loadingDialog;

    private boolean isBrowserDownload = false;
    private boolean isUpdating = false;
    private boolean isApkFullyDownloaded = false;

    private CheckUpdateManager() {
    }

    public static CheckUpdateManager getInstance() {
        if (instance == null) {
            synchronized (CheckUpdateManager.class) {
                if (instance == null) {
                    instance = new CheckUpdateManager();
                }
            }
        }
        return instance;
    }

    /**
     * Check for updates
     *
     * @param context  Context
     * @param isSilent Is it silent
     */
    public void check(Context context, boolean isSilent) {
        if (!isSilent) {
            openLoadingDialog(context, "正在检查更新");
        }
        ApiServiceManager.getInstance()
                .create(CheckUpdateApi.class)
                .getUpdateConfig()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ResponseBody responseBody) {
                        try {
                            String data = responseBody.string();
                            JSONObject jsonObject = new JSONObject(data);
                            if (jsonObject.getInt("newversioncode") > context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode) {
                                //发现新版本
                                String newversionname = jsonObject.optString("newversionname");
                                String newapkmd5 = jsonObject.optString("newapkmd5");
                                String downloadurl = jsonObject.optString("downloadurl");
                                boolean isforceupdate = jsonObject.getInt("isforceupdate") == 1;

                                File mApkFile = new File(PathUtils.getExternalAppDownloadPath(), AppUtils.getAppName() + ".v." + newversionname + ".apk");
                                if (FileUtils.isFileExists(mApkFile) && FileUtils.getFileMD5ToString(mApkFile).equalsIgnoreCase(newapkmd5)) {
                                    isApkFullyDownloaded = true;
                                }
                                if (isBrowserDownload) {
                                    isApkFullyDownloaded = false;
                                }
                                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.LightAlertDialog)
                                        .setTitle("发现新版本(V." + newversionname + ")")
                                        .setMessage(jsonObject.optString("updatacontent"))
                                        .setCancelable(false)
                                        //点击事件在下面设置 防止点击后dialog消失
                                        .setPositiveButton(isApkFullyDownloaded ? "立即安装" : "立即更新", null);
                                if (!isforceupdate) {
                                    builder = builder.setNegativeButton(isApkFullyDownloaded ? "暂不安装" : "暂不更新", null);
                                }
                                if (isBrowserDownload) {
                                    builder = builder.setNeutralButton("复制下载链接", null);
                                }
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                reflectChangeTitleColor(dialog);
                                Button neutralBtn = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                                neutralBtn.setOnClickListener(v -> {
                                    ClipboardUtils.copyText(downloadurl);
                                    ToastUtils.showShort("下载链接已复制至剪贴板");
                                });
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (isBrowserDownload) {
                                            //从其他浏览器打开
                                            SysBrowserUtils.open(context, downloadurl);
                                        } else {
                                            if (isApkFullyDownloaded) {
                                                //立即安装
                                                AppUtils.installApp(mApkFile);
                                                return;
                                            }
                                            /*if (!PermissionUtils.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                                PermissionUtils.permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                        .callback(new PermissionUtils.SimpleCallback() {
                                                            @Override
                                                            public void onGranted() {
                                                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
                                                            }

                                                            @Override
                                                            public void onDenied() {
                                                                onClick(v);
                                                            }
                                                        })
                                                        .request();
                                                return;
                                            }*/
                                            if (!isUpdating) {
                                                isUpdating = true;
                                            } else {
                                                return;
                                            }
                                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
                                            EasyHttp.download((AppCompatActivity) context)
                                                    .method(HttpMethod.GET)
                                                    .file(mApkFile)
                                                    .url(downloadurl)
                                                    .md5(newapkmd5)
                                                    .listener(new OnDownloadListener() {
                                                        @Override
                                                        public void onStart(File file) {

                                                        }

                                                        @Override
                                                        public void onProgress(File file, int progress) {
                                                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setText(progress + "%");
                                                        }

                                                        @Override
                                                        public void onComplete(File file) {
                                                            if (!isforceupdate) {
                                                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
                                                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setText("暂不安装");
                                                            }
                                                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setText("立即安装");
                                                            isUpdating = false;
                                                            isApkFullyDownloaded = true;
                                                            AppUtils.installApp(file);
                                                        }

                                                        @Override
                                                        public void onError(File file, Exception e) {
                                                            ToastUtils.showShort(e.toString());
                                                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
                                                            isUpdating = false;
                                                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setText("立即更新");
                                                        }

                                                        @Override
                                                        public void onEnd(File file) {

                                                        }
                                                    })
                                                    .start();
                                        }
                                    }
                                });
                            } else {
                                if (!isSilent) {
                                    ToastUtils.showShort("当前已是最新版本");
                                }
                            }
                        } catch (IOException | JSONException | PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                            onError(e);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (!isSilent) {
                            closeLoadingDialog();
                            ToastUtils.showShort("检查更新出错:" + e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (!isSilent) {
                            closeLoadingDialog();
                        }
                    }
                });
    }

    private void reflectChangeTitleColor(AlertDialog dialog) {
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object alertController = mAlert.get(dialog);

            Field mTitleView = alertController.getClass().getDeclaredField("mTitleView");
            mTitleView.setAccessible(true);

            TextView title = (TextView) mTitleView.get(alertController);
            if (title != null) {
                title.setTextColor(Color.BLACK);
                title.setAlpha(0.75f);
            }
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void openLoadingDialog(Context context, String msg) {
        if (loadingDialog == null) {
            loadingDialog = new MyLoadingDialog(context);
        }
        if (!loadingDialog.isShowing()) {
            loadingDialog.setMessage(msg);
            loadingDialog.show();
        }
    }

    public void closeLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}
