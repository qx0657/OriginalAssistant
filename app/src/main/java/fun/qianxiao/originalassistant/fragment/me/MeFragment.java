package fun.qianxiao.originalassistant.fragment.me;

import fun.qianxiao.originalassistant.base.BaseActivity;
import fun.qianxiao.originalassistant.base.BaseFragment;
import fun.qianxiao.originalassistant.databinding.FragmentMeBinding;
import fun.qianxiao.originalassistant.databinding.FragmentTestBinding;

/**
 * MeFragment
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class MeFragment<A extends BaseActivity<?>> extends BaseFragment<FragmentMeBinding, A> {

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        binding.textView.setText("开发中");
    }
}
