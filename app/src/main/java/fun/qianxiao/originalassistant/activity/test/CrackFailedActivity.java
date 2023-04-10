package fun.qianxiao.originalassistant.activity.test;

import android.os.Build;

import androidx.appcompat.widget.AppCompatRadioButton;

import com.blankj.utilcode.util.ClipboardUtils;
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
        super.initListener();
    }

    @Override
    protected void cleanAllInputContent() {
        binding.etGameName.setText("");
        binding.etGamePackageName.setText("");
        binding.etGameVersion.setText("");
        binding.etGameVersionCode.setText("");
    }

    @Override
    protected void copyContent() {
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
        AppCompatRadioButton radioButtonChecked = findViewById(binding.rgFailedTypeRadioButtonGroup.getCheckedRadioButtonId());
        return radioButtonChecked.getText().toString();
    }

    private String getSimChannel() {
        AppCompatRadioButton radioButtonChecked = findViewById(binding.rgSimChannelRadioButtonGroup.getCheckedRadioButtonId());
        return radioButtonChecked.getText().toString();
    }

    @Override
    protected void initData() {
        super.initData();
        initSimChannel();
    }

    private void initSimChannel() {
        int[] rbSimIds = {
                binding.rbSimChannelCmcc.getId(),
                binding.rbSimChannelCucc.getId(),
                binding.rbSimChannelCtcc.getId(),
                binding.rbSimChannelNone.getId(),
        };
        int indexSelf = getSimChannelIndex();
        if (indexSelf >= 0 && indexSelf < rbSimIds.length) {
            binding.rgSimChannelRadioButtonGroup.check(rbSimIds[indexSelf]);
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
}