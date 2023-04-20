package fun.qianxiao.originalassistant.manager;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.ConfirmPopupView;

import fun.qianxiao.originalassistant.activity.BrowserActivity;
import fun.qianxiao.originalassistant.config.AppConfig;

/**
 * PrivacyPolicyManager
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
                "本App尊重并保护所有使用服务用户的个人隐私权。" +
                "为了给您提供更优质的服务，本应用会按照本《隐私权政策》的规定使用和披露您的个人信息。" +
                "本应用会请求使用存储权限等以更好的为您提供服务。";
        style.append(privacyPolicy);
        int start = privacyPolicy.indexOf("《隐私权政策》");
        if (start != -1) {
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    BrowserActivity.load(context, "隐私政策", AppConfig.PRIVACY_POLICY_URL);
                }
            };
            int end = start + "《隐私权政策》".length();
            style.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

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
