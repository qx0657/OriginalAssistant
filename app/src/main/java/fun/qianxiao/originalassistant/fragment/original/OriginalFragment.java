package fun.qianxiao.originalassistant.fragment.original;

import com.blankj.utilcode.util.LogUtils;

import java.util.List;

import fun.qianxiao.originalassistant.base.BaseFragment;
import fun.qianxiao.originalassistant.bean.AppInfo;
import fun.qianxiao.originalassistant.databinding.FragmentOriginalBinding;
import fun.qianxiao.originalassistant.databinding.FragmentTestBinding;
import fun.qianxiao.originalassistant.utils.AppListTool;

/**
 * TODO
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class OriginalFragment extends BaseFragment<FragmentOriginalBinding> {

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        binding.textView.setText("开发中");
        //List<AppInfo> appInfoList = AppListTool.getAppList(getContext());
    }
}
