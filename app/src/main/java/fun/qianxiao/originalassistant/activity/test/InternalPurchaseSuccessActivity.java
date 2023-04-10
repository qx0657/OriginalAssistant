package fun.qianxiao.originalassistant.activity.test;

import android.os.Build;
import android.view.View;

import androidx.appcompat.widget.AppCompatRadioButton;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.ToastUtils;

import fun.qianxiao.originalassistant.bean.AppInfo;
import fun.qianxiao.originalassistant.databinding.ActivityTestInternalPurchaseSuccessBinding;

/**
 * InternalPurchaseSuccessActivity
 *
 * @Author QianXiao
 * @Date 2023/3/17
 */
public class InternalPurchaseSuccessActivity extends BaseTestActivity<ActivityTestInternalPurchaseSuccessBinding> {
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
        sb.append("【测试说明】内购破解\n");
        sb.append("【测试结果】内购成功\n");
        sb.append("【测试详情】");
        if (binding.rbFlyModeSupport.isChecked()) {
            sb.append("支持飞行模式，");
        } else {
            sb.append("不支持飞行模式，");
        }
        if (binding.rbPurchaseSuccessDirectly.isChecked()) {
            sb.append("点击购买直接成功，");
        } else {
            sb.append("购买时出现支付窗口，点击确认（返回、叉叉），");
        }
        if (binding.rbToastTrue.isChecked()) {
            sb.append("有个人弹窗，");
        } else {
            sb.append("无个人弹窗，");
        }
        if (PhoneUtils.isSimCardReady()) {
            sb.append("有手机卡，");
        } else {
            sb.append("无手机卡，");
        }
        int smsPermissionCheckResult = isRemoveSmsPermission(binding.etGamePackageName.getText().toString());
        sb.append(getTextBySmsPermissionRemoveResult(smsPermissionCheckResult));
        ClipboardUtils.copyText(sb.toString());
        ToastUtils.showShort("已复制到剪贴板");
    }

    private String getSimChannel() {
        for (int i = 0; i < binding.rgSimChannelRadioButtonGroup.getChildCount(); i++) {
            View view = binding.rgSimChannelRadioButtonGroup.getChildAt(i);
            if (view instanceof AppCompatRadioButton) {
                AppCompatRadioButton radioButton = (AppCompatRadioButton) view;
                if (radioButton.isChecked()) {
                    return radioButton.getText().toString();
                }
            }
        }
        return null;
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
        return "内购成功";
    }
}
