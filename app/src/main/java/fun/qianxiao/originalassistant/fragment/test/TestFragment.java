package fun.qianxiao.originalassistant.fragment.test;

import fun.qianxiao.originalassistant.base.BaseFragment;
import fun.qianxiao.originalassistant.databinding.FragmentTestBinding;

/**
 * TODO
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class TestFragment extends BaseFragment<FragmentTestBinding> {

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        binding.textView.setText("开发中");
    }
}
