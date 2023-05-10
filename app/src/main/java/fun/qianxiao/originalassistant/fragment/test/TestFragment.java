package fun.qianxiao.originalassistant.fragment.test;

import android.content.Intent;

import com.blankj.utilcode.util.ActivityUtils;

import fun.qianxiao.originalassistant.MainActivity;
import fun.qianxiao.originalassistant.activity.test.CrackFailedActivity;
import fun.qianxiao.originalassistant.activity.test.InternalPurchaseSuccessActivity;
import fun.qianxiao.originalassistant.activity.test.MovingBricksActivity;
import fun.qianxiao.originalassistant.activity.test.ReviseSuccessActivity;
import fun.qianxiao.originalassistant.base.BaseFragment;
import fun.qianxiao.originalassistant.databinding.FragmentTestBinding;

/**
 * TestFragment
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class TestFragment extends BaseFragment<FragmentTestBinding, MainActivity> {

    @Override
    protected void initListener() {
        binding.llInternalPurchaseSuccess.setOnClickListener(v -> ActivityUtils.startActivity(new Intent(activity, InternalPurchaseSuccessActivity.class)));
        binding.llReviseSuccess.setOnClickListener(v -> ActivityUtils.startActivity(new Intent(activity, ReviseSuccessActivity.class)));
        binding.llCrackFailed.setOnClickListener(v -> ActivityUtils.startActivity(new Intent(activity, CrackFailedActivity.class)));
        binding.llMovingBricks.setOnClickListener(v -> ActivityUtils.startActivity(new Intent(activity, MovingBricksActivity.class)));
    }

    @Override
    protected void initData() {
    }
}
