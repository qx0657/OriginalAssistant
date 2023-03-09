package fun.qianxiao.originalassistant.fragment.original;

import fun.qianxiao.originalassistant.base.BaseFragment;
import fun.qianxiao.originalassistant.databinding.FragmentTestBinding;

/**
 * TODO
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class OriginalFragment extends BaseFragment<FragmentTestBinding> {

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        binding.textView.setText("开发中");
    }
}
