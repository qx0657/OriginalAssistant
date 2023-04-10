package fun.qianxiao.originalassistant.activity.test;


import androidx.appcompat.widget.AppCompatRadioButton;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;

import fun.qianxiao.originalassistant.bean.AppInfo;
import fun.qianxiao.originalassistant.databinding.ActivityTestMovingBricksBinding;

/**
 * MovingBricksActivity
 *
 * @Author QianXiao
 * @Date 2023/3/17
 */
public class MovingBricksActivity extends BaseTestActivity<ActivityTestMovingBricksBinding> {
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
        sb.append("【测试结果】");
        sb.append(getResultText());
        ClipboardUtils.copyText(sb.toString());
        ToastUtils.showShort("已复制到剪贴板");
    }

    private String getResultText() {
        AppCompatRadioButton radioButtonChecked = findViewById(binding.rgMoveTypeRadioButtonGroup.getCheckedRadioButtonId());
        return radioButtonChecked.getText().toString();
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
        return "搬砖";
    }
}