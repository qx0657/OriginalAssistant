package fun.qianxiao.originalassistant.fragment.me;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.InputConfirmPopupView;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.util.XPopupUtils;

import fun.qianxiao.originalassistant.MainActivity;
import fun.qianxiao.originalassistant.R;
import fun.qianxiao.originalassistant.activity.AboutActivity;
import fun.qianxiao.originalassistant.activity.BrowserActivity;
import fun.qianxiao.originalassistant.activity.SettingsActivity;
import fun.qianxiao.originalassistant.activity.SupportActivity;
import fun.qianxiao.originalassistant.api.hlx.HLXApi;
import fun.qianxiao.originalassistant.base.BaseActivity;
import fun.qianxiao.originalassistant.base.BaseFragment;
import fun.qianxiao.originalassistant.bean.HLXUserInfo;
import fun.qianxiao.originalassistant.config.AppConfig;
import fun.qianxiao.originalassistant.config.SPConstants;
import fun.qianxiao.originalassistant.databinding.FragmentMeBinding;
import fun.qianxiao.originalassistant.manager.HLXApiManager;
import fun.qianxiao.originalassistant.utils.HlxKeyLocal;
import fun.qianxiao.originalassistant.utils.MyStringUtils;
import fun.qianxiao.originalassistant.view.loading.ILoadingView;

/**
 * MeFragment
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class MeFragment<A extends BaseActivity<?>> extends BaseFragment<FragmentMeBinding, A> implements ILoadingView {
    private InputConfirmPopupView keyInputConfirmPopupView;
    private InputConfirmPopupView userIdInputConfirmPopupView;
    private boolean hasSignIn;

    @Override
    protected void initListener() {
        binding.tvNick.setOnClickListener(v -> showLogin());
        binding.tvSignin.setOnClickListener(v -> signIn());
        binding.tvId.setOnClickListener(v -> setUserId());
        binding.tvId.setOnLongClickListener(v -> {
            copyId();
            return true;
        });
        binding.ivAvatar.setOnClickListener(v -> binding.tvNick.performClick());
        binding.llAbout.setOnClickListener(v -> {
            ActivityUtils.startActivity(new Intent(activity, AboutActivity.class));
        });
        binding.llSetting.setOnClickListener(v -> {
            ActivityUtils.startActivity(new Intent(activity, SettingsActivity.class));
        });
        binding.llSupport.setOnClickListener(v -> {
            ActivityUtils.startActivity(new Intent(activity, SupportActivity.class));
        });
        binding.llHelp.setOnClickListener(v -> {
            BrowserActivity.load(getContext(), "帮助中心", AppConfig.HELP_URL);
        });

        KeyboardUtils.registerSoftInputChangedListener(activity, height -> {
            if (keyInputConfirmPopupView != null) {
                XPopupUtils.moveUpToKeyboard(height + 100, keyInputConfirmPopupView);
            }
            if (userIdInputConfirmPopupView != null) {
                XPopupUtils.moveUpToKeyboard(height + 100, userIdInputConfirmPopupView);
            }
        });
    }

    private void copyId() {
        Object tag = binding.tvId.getTag();
        if (tag != null) {
            ClipboardUtils.copyText((CharSequence) tag);
            ToastUtils.showShort("ID已复制至剪贴板");
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

    private void showLogin() {
        if (TextUtils.isEmpty(HlxKeyLocal.read())) {
            SpannableStringBuilder style = new SpannableStringBuilder();
            String s = "key获取请查看帮助中心";
            style.append(s);
            int start = s.indexOf("帮助中心");
            if (start != -1) {
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        BrowserActivity.load(getContext(), "帮助中心", AppConfig.HELP_URL);
                    }
                };
                int end = start + "帮助中心".length();
                style.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            keyInputConfirmPopupView = new XPopup.Builder(activity)
                    .dismissOnTouchOutside(false)
                    .asInputConfirm(
                            "葫芦侠Key登录", style, "", "请输入Key",
                            text -> {
                                final int KEY_VALID_LENGTH = 112;
                                if (TextUtils.isEmpty(text)) {
                                    ToastUtils.showShort("输入为空");
                                } else if (text.length() != KEY_VALID_LENGTH) {
                                    ToastUtils.showShort("Key长度应为112位");
                                } else {
                                    HLXApiManager.INSTANCE.checkKey(text, (valid, errMsg) -> {
                                        if (valid) {
                                            HlxKeyLocal.write(text);
                                            setKeyToNick(text);
                                            baseInputXPopViewDismiss(keyInputConfirmPopupView);
                                            loginingByKey();
                                        } else {
                                            ToastUtils.showShort(errMsg);
                                        }
                                    });
                                }
                            });
            baseInputXPopViewShow(keyInputConfirmPopupView);
        } else {
            new XPopup.Builder(getContext())
                    .asConfirm("温馨提示", "是否重设Key?", new OnConfirmListener() {
                        @Override
                        public void onConfirm() {
                            SPUtils.getInstance().remove(SPConstants.KEY_HLX_KEY);
                            displayUserInfo(null);
                            String userId = SPUtils.getInstance().getString(SPConstants.KEY_HLX_USER_ID);
                            setUserId(userId);
                        }
                    })
                    .show();
        }
    }

    private void loginingByKey() {
        String userId = SPUtils.getInstance().getString(SPConstants.KEY_HLX_USER_ID);
        if (TextUtils.isEmpty(userId)) {
            ToastUtils.showShort("设置Key成功，如需获取用户信息请设置userId");
            signInCheck(HlxKeyLocal.read());
        } else {
            loginHLX(HlxKeyLocal.read(), userId, false);
        }
    }

    private void loginHLX(String key, String userId, boolean isSilent) {
        if (!isSilent) {
            openLoadingDialog("登录中");
        }
        HLXApiManager.INSTANCE.getUserInfo(
                key,
                userId,
                new HLXApiManager.OnGetUserInfoResult() {
                    @Override
                    public void onResult(boolean success, HLXUserInfo hlxUserInfo, String errMsg) {
                        closeLoadingDialog();
                        if (success) {
                            SPUtils.getInstance().put(SPConstants.KEY_HLX_USER_ID, userId);
                            if (!isSilent) {
                                ToastUtils.showShort("登录成功");
                            }
                            signInCheck(key);
                            baseInputXPopViewDismiss(userIdInputConfirmPopupView);
                            displayUserInfo(hlxUserInfo);
                        } else {
                            HLXApiManager.INSTANCE.checkKey(key, new HLXApiManager.OnCommonBooleanResultListener() {
                                @Override
                                public void onResult(boolean valid, String errMsg2) {
                                    if (!valid) {
                                        if ("key无效".equals(errMsg2)) {
                                            ToastUtils.showShort("Key已失效, 请重新登录");
                                            SPUtils.getInstance().remove(SPConstants.KEY_HLX_KEY);
                                        } else {
                                            ToastUtils.showShort(errMsg);
                                        }
                                        displayUserInfo(null);
                                    } else {
                                        setUserId(null);
                                        //setKeyToNick(key);
                                        SPUtils.getInstance().remove(SPConstants.KEY_HLX_USER_ID);
                                    }
                                }
                            });
                            if (!isSilent) {
                                ToastUtils.showShort(errMsg);
                            }
                        }
                    }
                });
    }

    private void signInInner(String key) {
        openLoadingDialog("签到中");
        HLXApiManager.INSTANCE.signIn(key, HLXApi.CAT_ID_ORIGINAL, new HLXApiManager.OnCommonBooleanResultListener() {
            @Override
            public void onResult(boolean success, String errMsg) {
                closeLoadingDialog();
                if (success) {
                    ToastUtils.showShort("签到成功");
                    setHasSignIn();
                } else {
                    ToastUtils.showShort(errMsg);
                }
            }
        });
    }

    private void signIn() {
        String key = HlxKeyLocal.read();
        if (TextUtils.isEmpty(key)) {
            ToastUtils.showShort("未登录");
            return;
        }
        if (hasSignIn) {
            ToastUtils.showShort("今日已签到");
            return;
        }
        signInInner(key);
    }

    private void setUserId() {
        String userIdSp = SPUtils.getInstance().getString(SPConstants.KEY_HLX_USER_ID);
        if (!TextUtils.isEmpty(userIdSp)) {
            new XPopup.Builder(getContext())
                    .asConfirm("温馨提示", "是否重设userId?", new OnConfirmListener() {
                        @Override
                        public void onConfirm() {
                            SPUtils.getInstance().remove(SPConstants.KEY_HLX_USER_ID);
                            displayUserInfo(null);
                            String key = HlxKeyLocal.read();
                            setKeyToNick(key);
                        }
                    })
                    .show();
        } else {
            userIdInputConfirmPopupView = new XPopup.Builder(activity)
                    .dismissOnTouchOutside(false)
                    .asInputConfirm(
                            "UserID", "可使用抓包软件抓包获取，见请求字段'user_id'", "", "请输入user_id",
                            userId -> {
                                if (TextUtils.isEmpty(userId)) {
                                    ToastUtils.showShort("输入为空");
                                } else if (!MyStringUtils.isNumeric(userId)) {
                                    ToastUtils.showShort("输入非法");
                                } else {
                                    baseInputXPopViewDismiss(userIdInputConfirmPopupView);
                                    setUserId(userId);
                                    String key = HlxKeyLocal.read();
                                    if (TextUtils.isEmpty(key)) {
                                        ToastUtils.showShort("userId设置成功，请设置key登录");
                                    } else {
                                        loginHLX(key, userId, false);
                                    }
                                }
                            });
            baseInputXPopViewShow(userIdInputConfirmPopupView);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setKeyToNick(String key) {
        if (!TextUtils.isEmpty(key)) {
            binding.tvNick.setText(key.substring(0, 4) + "****" + key.substring(key.length() - 4));
        }
    }

    @SuppressLint("SetTextI18n")
    private void setUserId(String userId) {
        if (!TextUtils.isEmpty(userId)) {
            binding.tvId.setTag(userId);
            binding.tvId.setText("ID: " + userId);
        } else {
            binding.tvId.setTag(null);
            binding.tvId.setText("ID: 未设置（点击设置）");
        }
    }

    @Override
    protected void initData() {
        String key = HlxKeyLocal.read();
        if (!TextUtils.isEmpty(key)) {
            setKeyToNick(key);
        }
        String userId = SPUtils.getInstance().getString(SPConstants.KEY_HLX_USER_ID);
        if (!TextUtils.isEmpty(userId)) {
            setUserId(userId);
        }
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(userId)) {
            loginHLX(key, userId, true);
        } else if (!TextUtils.isEmpty(key)) {
            HLXApiManager.INSTANCE.checkKey(key, new HLXApiManager.OnCommonBooleanResultListener() {
                @Override
                public void onResult(boolean valid, String errMsg) {
                    if (valid) {
                        signInCheck(key);
                    } else {
                        if ("key无效".equals(errMsg)) {
                            ToastUtils.showShort("Key已失效, 请重新登录");
                            SPUtils.getInstance().remove(SPConstants.KEY_HLX_KEY);
                        } else {
                            ToastUtils.showShort(errMsg);
                        }
                        displayUserInfo(null);
                    }
                }
            });
        }
    }

    private void setHasSignIn() {
        hasSignIn = true;
        binding.tvSignin.setText("已签到");
    }

    private void signInCheck(String key) {
        HLXApiManager.INSTANCE.signInCheck(key, HLXApi.CAT_ID_ORIGINAL, new HLXApiManager.OnCommonBooleanResultListener() {
            @Override
            public void onResult(boolean valid, String errMsg) {
                if (valid) {
                    // has not sign in, auto sign in in future
                } else if ("今日已签到".equals(errMsg)) {
                    setHasSignIn();
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void displayUserInfo(HLXUserInfo userInfo) {
        if (userInfo == null) {
            binding.tvId.setText("ID: 未设置（点击设置）");
            binding.tvId.setTag(null);
            binding.tvNick.setText("点击登录");
            binding.tvPostCount.setText("-");
            binding.tvCommentCount.setText("-");
            binding.ivAvatar.setImageResource(R.drawable.ic_svg_default_avatar);
        } else {
            setUserId(String.valueOf(userInfo.getUserId()));
            binding.tvNick.setText(userInfo.getNick());
            binding.tvPostCount.setText(String.valueOf(userInfo.getPostCount()));
            binding.tvCommentCount.setText(String.valueOf(userInfo.getCommentCount()));
            Glide.with(binding.ivAvatar).load(userInfo.getAvatarUrl()).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(binding.ivAvatar);
        }
    }

    @Override
    public void openLoadingDialog(String msg) {
        ((MainActivity) activity).openLoadingDialog(msg);
    }

    @Override
    public void closeLoadingDialog() {
        ((MainActivity) activity).closeLoadingDialog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        KeyboardUtils.unregisterSoftInputChangedListener(activity.getWindow());
    }
}
