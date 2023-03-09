package fun.qianxiao.originalassistant.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.ConfirmPopupView;

/**
 * 隐私政策管理
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class PrivacyPolicyManager {
    private final String KEY_AGREE_Privacy_POLICY = "agreePrivacyPolicy";
    private Context context;

    public PrivacyPolicyManager(Context context) {
        this.context = context;
    }

    public boolean isAgreePrivacyPolicy() {
        return SPUtils.getInstance().getBoolean(KEY_AGREE_Privacy_POLICY);
    }

    public interface OnPrivacyPolicyListener {
        void onAgree();

        void onRefuse();
    }

    public void confrim(OnPrivacyPolicyListener onPrivacyPolicyListener) {
        SpannableStringBuilder style = new SpannableStringBuilder();
        String privacyPolicy = "本应用非常重视用户隐私政策并严格遵守相关的法律规定。" +
                "本app尊重并保护所有使用服务用户的个人隐私权。" +
                "为了给您提供更准确、更优质的服务，本应用会按照本《隐私权政策》的规定使用和披露您的个人信息。" +
                "本应用会请求使用位置权限、手机信息权限、存储权限等以更好的为您提供服务。";
        style.append(privacyPolicy);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                ToastUtils.showShort("跳转隐私政策界面");
            }
        };
        int start = privacyPolicy.indexOf("《隐私权政策》");
        int end = start + "《隐私权政策》".length();
        style.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ConfirmPopupView confirmPopupView = new XPopup.Builder(getContext())
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(false)
                .asConfirm(
                        "隐私政策",
                        style,
                        // 确认
                        () -> {
                            SPUtils.getInstance().put(KEY_AGREE_Privacy_POLICY, true);
                            if (onPrivacyPolicyListener != null) {
                                onPrivacyPolicyListener.onAgree();
                            }
                        },
                        // 取消
                        () -> {
                            SPUtils.getInstance().put(KEY_AGREE_Privacy_POLICY, false);
                            if (onPrivacyPolicyListener != null) {
                                onPrivacyPolicyListener.onRefuse();
                            }
                        });
        TextView contentView = confirmPopupView.getContentTextView();
        contentView.setGravity(Gravity.START);

        confirmPopupView.setConfirmText("同意")
                .setCancelText("拒绝")
                .show();
    }

    private Context getContext() {
        return context;
    }
}
