package fun.qianxiao.originalassistant.activity.hlxpicbed;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.GridLayoutManager;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fun.qianxiao.originalassistant.activity.hlxpicbed.adapter.HLXPicBedUploadHistoryAdapter;
import fun.qianxiao.originalassistant.base.BaseActivity;
import fun.qianxiao.originalassistant.base.BaseAdapter;
import fun.qianxiao.originalassistant.bean.HLXPicBedUploadHistory;
import fun.qianxiao.originalassistant.bean.UploadPictureResult;
import fun.qianxiao.originalassistant.config.SPConstants;
import fun.qianxiao.originalassistant.databinding.ActivityHlxPictureBedBinding;
import fun.qianxiao.originalassistant.manager.HLXApiManager;
import fun.qianxiao.originalassistant.utils.HlxKeyLocal;
import fun.qianxiao.originalassistant.view.RecyclerSpace;
import fun.qianxiao.originalassistant.view.loading.MyLoadingDialog;

/**
 * HLXPictureBedActivity
 *
 * @Author QianXiao
 * @Date 2023/5/8
 */
public class HLXPictureBedActivity extends BaseActivity<ActivityHlxPictureBedBinding> {
    private ActivityResultLauncher<String> pickSingleMediaResultLauncher;
    private MyLoadingDialog loadingDialog;
    private HLXPicBedUploadHistoryAdapter adapter;

    @Override
    protected void initListener() {
        binding.fabHlxPictureBedSelectPicture.setOnClickListener(v -> pickSingleMediaResultLauncher.launch("image/*"));
    }

    @Override
    protected void initData() {
        showBackIcon();
        initActivityResultLauncher();
        initRecycleView();
        initHistory();
    }

    private void initActivityResultLauncher() {
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
    }

    private void initHistory() {
        LinkedHashSet<String> linkedHashSet = getHistory();
        List<HLXPicBedUploadHistory> list = new ArrayList<>();
        for (String s : linkedHashSet) {
            HLXPicBedUploadHistory hlxPicBedUploadHistory = new HLXPicBedUploadHistory();
            UploadPictureResult uploadPictureResult = new UploadPictureResult();
            uploadPictureResult.setUrl(s);
            hlxPicBedUploadHistory.setUploadPictureResult(uploadPictureResult);
            list.add(hlxPicBedUploadHistory);
        }
        adapter.getDataList().addAll(list);
    }

    private void initRecycleView() {
        binding.rvHlxPictureBedUploadHistory.setLayoutManager(new GridLayoutManager(context, 4));
        binding.rvHlxPictureBedUploadHistory.addItemDecoration(new RecyclerSpace(4));
        adapter = new HLXPicBedUploadHistoryAdapter(new ArrayList<>());
        adapter.setItemClickListener(new BaseAdapter.ItemClickListener<HLXPicBedUploadHistory>() {
            @Override
            public void onItemClick(View v, int pos, HLXPicBedUploadHistory bean) {
                ClipboardUtils.copyText(bean.getUploadPictureResult().getUrl());
                ToastUtils.showShort("链接已复制至剪贴板");
            }
        });
        binding.rvHlxPictureBedUploadHistory.setAdapter(adapter);
    }

    private void uploadPictures(File file) {
        String key = HlxKeyLocal.read();
        if (TextUtils.isEmpty(key)) {
            ToastUtils.showShort("未登录");
            return;
        }
        openLoadingDialog("上传中");
        HLXApiManager.INSTANCE.uploadPictures(key, Collections.singletonList(file), new HLXApiManager.OnUploadPicturesListener() {
            @Override
            public void onUploadPicturesResult(int code, String errMsg, Map<File, UploadPictureResult> result) {
                closeLoadingDialog();
                if (code == HLXApiManager.OnUploadPicturesListener.UPLOAD_ALL_SUCCESS) {
                    if (result.size() == 0) {
                        ToastUtils.showShort("图片上传结果列表为空");
                        return;
                    }
                    List<UploadPictureResult> uploadPictureResults = new ArrayList<>(result.values());
                    UploadPictureResult pictureResult = uploadPictureResults.get(0);
                    ClipboardUtils.copyText(pictureResult.getUrl());
                    ToastUtils.showShort("链接已复制至剪贴板");
                    addHistory(file, pictureResult);
                } else {
                    ToastUtils.showShort(errMsg);
                }
            }
        });
    }

    private LinkedHashSet<String> getHistory() {
        Set<String> history = SPUtils.getInstance().getStringSet(SPConstants.KEY_HLX_PIC_BED_UPLOAD_HISTORY);
        return new LinkedHashSet<>(history);
    }

    private void addHistory(File file, UploadPictureResult pictureResult) {
        /*
        Currently using SP to save Url set, planning to use database in the future.
         */
        LinkedHashSet<String> linkedHashSet = getHistory();
        linkedHashSet.add(pictureResult.getUrl());
        SPUtils.getInstance().put(SPConstants.KEY_HLX_PIC_BED_UPLOAD_HISTORY, linkedHashSet);

        HLXPicBedUploadHistory hlxPicBedUploadHistory = new HLXPicBedUploadHistory();
        hlxPicBedUploadHistory.setFilePath(file.toString());
        hlxPicBedUploadHistory.setUploadPictureResult(pictureResult);
        adapter.getDataList().add(hlxPicBedUploadHistory);
        adapter.notifyItemInserted(adapter.getDataList().size() - 1);
        adapter.notifyDataSetChanged();
    }

    public void openLoadingDialog(String msg) {
        if (loadingDialog == null) {
            loadingDialog = new MyLoadingDialog(context);
        }
        if (!loadingDialog.isShowing()) {
            loadingDialog.setMessage(msg);
            loadingDialog.show();
        }
    }

    public void closeLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}
