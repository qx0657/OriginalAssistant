package fun.qianxiao.originalassistant.fragment.me;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.InputConfirmPopupView;
import com.lxj.xpopup.util.XPopupUtils;

import fun.qianxiao.originalassistant.MainActivity;
import fun.qianxiao.originalassistant.activity.AboutActivity;
import fun.qianxiao.originalassistant.activity.SettingsActivity;
import fun.qianxiao.originalassistant.api.hlx.HLXApiManager;
import fun.qianxiao.originalassistant.base.BaseActivity;
import fun.qianxiao.originalassistant.base.BaseFragment;
import fun.qianxiao.originalassistant.bean.HLXUserInfo;
import fun.qianxiao.originalassistant.config.SPConstants;
import fun.qianxiao.originalassistant.databinding.FragmentMeBinding;
import fun.qianxiao.originalassistant.view.loading.ILoadingView;

/**
 * MeFragment
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class MeFragment<A extends BaseActivity<?>> extends BaseFragment<FragmentMeBinding, A> implements ILoadingView {
    InputConfirmPopupView keyInputConfirmPopupView;

    @Override
    protected void initListener() {
        binding.tvNick.setOnClickListener(v -> login());
        binding.tvSignin.setOnClickListener(v -> signIn());
        binding.tvId.setOnClickListener(v -> copyId());
        binding.ivAvatar.setOnClickListener(v -> binding.tvId.performClick());
        binding.llAbout.setOnClickListener(v -> {
            ActivityUtils.startActivity(new Intent(activity, AboutActivity.class));
        });
        binding.llSetting.setOnClickListener(v -> {
            ActivityUtils.startActivity(new Intent(activity, SettingsActivity.class));
        });
        binding.llSupport.setOnClickListener(v -> {
            ToastUtils.showShort("支持");
        });
        binding.llHelp.setOnClickListener(v -> {
            ToastUtils.showShort("帮助");
        });

        KeyboardUtils.registerSoftInputChangedListener(activity, height -> {
            if (keyInputConfirmPopupView != null) {
                XPopupUtils.moveUpToKeyboard(height + 100, keyInputConfirmPopupView);
            }
        });
    }

    private void login() {
        if (true) {
            keyInputConfirmPopupView = new XPopup.Builder(activity).asInputConfirm(
                    "Key登录", "可使用抓包软件抓包获取，见请求字段'_key'，长度112位", "", "请输入Key",
                    text -> {
                        final int KEY_VALID_LENGTH = 112;
                        if (TextUtils.isEmpty(text)) {
                            ToastUtils.showShort("输入为空");
                        } else if (text.length() != KEY_VALID_LENGTH) {
                            ToastUtils.showShort("Key长度应为112位");
                        } else {
                            HLXApiManager.INSTANCE.checkKey(text, (valid, errMsg) -> {
                                if (valid) {
                                    SPUtils.getInstance().put(SPConstants.KEY_HLX_KEY, text);
                                    KeyboardUtils.hideSoftInput(keyInputConfirmPopupView.getEditText());
                                    ThreadUtils.runOnUiThreadDelayed(keyInputConfirmPopupView::dismiss, 50);
                                    loginingByKey();
                                } else {
                                    ToastUtils.showShort(errMsg);
                                }
                            });
                        }
                    });
            TextView tvContent = keyInputConfirmPopupView.getPopupContentView().findViewById(com.lxj.xpopup.R.id.tv_content);
            tvContent.setGravity(Gravity.START);
            keyInputConfirmPopupView.popupInfo.autoDismiss = false;
            keyInputConfirmPopupView.show();
            ThreadUtils.runOnUiThreadDelayed(() -> keyInputConfirmPopupView.getCancelTextView().setOnClickListener(v -> {
                KeyboardUtils.hideSoftInput(keyInputConfirmPopupView.getEditText());
                ThreadUtils.runOnUiThreadDelayed(keyInputConfirmPopupView::dismiss, 50);
            }), 100);
        }
    }

    private void loginingByKey() {
        openLoadingDialog("登录中");
        HLXApiManager.INSTANCE.getUserInfo(SPUtils.getInstance().getString(SPConstants.KEY_HLX_KEY), new HLXApiManager.OnGetUserInfoResult() {
            @Override
            public void onResult(boolean success, HLXUserInfo hlxUserInfo, String errMsg) {
                closeLoadingDialog();
                if (success) {
                    ToastUtils.showShort("登录成功" + hlxUserInfo.getNick());
                } else {
                    ToastUtils.showShort(errMsg);
                }
            }
        });
    }

    private void signIn() {
        ToastUtils.showShort("签到");
    }

    private void copyId() {
        ClipboardUtils.copyText("12345");
        ToastUtils.showShort("ID已复制至剪贴板");
    }

    @Override
    protected void initData() {
    }

    @Override
    public void openLoadingDialog(String msg) {
        ((MainActivity) activity).openLoadingDialog(msg);
    }

    @Override
    public void closeLoadingDialog() {
        ((MainActivity) activity).closeLoadingDialog();
    }
}
