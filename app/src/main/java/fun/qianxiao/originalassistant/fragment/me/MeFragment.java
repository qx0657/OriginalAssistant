package fun.qianxiao.originalassistant.fragment.me;

import android.content.Intent;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.ToastUtils;

import fun.qianxiao.originalassistant.activity.AboutActivity;
import fun.qianxiao.originalassistant.activity.SettingsActivity;
import fun.qianxiao.originalassistant.base.BaseActivity;
import fun.qianxiao.originalassistant.base.BaseFragment;
import fun.qianxiao.originalassistant.databinding.FragmentMeBinding;

/**
 * MeFragment
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class MeFragment<A extends BaseActivity<?>> extends BaseFragment<FragmentMeBinding, A> {

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
    }

    private void login() {
        ToastUtils.showShort("登录");
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
}
