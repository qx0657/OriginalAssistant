package fun.qianxiao.originalassistant.fragment.find;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.stx.xhb.androidx.XBanner;

import java.util.ArrayList;
import java.util.List;

import fun.qianxiao.originalassistant.base.BaseActivity;
import fun.qianxiao.originalassistant.base.BaseFragment;
import fun.qianxiao.originalassistant.bean.FindBannerInfo;
import fun.qianxiao.originalassistant.databinding.FragmentFindBinding;
import fun.qianxiao.originalassistant.manager.HLXLinkedMeManager;
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
        binding.xbanner.setOnItemClickListener(new XBanner.OnItemClickListener() {
            @Override
            public void onItemClick(XBanner banner, Object model, View view, int position) {
                FindBannerInfo findBannerInfo = (FindBannerInfo) model;
                if (findBannerInfo.getMode() == FindBannerInfo.MODE.POST) {
                    HLXLinkedMeManager.INSTANCE.gotoPostDetail(findBannerInfo.getPostId(), false);
                } else if (findBannerInfo.getMode() == FindBannerInfo.MODE.URL) {
                    HLXLinkedMeManager.INSTANCE.gotoActionDetail(findBannerInfo.getId(), "");
                }
            }
        });
    }

    @Override
    protected void initData() {
        binding.xbanner.loadImage(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_CENTER);
                Glide.with(view).load(((FindBannerInfo) model).getImageUrl()).into((ImageView) view);
            }
        });
        setActivityData(getTestData());
    }

    private void setActivityData(List<FindBannerInfo> data) {
        if (data.size() == 0) {
            binding.tvNoActivities.setVisibility(View.VISIBLE);
        } else {
            binding.tvNoActivities.setVisibility(View.GONE);
            binding.xbanner.setBannerData(data);
        }
    }

    private List<FindBannerInfo> getTestData() {
        List<FindBannerInfo> list = new ArrayList<>();
        list.add(new FindBannerInfo(415, "http://cdn.u1.huluxia.com/g4/M03/8A/63/rBAAdmRUYSOAMpuDAAEPPeGyuKA551.jpg", "【活动】和平精英五月份水友赛")
                .setMode(FindBannerInfo.MODE.POST)
                .setPostId(52410732));
        list.add(new FindBannerInfo(414, "http://cdn.u1.huluxia.com/g4/M01/85/56/rBAAdmRLbcuABnmLAALh2GyLpoA220.png", "【活动】五一飞行棋"));
        list.add(new FindBannerInfo(413, "http://cdn.u1.huluxia.com/g4/M01/84/81/rBAAdmRKN9OAVCmOAAdZfWClGfI995.png", "【活动】五一劳动节-劳动最光荣"));
        return list;
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
