package fun.qianxiao.originalassistant.fragment.find;

import android.content.Intent;

import com.blankj.utilcode.util.ToastUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;

import fun.qianxiao.originalassistant.base.BaseActivity;
import fun.qianxiao.originalassistant.base.BaseFragment;
import fun.qianxiao.originalassistant.databinding.FragmentFindBinding;
import fun.qianxiao.originalassistant.utils.HLXUtils;
import fun.qianxiao.originalassistant.utils.ShortCutUtils;

/**
 * FindFragment
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class FindFragment<A extends BaseActivity<?>> extends BaseFragment<FragmentFindBinding, A> {

    @Override
    protected void initListener() {
        binding.llHlxFastStartShortCut.setOnClickListener(v -> {
            new XPopup.Builder(activity)
                    .asBottomList("请选择tab页", new String[]{"首页", "社区", "发现", "我的"}, new OnSelectListener() {
                        @Override
                        public void onSelect(int position, String text) {
                            createHLXFastStartShortCut(position);
                        }
                    })
                    .show();
        });
    }

    @Override
    protected void initData() {
    }

    private void createHLXFastStartShortCut(int tabIndex) {
        Intent intent = HLXUtils.getHlxHomeActivityIntent();
        if (intent == null) {
            ToastUtils.showShort("请检查是否已安装葫芦侠App");
            return;
        }
        intent.putExtra("tab_index", tabIndex);
        ShortCutUtils.create(activity, intent, "葫芦侠快速启动");
    }
}
