package fun.qianxiao.originalassistant.activity.hlxpicbed;

import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.interfaces.OnConfirmListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fun.qianxiao.originalassistant.R;
import fun.qianxiao.originalassistant.activity.hlxpicbed.adapter.HLXPicBedUploadHistoryAdapter;
import fun.qianxiao.originalassistant.base.BaseActivity;
import fun.qianxiao.originalassistant.base.BaseAdapter;
import fun.qianxiao.originalassistant.bean.HLXPicBedUploadHistory;
import fun.qianxiao.originalassistant.bean.UploadPictureResult;
import fun.qianxiao.originalassistant.config.SPConstants;
import fun.qianxiao.originalassistant.databinding.ActivityHlxPictureBedBinding;
import fun.qianxiao.originalassistant.manager.HLXApiManager;
import fun.qianxiao.originalassistant.manager.PermissionManager;
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
        binding.fabHlxPictureBedSelectPicture.setOnClickListener(v -> selectPic());
    }

    private void selectPic() {
        if (!PermissionManager.getInstance().hasRequestReadWritePermission()) {
            PermissionManager.getInstance().requestReadWritePermission();
            return;
        }
        pickSingleMediaResultLauncher.launch("image/*");
    }

    @Override
    protected void initData() {
        showBackIcon();
        setTitle("葫芦侠图床");
        initActivityResultLauncher();
        initRecycleView();
        initHistory();
        showTip();
    }

    private void showTip() {
        if (!SPUtils.getInstance().getBoolean(SPConstants.KEY_HLX_PIC_BED_TIP)) {
            BasePopupView basePopupView = new XPopup.Builder(context)
                    .dismissOnTouchOutside(false)
                    .asConfirm("温馨提示", "该功能依赖您葫芦侠身份认证标识key使用葫芦侠图片上传接口实现，请合理使用该功能，请勿上传非法文件。", "", "不再提示", new OnConfirmListener() {
                        @Override
                        public void onConfirm() {
                            SPUtils.getInstance().put(SPConstants.KEY_HLX_PIC_BED_TIP, true);
                        }
                    }, null, true)
                    .show();
            TextView tvContent = basePopupView.getPopupContentView().findViewById(com.lxj.xpopup.R.id.tv_content);
            tvContent.setGravity(Gravity.START);
        }
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
        adapter.getDataList().clear();
        adapter.getDataList().addAll(list);
        adapter.notifyDataSetChanged();
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
                    ToastUtils.showShort("图片上传成功\n链接已复制至剪贴板");
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

    private void clearHistory() {
        SPUtils.getInstance().remove(SPConstants.KEY_HLX_PIC_BED_UPLOAD_HISTORY);
        initHistory();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_hlx_pic_bed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_item_clear) {
            clearHistory();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
