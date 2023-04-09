package fun.qianxiao.originalassistant.activity.test;

import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.appcompat.widget.AppCompatRadioButton;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;

import fun.qianxiao.originalassistant.bean.AppInfo;
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
        setRadioButtonChangeLister();
        setFloatingActionButtonListener();

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
        binding.rbToastTrue.setOnCheckedChangeListener(onCheckedChangeListener);
        binding.rbToastFalse.setOnCheckedChangeListener(onCheckedChangeListener);
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
    public void onDestroy() {
        KeyboardUtils.unregisterSoftInputChangedListener(getWindow());
        super.onDestroy();
    }
}