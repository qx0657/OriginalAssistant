package fun.qianxiao.originalassistant.activity.test;

import android.os.Build;
import android.text.TextUtils;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;

import fun.qianxiao.originalassistant.bean.AppInfo;
import fun.qianxiao.originalassistant.config.Constants;
import fun.qianxiao.originalassistant.config.SPConstants;
import fun.qianxiao.originalassistant.databinding.ActivityTestReviseSuccessBinding;

/**
 * ReviseSuccessActivity
 *
 * @Author QianXiao
 * @Date 2023/3/17
 */
public class ReviseSuccessActivity extends BaseTestActivity<ActivityTestReviseSuccessBinding> {
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
        int mode = SPUtils.getInstance().getInt(SPConstants.KEY_TEST_APP_MODE, Constants.APP_MODE_GAME);
        sb.append(mode == Constants.APP_MODE_GAME ? "【游戏名称】" : "【软件名称】");
        sb.append(binding.etGameName.getText().toString());
        sb.append("\n");
        sb.append(mode == Constants.APP_MODE_GAME ? "【游戏包名】" : "【软件包名】");
        sb.append(binding.etGamePackageName.getText().toString());
        sb.append("\n");
        sb.append(mode == Constants.APP_MODE_GAME ? "【游戏版本】" : "【软件版本】");
        sb.append(binding.etGameVersion.getText().toString());
        sb.append("\n");
        sb.append(mode == Constants.APP_MODE_GAME ? "【游戏版本值】" : "【软件版本值】");
        sb.append(binding.etGameVersionCode.getText().toString());
        sb.append("\n");
        sb.append("【系统版本】");
        sb.append(Build.VERSION.RELEASE);
        sb.append("\n");
        sb.append("【测试说明】游戏修改\n");
        sb.append("【测试结果】修改成功\n");
        sb.append("【测试详情】");
        if (binding.cbModTypeLargeAmountOfCurrency.isChecked()) {
            sb.append("进入游戏赠送大量货币，");
        }
        if (binding.cbModTypeUnlockLevel.isChecked()) {
            sb.append("解锁游戏关卡，");
        }
        if (binding.cbModTypeUnlockItem.isChecked()) {
            sb.append("解锁游戏物品，");
        }
        if (binding.cbModTypePurchaseReverse.isChecked()) {
            sb.append("购买物品反加金币，");
        }
        if (binding.rbToastTrue.isChecked()) {
            sb.append("有个人弹窗，");
        } else {
            sb.append("无个人弹窗，");
        }
        int smsPermissionCheckResult = isRemoveSmsPermission(binding.etGamePackageName.getText().toString());
        sb.append(getTextBySmsPermissionRemoveResult(smsPermissionCheckResult));
        sb.append("\n");
        sb.append("【其他情况】");
        String otherSituations = binding.etOtherSituations.getText().toString();
        if (TextUtils.isEmpty(otherSituations)) {
            sb.append("暂无其他情况");
        } else {
            sb.append(otherSituations);
        }
        ClipboardUtils.copyText(sb.toString());
        ToastUtils.showShort("已复制到剪贴板");
    }

    @Override
    protected void initData() {
        super.initData();
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
        return "修改成功";
    }

    @Override
    protected void setAppMode(int mode) {
        if (mode == Constants.APP_MODE_GAME) {
            binding.tlGameName.setHint("游戏名称");
            binding.tlGamePackageName.setHint("游戏包名");
            binding.tlGameVersion.setHint("游戏版本");
            binding.tlGameVersionCode.setHint("游戏版本值");
        } else if (mode == Constants.APP_MODE_SOFTWARE) {
            binding.tlGameName.setHint("软件名称");
            binding.tlGamePackageName.setHint("软件包名");
            binding.tlGameVersion.setHint("软件版本");
            binding.tlGameVersionCode.setHint("软件版本值");
        }
    }
}