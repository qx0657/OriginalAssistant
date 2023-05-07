package fun.qianxiao.originalassistant.fragment.find;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;
import com.bumptech.glide.Glide;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.CenterListPopupView;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.stx.xhb.androidx.XBanner;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import fun.qianxiao.originalassistant.MainActivity;
import fun.qianxiao.originalassistant.base.BaseActivity;
import fun.qianxiao.originalassistant.base.BaseFragment;
import fun.qianxiao.originalassistant.bean.FindBannerInfo;
import fun.qianxiao.originalassistant.bean.UploadPictureResult;
import fun.qianxiao.originalassistant.databinding.FragmentFindBinding;
import fun.qianxiao.originalassistant.manager.HLXApiManager;
import fun.qianxiao.originalassistant.manager.HLXLinkedMeManager;
import fun.qianxiao.originalassistant.utils.HLXUtils;
import fun.qianxiao.originalassistant.utils.HlxKeyLocal;
import fun.qianxiao.originalassistant.utils.ShortCutUtils;

/**
 * FindFragment
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class FindFragment<A extends BaseActivity<?>> extends BaseFragment<FragmentFindBinding, A> {
    private ActivityResultLauncher<String> pickSingleMediaResultLauncher;

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
        binding.llHlxPictureBed.setOnClickListener(v -> {
            pickSingleMediaResultLauncher.launch("image/*");
        });
    }

    @Override
    protected void initData() {
        pickSingleMediaResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                if (uri != null) {
                    File file = UriUtils.uri2File(uri);
                    LogUtils.i(file);
                    uploadPictures(file);
                }
            }
        });
        initBanner();
        initHlxActivityData();
    }

    private void uploadPictures(File file) {
        String key = HlxKeyLocal.read();
        if (TextUtils.isEmpty(key)) {
            ToastUtils.showShort("未登录");
            return;
        }
        ((MainActivity) activity).openLoadingDialog("上传中");
        HLXApiManager.INSTANCE.uploadPictures(key, Collections.singletonList(file), new HLXApiManager.OnUploadPicturesListener() {
            @Override
            public void onUploadPicturesResult(int code, String errMsg, Map<File, UploadPictureResult> result) {
                ((MainActivity) activity).closeLoadingDialog();
                if (code == HLXApiManager.OnUploadPicturesListener.UPLOAD_ALL_SUCCESS) {
                    if (result.size() == 0) {
                        ToastUtils.showShort("图片上传结果列表为空");
                        return;
                    }
                    String[] resultUrl = new String[result.size()];
                    int i = 0;
                    for (File file : result.keySet()) {
                        UploadPictureResult uploadPictureResult = result.get(file);
                        assert uploadPictureResult != null;
                        resultUrl[i++] = uploadPictureResult.getUrl();
                    }
                    ClipboardUtils.copyText(resultUrl[0]);
                    ToastUtils.showShort("链接已复制至剪贴板");
                } else {
                    ToastUtils.showShort(errMsg);
                }
            }
        });
    }

    private void initBanner() {
        binding.xbanner.loadImage(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_CENTER);
                Glide.with(view).load(((FindBannerInfo) model).getImageUrl()).into((ImageView) view);
            }
        });
    }

    private void initHlxActivityData() {
        HLXApiManager.INSTANCE.getActivityList(new HLXApiManager.OnGetActivityListListener() {
            @Override
            public void onGetActivityList(int code, String errMsg, List<FindBannerInfo> list) {
                if (code == HLXApiManager.OnGetActivityListListener.SUCCESS) {
                    setActivityData(list);
                } else {
                    ToastUtils.showShort(errMsg);
                }
            }
        });
    }

    private void setActivityData(List<FindBannerInfo> data) {
        if (data.size() == 0) {
            binding.tvNoActivities.setVisibility(View.VISIBLE);
        } else {
            binding.tvNoActivities.setVisibility(View.GONE);
            binding.xbanner.setBannerData(data);
        }
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
