package fun.qianxiao.originalassistant.fragment.find;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.InputConfirmPopupView;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.util.XPopupUtils;
import com.stx.xhb.androidx.XBanner;

import java.io.File;
import java.util.List;

import fun.qianxiao.originalassistant.MainActivity;
import fun.qianxiao.originalassistant.activity.hlxpicbed.HLXPictureBedActivity;
import fun.qianxiao.originalassistant.activity.selectapp.SelectAppActivity;
import fun.qianxiao.originalassistant.base.BaseFragment;
import fun.qianxiao.originalassistant.bean.FindBannerInfo;
import fun.qianxiao.originalassistant.config.AppConfig;
import fun.qianxiao.originalassistant.config.CodeConstants;
import fun.qianxiao.originalassistant.config.SPConstants;
import fun.qianxiao.originalassistant.databinding.FragmentFindBinding;
import fun.qianxiao.originalassistant.manager.HLXApiManager;
import fun.qianxiao.originalassistant.manager.HLXLinkedMeManager;
import fun.qianxiao.originalassistant.manager.PermissionManager;
import fun.qianxiao.originalassistant.utils.ApkExportUtils;
import fun.qianxiao.originalassistant.utils.FileOpenUtils;
import fun.qianxiao.originalassistant.utils.HLXUtils;
import fun.qianxiao.originalassistant.utils.HlxKeyLocal;
import fun.qianxiao.originalassistant.utils.ShortCutUtils;

/**
 * FindFragment
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class FindFragment extends BaseFragment<FragmentFindBinding, MainActivity> {
    private ActivityResultLauncher<Intent> activityResultLauncher;

    private InputConfirmPopupView customCodeInputConfirmPopupView;
    private InputConfirmPopupView renameCodeInputConfirmPopupView;

    @Override
    protected void initListener() {
        binding.llHlxFastStartShortCut.setOnClickListener(v -> {
            new XPopup.Builder(activity)
                    .asBottomList("请选择tab页", new String[]{"首页", "社区", "发现", "我的"}, new OnSelectListener() {
                        @Override
                        public void onSelect(int position, String text) {
                            createHLXFastStartShortCut(position);
                        }
                    })
                    .show();
        });
        binding.xbanner.setOnItemClickListener(new XBanner.OnItemClickListener() {
            @Override
            public void onItemClick(XBanner banner, Object model, View view, int position) {
                FindBannerInfo findBannerInfo = (FindBannerInfo) model;
                if (findBannerInfo.getMode() == FindBannerInfo.MODE.POST) {
                    HLXLinkedMeManager.INSTANCE.gotoPostDetail(findBannerInfo.getPostId(), false);
                } else if (findBannerInfo.getMode() == FindBannerInfo.MODE.URL) {
                    HLXLinkedMeManager.INSTANCE.gotoActionDetail(findBannerInfo.getId(), "");
                }
            }
        });
        binding.llHlxPictureBed.setOnClickListener(v -> {
            if (TextUtils.isEmpty(HlxKeyLocal.read())) {
                ToastUtils.showShort("未登录");
                return;
            }
            ActivityUtils.startActivity(HLXPictureBedActivity.class);
        });
        binding.llApkExportFindF.setOnClickListener(v -> appExport());

        binding.llCopyCode1FindF.setOnClickListener(v -> copyCode(CODE_TYPE.CODE_1));
        binding.llCopyCode2FindF.setOnClickListener(v -> copyCode(CODE_TYPE.CODE_2));
        binding.llCopyCode3FindF.setOnClickListener(v -> copyCode(CODE_TYPE.CODE_3));
        binding.llCopyCode4FindF.setOnClickListener(v -> copyCode(CODE_TYPE.CODE_4));

        binding.llCopyCode1FindF.setOnLongClickListener(v -> editCode(v, CODE_TYPE.CODE_1));
        binding.llCopyCode2FindF.setOnLongClickListener(v -> editCode(v, CODE_TYPE.CODE_2));
        binding.llCopyCode3FindF.setOnLongClickListener(v -> editCode(v, CODE_TYPE.CODE_3));
        binding.llCopyCode4FindF.setOnLongClickListener(v -> editCode(v, CODE_TYPE.CODE_4));

        KeyboardUtils.registerSoftInputChangedListener(activity, height -> {
            if (customCodeInputConfirmPopupView != null) {
                XPopupUtils.moveUpToKeyboard(height + 30, customCodeInputConfirmPopupView);
            }
        });
    }

    private enum CODE_TYPE {
        /**
         * 自定义代码2
         * 默认: 弹窗代码
         */
        CODE_1,
        /**
         * 自定义代码2
         */
        CODE_2,
        /**
         * 自定义代码3
         */
        CODE_3,
        /**
         * 自定义代码4
         */
        CODE_4
    }

    private String getCurrentCode(CODE_TYPE code_type) {
        switch (code_type) {
            case CODE_1:
                return SPUtils.getInstance().getString(SPConstants.KEY_CODE_1, CodeConstants.CODE_1);
            case CODE_2:
                return SPUtils.getInstance().getString(SPConstants.KEY_CODE_2, CodeConstants.CODE_2);
            case CODE_3:
                return SPUtils.getInstance().getString(SPConstants.KEY_CODE_3, CodeConstants.CODE_3);
            case CODE_4:
                return SPUtils.getInstance().getString(SPConstants.KEY_CODE_4, CodeConstants.CODE_4);
            default:
                return "";
        }
    }

    private boolean editCode(View view, CODE_TYPE code_type) {
        new XPopup.Builder(activity).atView(view)
                .hasShadowBg(false)
                .asAttachList(new String[]{"重命名", "自定义代码"}, null, new OnSelectListener() {
                    @Override
                    public void onSelect(int position, String text) {
                        if (position == 0) {
                            renameCode(code_type);
                        } else if (position == 1) {
                            customCode(code_type);
                        }
                    }
                })
                .show();
        return true;
    }

    private void renameCode(CODE_TYPE code_type) {
        renameCodeInputConfirmPopupView = new XPopup.Builder(activity)
                .dismissOnTouchOutside(false)
                .asInputConfirm("重命名", "", getCodeName(code_type), "", new OnInputConfirmListener() {
                    @Override
                    public void onConfirm(String text) {
                        if (TextUtils.isEmpty(text)) {
                            ToastUtils.showShort("输入为空");
                            baseInputXPopViewDismiss(renameCodeInputConfirmPopupView);
                            return;
                        }
                        saveCodeName(code_type, text);
                        refreshCodeName();
                        baseInputXPopViewDismiss(renameCodeInputConfirmPopupView);
                    }
                });
        baseInputXPopViewShow(renameCodeInputConfirmPopupView);
    }

    private String getCodeName(CODE_TYPE code_type) {
        switch (code_type) {
            case CODE_1:
                return SPUtils.getInstance().getString(SPConstants.KEY_CODE_NAME_2, CodeConstants.CODE_1_NAME_DEFAULT);
            case CODE_2:
                return SPUtils.getInstance().getString(SPConstants.KEY_CODE_NAME_2, CodeConstants.CODE_2_NAME_DEFAULT);
            case CODE_3:
                return SPUtils.getInstance().getString(SPConstants.KEY_CODE_NAME_3, CodeConstants.CODE_3_NAME_DEFAULT);
            case CODE_4:
                return SPUtils.getInstance().getString(SPConstants.KEY_CODE_NAME_4, CodeConstants.CODE_4_NAME_DEFAULT);
            default:
                return "";
        }
    }

    private void refreshCodeName() {
        binding.tvCodeName2.setText(getCodeName(CODE_TYPE.CODE_2));
        binding.tvCodeName3.setText(getCodeName(CODE_TYPE.CODE_3));
        binding.tvCodeName4.setText(getCodeName(CODE_TYPE.CODE_4));
    }

    private void saveCodeName(CODE_TYPE code_type, String text) {
        switch (code_type) {
            case CODE_1:
                SPUtils.getInstance().put(SPConstants.KEY_CODE_NAME_1, text);
                break;
            case CODE_2:
                SPUtils.getInstance().put(SPConstants.KEY_CODE_NAME_2, text);
                break;
            case CODE_3:
                SPUtils.getInstance().put(SPConstants.KEY_CODE_NAME_3, text);
                break;
            case CODE_4:
                SPUtils.getInstance().put(SPConstants.KEY_CODE_NAME_4, text);
                break;
            default:
                break;
        }
    }

    private void customCode(CODE_TYPE code_type) {
        customCodeInputConfirmPopupView = new XPopup.Builder(activity)
                .dismissOnTouchOutside(false)
                .asInputConfirm("自定义代码" + (code_type.ordinal() + 1), "", getCurrentCode(code_type), "", new OnInputConfirmListener() {
                    @Override
                    public void onConfirm(String code) {
                        if (TextUtils.isEmpty(code)) {
                            ToastUtils.showShort("输入为空");
                            return;
                        }
                        saveCode(code_type, code);
                        baseInputXPopViewDismiss(customCodeInputConfirmPopupView);
                    }
                });
        baseInputXPopViewShow(customCodeInputConfirmPopupView);
        ThreadUtils.runOnUiThreadDelayed(() -> {
            customCodeInputConfirmPopupView.getEditText().setSingleLine(false);
            customCodeInputConfirmPopupView.getEditText().setMaxHeight(ScreenUtils.getScreenHeight() / 2 - 250);
            customCodeInputConfirmPopupView.getEditText().setSelection(customCodeInputConfirmPopupView.getEditText().getText().length());
        }, 100);
    }

    private void saveCode(CODE_TYPE code_type, String code) {
        switch (code_type) {
            case CODE_1:
                SPUtils.getInstance().put(SPConstants.KEY_CODE_1, code);
                break;
            case CODE_2:
                SPUtils.getInstance().put(SPConstants.KEY_CODE_2, code);
                break;
            case CODE_3:
                SPUtils.getInstance().put(SPConstants.KEY_CODE_3, code);
                break;
            case CODE_4:
                SPUtils.getInstance().put(SPConstants.KEY_CODE_4, code);
                break;
            default:
                break;
        }
    }

    private void baseInputXPopViewShow(InputConfirmPopupView popupView) {
        if (popupView != null) {
            TextView tvContent = popupView.getPopupContentView().findViewById(com.lxj.xpopup.R.id.tv_content);
            tvContent.setGravity(Gravity.START);
            popupView.popupInfo.autoDismiss = false;
            popupView.show();
            ThreadUtils.runOnUiThreadDelayed(() -> popupView.getCancelTextView().setOnClickListener(v -> {
                baseInputXPopViewDismiss(popupView);
            }), 100);
        }
    }

    private void baseInputXPopViewDismiss(InputConfirmPopupView popupView) {
        if (popupView != null) {
            KeyboardUtils.hideSoftInput(popupView.getEditText());
            ThreadUtils.runOnUiThreadDelayed(popupView::dismiss, 50);
        }
    }

    private void copyCode(CODE_TYPE code_type) {
        String code = "";
        switch (code_type) {
            case CODE_1:
                code = SPUtils.getInstance().getString(SPConstants.KEY_CODE_1, CodeConstants.TOAST_CODE_TEXT);
                ClipboardUtils.copyText(code);
                ToastUtils.showShort("已复制至剪贴板");
                break;
            case CODE_2:
                code = SPUtils.getInstance().getString(SPConstants.KEY_CODE_2);
                if (TextUtils.isEmpty(code)) {
                    customCode(code_type);
                    return;
                }
                ClipboardUtils.copyText(code);
                ToastUtils.showShort("已复制至剪贴板");
                break;
            case CODE_3:
                code = SPUtils.getInstance().getString(SPConstants.KEY_CODE_3);
                if (TextUtils.isEmpty(code)) {
                    customCode(code_type);
                    return;
                }
                ClipboardUtils.copyText(code);
                ToastUtils.showShort("已复制至剪贴板");
                break;
            case CODE_4:
                code = SPUtils.getInstance().getString(SPConstants.KEY_CODE_4);
                if (TextUtils.isEmpty(code)) {
                    customCode(code_type);
                    return;
                }
                ClipboardUtils.copyText(code);
                ToastUtils.showShort("已复制至剪贴板");
                break;
            default:
                break;
        }
    }

    private void appExport() {
        boolean permission = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                permission = true;
            } else {
                ToastUtils.showShort("请给予" + AppUtils.getAppName() + "所有文件权限");
                PermissionManager.getInstance().requestManageExternalStoragePermission(activity);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                permission = true;
            } else {
                ToastUtils.showShort("请给予" + AppUtils.getAppName() + "文件读写权限");
                PermissionManager.getInstance().requestReadWritePermission();
            }
        }
        if (permission) {
            activityResultLauncher.launch(new Intent(activity, SelectAppActivity.class));
        }
    }

    @Override
    protected void initData() {
        initActivityResultLauncher();
        initBanner();
        initHlxActivityData();
        refreshCodeName();
    }

    private void initActivityResultLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int resultCode = result.getResultCode();
                Intent data = result.getData();
                if (resultCode == SelectAppActivity.RESULT_CODE_SELECT_APP_OK) {
                    if (data != null) {
                        String appName = data.getStringExtra(SelectAppActivity.KEY_APP_NAME);
                        String appPackageName = data.getStringExtra(SelectAppActivity.KEY_APP_PACKAGE_NAME);
                        String storePath = SPUtils.getInstance().getString(SPConstants.KEY_APK_EXPORT_DIR, AppConfig.DEFAULT_APK_EXPORT_DIR);
                        if (!storePath.endsWith(File.separator)) {
                            storePath = storePath + File.separator;
                        }
                        String outPath = storePath + appName + "_" + AppUtils.getAppVersionName(appPackageName) + "_" + AppUtils.getAppVersionCode(appPackageName) + ".apk";
                        activity.openLoadingDialog("导出中");
                        ThreadUtils.executeBySingle(new ThreadUtils.SimpleTask<Boolean>() {
                            @Override
                            public Boolean doInBackground() throws Throwable {
                                return ApkExportUtils.exportApk(appPackageName, outPath);
                            }

                            @Override
                            public void onSuccess(Boolean result) {
                                activity.closeLoadingDialog();
                                if (result) {
                                    new XPopup.Builder(activity)
                                            .dismissOnTouchOutside(false)
                                            .asConfirm("Apk导出成功", outPath, "确定", "打开", new OnConfirmListener() {
                                                @Override
                                                public void onConfirm() {
                                                    FileOpenUtils.openFile(activity, new File(outPath));
                                                }
                                            }, new OnCancelListener() {
                                                @Override
                                                public void onCancel() {

                                                }
                                            }, false)
                                            .show();
                                } else {
                                    ToastUtils.showShort("Apk导出失败");
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void initBanner() {
        binding.xbanner.loadImage(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_CENTER);
                Glide.with(view).load(((FindBannerInfo) model).getImageUrl()).into((ImageView) view);
            }
        });
    }

    private void initHlxActivityData() {
        HLXApiManager.INSTANCE.getActivityList(new HLXApiManager.OnGetActivityListListener() {
            @Override
            public void onGetActivityList(int code, String errMsg, List<FindBannerInfo> list) {
                if (code == HLXApiManager.OnGetActivityListListener.SUCCESS) {
                    setActivityData(list);
                } else {
                    ToastUtils.showShort(errMsg);
                }
            }
        });
    }

    private void setActivityData(List<FindBannerInfo> data) {
        if (data.size() == 0) {
            binding.tvNoActivities.setVisibility(View.VISIBLE);
        } else {
            binding.tvNoActivities.setVisibility(View.GONE);
            binding.xbanner.setBannerData(data);
        }
    }

    private void createHLXFastStartShortCut(int tabIndex) {
        Intent intent = HLXUtils.getHlxHomeActivityIntent();
        if (intent == null) {
            ToastUtils.showShort("请检查是否已安装葫芦侠App");
            return;
        }
        intent.putExtra("tab_index", tabIndex);
        ShortCutUtils.create(activity, intent, "葫芦侠快速启动");
    }
}
