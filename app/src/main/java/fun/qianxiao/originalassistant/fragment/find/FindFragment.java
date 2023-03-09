package fun.qianxiao.originalassistant.fragment.find;

import fun.qianxiao.originalassistant.base.BaseFragment;
import fun.qianxiao.originalassistant.databinding.FragmentTestBinding;

/**
 * TODO
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class FindFragment extends BaseFragment<FragmentTestBinding> {

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        binding.textView.setText("敬请期待");
    }
}
