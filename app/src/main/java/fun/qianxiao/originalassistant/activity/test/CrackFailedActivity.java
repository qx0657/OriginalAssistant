package fun.qianxiao.originalassistant.activity.test;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.appcompat.widget.AppCompatRadioButton;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;

import fun.qianxiao.originalassistant.bean.AppInfo;
import fun.qianxiao.originalassistant.databinding.ActivityTestCrackFailedBinding;

/**
 * CrackFailedActivity
 *
 * @Author QianXiao
 * @Date 2023/3/17
 */
public class CrackFailedActivity extends BaseTestActivity<ActivityTestCrackFailedBinding> {
    @Override
    protected void initListener() {
        setFloatingActionButtonListener();
        setRadioButtonChangeLister();

        KeyboardUtils.registerSoftInputChangedListener(this, height -> {
            if (height != 0) {
                binding.famTest.collapse();
                binding.famTest.setVisibility(View.GONE);
            } else {
                ThreadUtils.runOnUiThreadDelayed(() -> binding.famTest.setVisibility(View.VISIBLE), 50);
            }
        });
    }

    private void setFloatingActionButtonListener() {
        binding.fabSelectApp.setOnClickListener(view -> {
            binding.famTest.collapse();
            ThreadUtils.runOnUiThreadDelayed(this::selectApp, 100);
        });
        binding.fabCleanContent.setOnClickListener(view -> {
            binding.famTest.collapse();
            cleanAllInputContent();
        });
        binding.fabCopyContent.setOnClickListener(view -> {
            binding.famTest.collapse();
            copyContent();
        });
        binding.fabGotoApp.setOnClickListener(view -> {
            binding.famTest.collapse();
            gotoApp();
        });
    }

    private void cleanAllInputContent() {
        binding.etGameName.setText("");
        binding.etGamePackageName.setText("");
        binding.etGameVersion.setText("");
        binding.etGameVersionCode.setText("");
    }

    private void copyContent() {
        StringBuilder sb = new StringBuilder();
        sb.append("【游戏名称】");
        sb.append(binding.etGameName.getText().toString());
        sb.append("\n");
        sb.append("【游戏包名】");
        sb.append(binding.etGamePackageName.getText().toString());
        sb.append("\n");
        sb.append("【游戏版本】");
        sb.append(binding.etGameVersion.getText().toString());
        sb.append("\n");
        sb.append("【游戏版本值】");
        sb.append(binding.etGameVersionCode.getText().toString());
        sb.append("\n");
        sb.append("【系统版本】");
        sb.append(Build.VERSION.RELEASE);
        sb.append("\n");
        sb.append("【运营商】");
        sb.append(getSimChannel());
        sb.append("\n");
        sb.append("【测试说明】");
        sb.append(binding.rbModTypeInternalPurchase.isChecked() ? "内购破解" : "游戏修改");
        sb.append("\n");
        sb.append("【测试结果】");
        sb.append(binding.rbModTypeInternalPurchase.isChecked() ? "内购失败" : "修改失败");
        sb.append("\n");
        sb.append("【失败类型】");
        sb.append(getFailedTypeText() + "，");
        int smsPermissionCheckResult = isRemoveSmsPermission(binding.etGamePackageName.getText().toString());
        sb.append(getTextBySmsPermissionRemoveResult(smsPermissionCheckResult));
        ClipboardUtils.copyText(sb.toString());
        ToastUtils.showShort("已复制到剪贴板");
    }

    private String getFailedTypeText() {
        for (int i = 0; i < binding.llFailedTypeRadioButtonGroup.getChildCount(); i++) {
            View view = binding.llFailedTypeRadioButtonGroup.getChildAt(i);
            if (view instanceof AppCompatRadioButton) {
                AppCompatRadioButton radioButton = (AppCompatRadioButton) view;
                if (radioButton.isChecked()) {
                    return radioButton.getText().toString();
                }
            }
        }
        return null;
    }

    private String getSimChannel() {
        for (int i = 0; i < binding.llSimChannelRadioButtonGroup.getChildCount(); i++) {
            View view = binding.llSimChannelRadioButtonGroup.getChildAt(i);
            if (view instanceof AppCompatRadioButton) {
                AppCompatRadioButton radioButton = (AppCompatRadioButton) view;
                if (radioButton.isChecked()) {
                    return radioButton.getText().toString();
                }
            }
        }
        return null;
    }

    private void setRadioButtonChangeLister() {
        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = (buttonView, isChecked) -> {
            ViewGroup viewGroup = (ViewGroup) buttonView.getParent();
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View view = viewGroup.getChildAt(i);
                if (view instanceof AppCompatRadioButton) {
                    AppCompatRadioButton radioButton = (AppCompatRadioButton) view;
                    if (radioButton.getId() != buttonView.getId() && isChecked) {
                        radioButton.setChecked(false);
                    }
                }
            }
        };
        binding.rbModTypeInternalPurchase.setOnCheckedChangeListener(onCheckedChangeListener);
        binding.rbModTypeGameModify.setOnCheckedChangeListener(onCheckedChangeListener);

        binding.rbFailedTypeInternalPurchaseFailed.setOnCheckedChangeListener(onCheckedChangeListener);
        binding.rbFailedTypeGameModifyFailed.setOnCheckedChangeListener(onCheckedChangeListener);
        binding.rbFailedTypeGameCannotInstall.setOnCheckedChangeListener(onCheckedChangeListener);
        binding.rbFailedTypeGameCrashOrBlackScreen.setOnCheckedChangeListener(onCheckedChangeListener);

        binding.rbSimChannelCmcc.setOnCheckedChangeListener(onCheckedChangeListener);
        binding.rbSimChannelCucc.setOnCheckedChangeListener(onCheckedChangeListener);
        binding.rbSimChannelCtcc.setOnCheckedChangeListener(onCheckedChangeListener);
        binding.rbSimChannelNone.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    @Override
    protected void initData() {
        super.initData();
        initSimChannel();
    }

    private void initSimChannel() {
        AppCompatRadioButton[] simRadioButtons = new AppCompatRadioButton[4];
        int index = 0;
        for (int i = 0; i < binding.llSimChannelRadioButtonGroup.getChildCount(); i++) {
            View view = binding.llSimChannelRadioButtonGroup.getChildAt(i);
            if (view instanceof AppCompatRadioButton) {
                AppCompatRadioButton radioButton = (AppCompatRadioButton) view;
                simRadioButtons[index++] = radioButton;
            }
        }
        int indexSelf = getSimChannelIndex();
        if (indexSelf >= 0 && indexSelf < 4 && simRadioButtons[indexSelf] != null) {
            simRadioButtons[indexSelf].setChecked(true);
        }
    }

    @Override
    protected void onSelectApp(AppInfo appInfo) {
        binding.etGameName.setText(appInfo.getAppName());
        binding.etGamePackageName.setText(appInfo.getPackageName());
        binding.etGameVersion.setText(appInfo.getVersionName());
        binding.etGameVersionCode.setText(String.valueOf(appInfo.getVersionCode()));
    }

    @Override
    protected CharSequence getTestTitle() {
        return "破解失败";
    }

    @Override
    public void onDestroy() {
        KeyboardUtils.unregisterSoftInputChangedListener(getWindow());
        super.onDestroy();
    }
}