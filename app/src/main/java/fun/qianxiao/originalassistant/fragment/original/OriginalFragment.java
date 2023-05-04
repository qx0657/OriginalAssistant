package fun.qianxiao.originalassistant.fragment.original;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SimpleAdapter;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;
import com.bumptech.glide.Glide;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import fun.qianxiao.originalassistant.MainActivity;
import fun.qianxiao.originalassistant.R;
import fun.qianxiao.originalassistant.activity.selectapp.SelectAppActivity;
import fun.qianxiao.originalassistant.appquery.IQuery;
import fun.qianxiao.originalassistant.base.BaseActivity;
import fun.qianxiao.originalassistant.base.BaseFragment;
import fun.qianxiao.originalassistant.bean.AppQueryResult;
import fun.qianxiao.originalassistant.bean.PostInfo;
import fun.qianxiao.originalassistant.bean.PostResultInfo;
import fun.qianxiao.originalassistant.bean.UploadPictureResult;
import fun.qianxiao.originalassistant.config.Constants;
import fun.qianxiao.originalassistant.config.SPConstants;
import fun.qianxiao.originalassistant.databinding.FragmentOriginalBinding;
import fun.qianxiao.originalassistant.fragment.original.adapter.AppPicturesAdapter;
import fun.qianxiao.originalassistant.manager.AppQueryManager;
import fun.qianxiao.originalassistant.manager.HLXApiManager;
import fun.qianxiao.originalassistant.manager.PermissionManager;
import fun.qianxiao.originalassistant.manager.TranslateManager;
import fun.qianxiao.originalassistant.translate.ITranslate;
import fun.qianxiao.originalassistant.utils.HLXUtils;
import fun.qianxiao.originalassistant.utils.HlxKeyLocal;
import fun.qianxiao.originalassistant.utils.PictureFileUtils;
import fun.qianxiao.originalassistant.utils.PostContentFormatUtils;
import fun.qianxiao.originalassistant.utils.SettingPreferences;
import fun.qianxiao.originalassistant.view.RecyclerSpace;

/**
 * OriginalFragment
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class OriginalFragment<A extends BaseActivity<?>> extends BaseFragment<FragmentOriginalBinding, A>
        implements AppPicturesAdapter.OnAppPicturesAdapterListener {
    private final int MIN_TITLE_LENGTH = 5;
    private final int MAX_TITLE_LENGTH = 32;
    private final int MAX_DETAIL_LENGTH = 10000;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ActivityResultLauncher<String> pickMultipleMediaResultLauncher;
    private AtomicBoolean isAppQuerying = new AtomicBoolean(false);
    private AppPicturesAdapter picturesAdapter;

    private final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int dragFlags = 0;
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            } else if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            }
            return makeMovementFlags(dragFlags, 0);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            //得到当拖拽的viewHolder的Position
            int fromPosition = viewHolder.getBindingAdapterPosition();
            //拿到当前拖拽到的item的viewHolder
            int toPosition = target.getBindingAdapterPosition();
            if (fromPosition == picturesAdapter.getItemCount() - 1 || toPosition == picturesAdapter.getItemCount() - 1) {
                return false;
            }
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(picturesAdapter.getDataList(), i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(picturesAdapter.getDataList(), i, i - 1);
                }
            }
            picturesAdapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    });

    private final int APP_QUERY_NOT_AUTO = -3;
    private final int APP_QUERY_MANUAL = -2;
    private final int APP_QUERY_AUTO_ALL = -1;

    @Override
    protected void initListener() {
        setScrollEditTextListener();
        setFloatingActionButtonListener();
        setSpecialInstructionsSpinnerListener();
        setSpecialInstructionsEditTextChangeListener();

        binding.ivAppPictureDelete.setOnClickListener(v -> {
            if (picturesAdapter != null && picturesAdapter.getItemCount() > 1) {
                setAppPicturesOptionMode(!picturesAdapter.isShowDelete(), true);
            }
        });

        KeyboardUtils.registerSoftInputChangedListener(activity, height -> {
            if (height != 0) {
                ((MainActivity) activity).setTabNavigationHide(true);
                binding.famOriginal.collapse();
                binding.famOriginal.setVisibility(View.GONE);
            } else {
                ((MainActivity) activity).setTabNavigationHide(false);
                ThreadUtils.runOnUiThreadDelayed(() -> binding.famOriginal.setVisibility(View.VISIBLE), 50);
            }
        });
    }

    private void clearAppPictureRecycleView() {
        picturesAdapter = new AppPicturesAdapter(this, new ArrayList<>(Collections.singletonList(AppPicturesAdapter.PLACEHOLDER_ADD)));
        binding.rvAppPics.setAdapter(picturesAdapter);
        picturesAdapter.notifyDataSetChanged();
    }

    private void setSpecialInstructionsEditTextChangeListener() {
        binding.etSpecialInstructions.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    binding.etSpecialInstructions.setTag(null);
                }
            }
        });
    }

    private void setSpecialInstructionsSpinnerListener() {
        binding.spinnerSpecialInstructionsSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    String[] s = getSpecialInstructionTexts();
                    /*
                    Use tag of view to save the added to prevent repeated addition
                     */
                    Object tag = binding.etSpecialInstructions.getTag();
                    String tagStr = "";
                    if (tag != null) {
                        tagStr = (String) tag;
                        String[] hasAdded = tagStr.split("#");
                        for (String s1 : hasAdded) {
                            if (Integer.parseInt(s1) == position) {
                                binding.spinnerSpecialInstructionsSelect.setSelection(0);
                                return;
                            }
                        }
                    }
                    CharSequence textOriginal = binding.etSpecialInstructions.getText();
                    binding.etSpecialInstructions.requestFocus();
                    if (TextUtils.isEmpty(textOriginal)) {
                        binding.etSpecialInstructions.setText(s[position]);
                    } else {
                        binding.etSpecialInstructions.setText(textOriginal + "、" + s[position]);
                    }
                    if (TextUtils.isEmpty(tagStr)) {
                        tagStr = String.valueOf(position);
                    } else {
                        tagStr = tagStr + "#" + position;
                    }
                    binding.etSpecialInstructions.setTag(tagStr);
                    binding.etSpecialInstructions.setSelection(binding.etSpecialInstructions.getText().length());
                    binding.spinnerSpecialInstructionsSelect.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.tilSpecialInstructions.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.etSpecialInstructions.setText("");
                binding.etSpecialInstructions.setTag(null);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setScrollEditTextListener() {
        View.OnTouchListener editTextScrollListener = (view, motionEvent) -> {
            EditText editText = (EditText) view;
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN && view.hasFocus() &&
                    editText.getLineCount() > editText.getMaxLines()) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                view.getParent().requestDisallowInterceptTouchEvent(false);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                view.getParent().requestDisallowInterceptTouchEvent(false);
            }
            return false;
        };
        binding.etSpecialInstructions.setOnTouchListener(editTextScrollListener);
        binding.etGameIntroduction.setOnTouchListener(editTextScrollListener);
        binding.etDownloadUrl.setOnTouchListener(editTextScrollListener);
    }

    private void setFloatingActionButtonListener() {
        binding.fabSelectApp.setOnClickListener(view -> {
            binding.famOriginal.collapse();
            ThreadUtils.runOnUiThreadDelayed(this::selectApp, 100);
        });
        binding.fabCleanContent.setOnClickListener(view -> {
            binding.famOriginal.collapse();
            cleanAllInputContent(true);
        });
        binding.fabCopyContent.setOnClickListener(view -> {
            binding.famOriginal.collapse();
            copyContent();
        });
        binding.fabGotoApp.setOnClickListener(view -> {
            binding.famOriginal.collapse();
            if (SettingPreferences.getBoolean(R.string.p_key_switch_post_one_key)) {
                oneKeyPost();
            } else {
                gotoApp();
            }
        });
    }

    private boolean checkAllPostContent() {
        if (TextUtils.isEmpty(binding.etGameName.getText().toString())) {
            binding.etGameName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(binding.etGamePackageName.getText().toString())) {
            binding.etGamePackageName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(binding.etGameSize.getText().toString())) {
            binding.etGameSize.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(binding.etGameVersion.getText().toString())) {
            binding.etGameVersion.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(binding.etGameVersionCode.getText().toString())) {
            binding.etGameVersionCode.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(binding.etSpecialInstructions.getText().toString())) {
            binding.etSpecialInstructions.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(binding.etGameIntroduction.getText().toString())) {
            binding.etGameIntroduction.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(binding.etDownloadUrl.getText().toString())) {
            binding.etDownloadUrl.requestFocus();
            return false;
        }
        return true;
    }

    private List<File> imagesToFileList(List<String> picsData) {
        List<File> list = new ArrayList<>();
        for (String picsDatum : picsData) {
            if (picsDatum.equals(AppPicturesAdapter.PLACEHOLDER_ADD)) {
                continue;
            }
            if (picsDatum.startsWith("http")) {
                File file;
                try {
                    file = Glide.with(activity).asFile().load(picsDatum).submit().get();
                    File newFile = new File(file.getParent() + File.separator + file.getName() + ".jpg");
                    if (FileUtils.isFileExists(newFile) || FileUtils.rename(file, newFile.getName())) {
                        list.add(newFile);
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                list.add(new File(picsDatum));
            }
        }
        return list;
    }

    private boolean checkTitle(PostInfo postInfo) {
        String title = PostContentFormatUtils.getFormatTitle(postInfo);
        if (title.length() < MIN_TITLE_LENGTH) {
            ToastUtils.showShort("标题过短（" + title.length() + "/" + MIN_TITLE_LENGTH + "）\n" +
                    "\"" + title + "\"");
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            ToastUtils.showShort("标题过长（" + title.length() + "/" + MAX_TITLE_LENGTH + "）\n" +
                    "\"" + title + "\"");
            return false;
        }
        return true;
    }

    private void oneKeyPost() {
        String key = HlxKeyLocal.read();
        if (TextUtils.isEmpty(key)) {
            ToastUtils.showShort("未登录");
            return;
        }
        if (!checkAllPostContent()) {
            ToastUtils.showShort("发帖内容不完整");
            return;
        }
        PostInfo postInfo = getPostInfo();
        if (!checkTitle(postInfo)) {
            return;
        }
        if (picturesAdapter.getItemCount() - 1 == 0) {
            ToastUtils.showShort("请选择图片");
            return;
        }
        HLXApiManager.INSTANCE.checkKey(key, new HLXApiManager.OnCommonBooleanResultListener() {
            @Override
            public void onResult(boolean valid, String errMsg) {
                if (valid) {
                    ((MainActivity) activity).openLoadingDialog("发帖中");
                    ThreadUtils.executeBySingle(new ThreadUtils.SimpleTask<List<File>>() {
                        @Override
                        public List<File> doInBackground() throws Throwable {
                            List<String> picsData = picturesAdapter.getDataList();
                            return imagesToFileList(picsData);
                        }

                        @Override
                        public void onSuccess(List<File> picsFiles) {
                            LogUtils.i(picsFiles);
                            if (picsFiles.size() == 0) {
                                ((MainActivity) activity).closeLoadingDialog();
                                ToastUtils.showShort("待上传图片列表为空");
                                return;
                            }
                            HLXApiManager.INSTANCE.uploadPictures(key, picsFiles, new HLXApiManager.OnUploadPicturesListener() {
                                @Override
                                public void onUploadPicturesResult(int code, String errMsg, Map<File, UploadPictureResult> result) {
                                    if (code == HLXApiManager.OnUploadPicturesListener.UPLOAD_ALL_SUCCESS) {
                                        if (result.size() == 0) {
                                            ((MainActivity) activity).closeLoadingDialog();
                                            ToastUtils.showShort("图片上传结果列表为空");
                                            return;
                                        }
                                        oneKeyPostInner(postInfo, key, result);
                                    } else {
                                        ((MainActivity) activity).closeLoadingDialog();
                                        ToastUtils.showShort(errMsg);
                                    }
                                }
                            });
                        }
                    });
                } else {
                    ToastUtils.showShort(errMsg);
                }
            }
        });
    }

    private String getNoRichDetailText(PostInfo postInfo) {
        StringBuilder detail = new StringBuilder("<text>" + PostContentFormatUtils.getFormatDetail(postInfo) + "</text>");
        String postPrefix = SettingPreferences.getString(R.string.p_key_post_prefix);
        if (!TextUtils.isEmpty(postPrefix)) {
            detail.insert(0, "<text>" + postPrefix + "</text>" + "<text>\n</text>");
        }
        String postSuffix = SettingPreferences.getString(R.string.p_key_post_suffix);
        if (!TextUtils.isEmpty(postSuffix)) {
            detail.append("<text>\n</text>").append("<text>").append(postSuffix).append("</text>");
        }
        return detail.toString();
    }

    private String getNoRichImagesText(Map<File, UploadPictureResult> picUploadResultMap) {
        StringBuilder stringBuilder = new StringBuilder();
        for (File file : picUploadResultMap.keySet()) {
            stringBuilder.append(Objects.requireNonNull(picUploadResultMap.get(file)).getFid());
            stringBuilder.append(",");
        }
        return stringBuilder.toString();
    }

    private String getRichDetailText(PostInfo postInfo, Map<File, UploadPictureResult> picUploadResultMap) {
        StringBuilder detail = new StringBuilder("<text>" + PostContentFormatUtils.getFormatDetail(postInfo) + "</text>");
        String postPrefix = SettingPreferences.getString(R.string.p_key_post_prefix);
        if (!TextUtils.isEmpty(postPrefix)) {
            if (postPrefix.startsWith("<text>")) {
                detail.insert(0, postPrefix);
            } else {
                detail.insert(0, "<text>" + postPrefix + "</text>");
            }
        }
        detail.append("<text></text>");
        for (File file : picUploadResultMap.keySet()) {
            Pair<Integer, Integer> wh = PictureFileUtils.getWidthAndHeight(file.toString());
            detail.append("<image>").append(Objects.requireNonNull(picUploadResultMap.get(file)).getFid()).append(",").append(wh.first).append(",").append(wh.second).append("</image>");
            detail.append("<text></text>");
        }
        String postSuffix = SettingPreferences.getString(R.string.p_key_post_suffix);
        if (!TextUtils.isEmpty(postSuffix)) {
            if (postSuffix.startsWith("<text>")) {
                detail.append(postSuffix);
            } else {
                detail.append("<text>").append(postSuffix).append("</text>");
            }
        }
        return detail.toString();
    }

    private boolean checkDetail(String detail) {
        String s = detail.replaceAll("<text>", "")
                .replaceAll("</text>", "")
                .replaceAll("<image>(.*)<image>", "");
        if (s.length() > MAX_DETAIL_LENGTH) {
            ToastUtils.showShort("发帖内容过长（" + s.length() + "/" + MAX_DETAIL_LENGTH + "）");
            return false;
        }
        return true;
    }

    private void oneKeyPostInner(PostInfo postInfo, String key, Map<File, UploadPictureResult> picUploadResultMap) {
        boolean isRich = SettingPreferences.getBoolean(R.string.p_key_switch_post_rich);
        String title = PostContentFormatUtils.getFormatTitle(postInfo);
        String detail = "";
        String images = "";
        LogUtils.i(picUploadResultMap);
        if (isRich) {
            detail = getRichDetailText(postInfo, picUploadResultMap);
            images = "";
        } else {
            detail = getNoRichDetailText(postInfo);
            images = getNoRichImagesText(picUploadResultMap);
        }
        if (!checkDetail(detail)) {
            ((MainActivity) activity).closeLoadingDialog();
            return;
        }
        HLXApiManager.INSTANCE.post(key, title, detail, images, isRich, new HLXApiManager.OnPostListener() {
            @Override
            public void onSuccess(PostResultInfo postResultInfo) {
                ((MainActivity) activity).closeLoadingDialog();
                new XPopup.Builder(getContext())
                        .dismissOnBackPressed(false)
                        .dismissOnTouchOutside(false)
                        .asConfirm("发帖结果", postResultInfo.getMsg()
                                , () -> gotoApp())
                        .setConfirmText("跳转葫芦侠")
                        .setCancelText("确定")
                        .show();
            }

            @Override
            public void onError(int code, String errMsg) {
                ((MainActivity) activity).closeLoadingDialog();
                ToastUtils.showShort(errMsg);
            }
        });
    }

    private void selectApp() {
        activityResultLauncher.launch(new Intent(activity, SelectAppActivity.class));
    }

    private void cleanAllInputContent(boolean isCleanSpecialInstructions) {
        binding.etGameName.setText("");
        binding.etGamePackageName.setText("");
        binding.etGameSize.setText("");
        binding.etGameVersion.setText("");
        binding.etGameVersionCode.setText("");
        if (isCleanSpecialInstructions) {
            binding.etSpecialInstructions.setText("");
        }
        binding.etGameIntroduction.setText("");
        binding.etDownloadUrl.setText("");

        clearAppPictureRecycleView();
    }

    private int getLanguageIndex() {
        int checkedId = binding.rgLanguageRadioButtonGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.rb_game_language_chinese_game) {
            return PostInfo.AppLanguage.CHINESE.ordinal();
        } else if (checkedId == R.id.rb_game_language_english_game) {
            return PostInfo.AppLanguage.ENGLISH.ordinal();
        }
        return PostInfo.AppLanguage.OTHER.ordinal();
    }

    private PostInfo getPostInfo() {
        PostInfo postInfo = new PostInfo();
        postInfo.setAppName(binding.etGameName.getText());
        postInfo.setAppPackageName(binding.etGamePackageName.getText());
        postInfo.setAppSize(binding.etGameSize.getText());
        postInfo.setAppVersionName(binding.etGameVersion.getText());
        postInfo.setAppVersionCode(binding.etGameVersionCode.getText());
        postInfo.setAppLanguage(PostInfo.AppLanguage.values()[getLanguageIndex()]);
        postInfo.setAppSpecialInstructions(binding.etSpecialInstructions.getText());
        postInfo.setAppIntroduction(binding.etGameIntroduction.getText());
        postInfo.setAppDownloadUrl(binding.etDownloadUrl.getText());

        return postInfo;
    }

    private void copyContent() {
        PostInfo postInfo = getPostInfo();

        StringBuilder stringBuilder = new StringBuilder();
        CharSequence title = PostContentFormatUtils.getFormatTitle(postInfo);
        if (!TextUtils.isEmpty(title)) {
            stringBuilder.append(title).append(PostContentFormatUtils.FIELD_SEPARATOR);
        }
        String postPrefix = SettingPreferences.getString(R.string.p_key_post_prefix);
        if (!TextUtils.isEmpty(postPrefix)) {
            stringBuilder.append(postPrefix).append(PostContentFormatUtils.FIELD_SEPARATOR);
        }
        CharSequence detail = PostContentFormatUtils.getFormatDetail(postInfo);
        stringBuilder.append(detail);
        String postSuffix = SettingPreferences.getString(R.string.p_key_post_suffix);
        if (!TextUtils.isEmpty(postSuffix)) {
            stringBuilder.append(PostContentFormatUtils.FIELD_SEPARATOR).append(postSuffix);
        }

        ClipboardUtils.copyText(stringBuilder.toString());
        ToastUtils.showShort("已复制到剪贴板");
    }

    private void gotoApp() {
        HLXUtils.gotoHLX();
    }

    @Override
    protected void initData() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int resultCode = result.getResultCode();
                Intent data = result.getData();
                if (resultCode == SelectAppActivity.RESULT_CODE_SELECT_APP_OK) {
                    if (data != null) {
                        cleanAllInputContent(false);
                        try {
                            String appName = data.getStringExtra(SelectAppActivity.KEY_APP_NAME);
                            String appPackageName = data.getStringExtra(SelectAppActivity.KEY_APP_PACKAGE_NAME);
                            binding.etGameName.setText(appName);
                            binding.etGamePackageName.setText(appPackageName);
                            PackageManager packageManager = activity.getPackageManager();
                            PackageInfo packageInfo = packageManager.getPackageInfo(appPackageName, 0);
                            binding.etGameVersion.setText(packageInfo.versionName);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                binding.etGameVersionCode.setText(String.valueOf(packageInfo.getLongVersionCode()));
                            } else {
                                binding.etGameVersionCode.setText(String.valueOf(packageInfo.versionCode));
                            }
                            binding.etGameSize.setText(ConvertUtils.byte2FitMemorySize(
                                    FileUtils.getFileLength(packageInfo.applicationInfo.sourceDir), 2));
                            queryAppInfo(appName, appPackageName);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        pickMultipleMediaResultLauncher = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), new ActivityResultCallback<List<Uri>>() {
            @Override
            public void onActivityResult(List<Uri> result) {
                if (result != null && result.size() > 0) {
                    for (Uri uri : result) {
                        File file = UriUtils.uri2File(uri);
                        LogUtils.i(file);
                        picturesAdapter.getDataList().add(picturesAdapter.getDataList().size() - 1, file.toString());
                        picturesAdapter.notifyItemInserted(picturesAdapter.getDataList().size() - 1);
                        picturesAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        initAppPicturesRecycleView();
        initAppMode();
    }

    private void initAppMode() {
        setAppMode(SPUtils.getInstance().getInt(SPConstants.KEY_APP_MODE, Constants.APP_MODE_GAME));
    }

    private void initFloatButtonData() {
        if (SettingPreferences.getBoolean(R.string.p_key_switch_post_one_key)) {
            binding.fabGotoApp.setTitle("发帖");
        } else {
            binding.fabGotoApp.setTitle("跳转");
        }
    }

    private void initAppPicturesRecycleView() {
        binding.rvAppPics.setLayoutManager(new GridLayoutManager(getContext(), 4));
        binding.rvAppPics.addItemDecoration(new RecyclerSpace(4));
        clearAppPictureRecycleView();
    }

    private void autoAppQuery(String appName, String packageName, IQuery.OnAppQueryListener onAppQueryListener) {
        isAppQuerying.set(true);
        ThreadUtils.executeBySingle(new ThreadUtils.SimpleTask<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                AppQueryManager.getInstance().query(appName, packageName, onAppQueryListener);
                return null;
            }

            @Override
            public void onSuccess(Object result) {

            }
        });
    }

    private IQuery.OnAppQueryListener getOnAppQueryListener() {
        return new IQuery.OnAppQueryListener() {
            @Override
            public void onResult(int code, String message, AppQueryResult appQueryResult) {
                isAppQuerying.set(false);
                LogUtils.i(code, message,
                        appQueryResult == null ? "appQueryResult null" : appQueryResult.getFromChannel(),
                        appQueryResult == null ? "appQueryResult null" : appQueryResult.getAppIntroduction(),
                        appQueryResult == null ? "appQueryResult null" : appQueryResult.getAppPictures());
                if (code == IQuery.OnAppQueryListener.QUERY_CODE_SUCCESS) {
                    if (appQueryResult == null) {
                        return;
                    }
                    String introduction = appQueryResult.getAppIntroduction();
                    int introductionLengthMax = Integer.parseInt(SettingPreferences.getString(R.string.p_key_get_introduction_length_max, "1200"));
                    if (introduction.length() > introductionLengthMax) {
                        introduction = introduction.substring(0, introductionLengthMax);
                    }
                    binding.etGameIntroduction.setText(introduction);
                    if (appQueryResult.getAppPictures() != null && appQueryResult.getAppPictures().size() > 0) {
                        int downloadPictureNum = Integer.parseInt(SettingPreferences.getString(R.string.p_key_download_picture_num, "5"));
                        if (appQueryResult.getAppPictures().size() > downloadPictureNum) {
                            appQueryResult.setAppPictures(appQueryResult.getAppPictures().subList(0, downloadPictureNum));
                        }
                        appQueryResult.getAppPictures().add(AppPicturesAdapter.PLACEHOLDER_ADD);
                        picturesAdapter = new AppPicturesAdapter(OriginalFragment.this, appQueryResult.getAppPictures());
                        binding.rvAppPics.setAdapter(picturesAdapter);
                        picturesAdapter.notifyDataSetChanged();
                    }
                } else {
                    ToastUtils.showShort(message);
                }
            }
        };
    }

    private void queryAppInfo(String appName, String packageName) {
        int appQueryChannel = Integer.parseInt(SettingPreferences.getString(R.string.p_key_app_query_channel, String.valueOf(APP_QUERY_AUTO_ALL)));
        if (appQueryChannel == APP_QUERY_NOT_AUTO) {
            return;
        }
        if (isAppQuerying.get()) {
            return;
        }
        ThreadUtils.executeBySingle(new ThreadUtils.SimpleTask<Boolean>() {
            @Override
            public Boolean doInBackground() throws Throwable {
                return NetworkUtils.isAvailable();
            }

            @Override
            public void onSuccess(Boolean result) {
                if (!result) {
                    ToastUtils.showShort("请检查网络连接");
                    return;
                }
                IQuery.OnAppQueryListener onAppQueryListener = getOnAppQueryListener();
                if (appQueryChannel == APP_QUERY_MANUAL) {
                    manualAppQQueryDialog(appName, packageName);
                } else if (appQueryChannel == APP_QUERY_AUTO_ALL) {
                    autoAppQuery(appName, packageName, onAppQueryListener);
                } else {
                    isAppQuerying.set(true);
                    AppQueryManager.createQuerier(AppQueryManager.AppQueryChannel.values()[appQueryChannel].getChannel())
                            .query(appName, packageName, onAppQueryListener);
                }
            }
        });
    }

    private void initSpecialInstructionsSpinner() {
        if (!TextUtils.isEmpty(SettingPreferences.getString(R.string.p_key_special_instructions))) {
            List<Map<String, String>> data = new ArrayList<>();
            Map<String, String> specialInstructionsMap = new LinkedHashMap<>();
            specialInstructionsMap.put("text", "");
            for (String s : getSpecialInstructionTexts()) {
                specialInstructionsMap = new LinkedHashMap<>();
                specialInstructionsMap.put("text", s);
                data.add(specialInstructionsMap);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), data, android.R.layout.simple_spinner_dropdown_item,
                    new String[]{"text"},
                    new int[]{android.R.id.text1});
            binding.spinnerSpecialInstructionsSelect.setDropDownWidth(ScreenUtils.getAppScreenWidth());
            binding.spinnerSpecialInstructionsSelect.setAdapter(simpleAdapter);
        }
    }

    private String[] getSpecialInstructionTexts() {
        if (TextUtils.isEmpty(SettingPreferences.getString(R.string.p_key_special_instructions))) {
            return getResources().getStringArray(R.array.special_instructions);
        }
        String pKeySpecialInstructions = SettingPreferences.getString(R.string.p_key_special_instructions);
        String[] strings = pKeySpecialInstructions.split("\n");
        String[] stringsRes = new String[strings.length + 1];
        stringsRes[0] = "";
        for (int i = 0; i < strings.length; i++) {
            stringsRes[i + 1] = strings[i];
        }
        return stringsRes;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        activity.getMenuInflater().inflate(R.menu.menu_original, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItemAppModeGame = menu.findItem(R.id.menu_item_app_mode_game);
        MenuItem menuItemAppModeSoftware = menu.findItem(R.id.menu_item_app_mode_software);
        int appMode = SPUtils.getInstance().getInt(SPConstants.KEY_APP_MODE, Constants.APP_MODE_GAME);
        if (appMode == Constants.APP_MODE_GAME) {
            menuItemAppModeGame.setChecked(true);
        } else {
            menuItemAppModeSoftware.setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_item_translate) {
            String text = binding.etGameIntroduction.getText().toString();
            if (!TextUtils.isEmpty(text)) {
                translateIntroduction(text);
            } else {
                ToastUtils.showShort("应用介绍为空");
            }
            return true;
        } else if (item.getItemId() == R.id.menu_item_app_query) {
            String appName = binding.etGameName.getText().toString();
            String packageName = binding.etGamePackageName.getText().toString();
            if (!TextUtils.isEmpty(appName) && !TextUtils.isEmpty(packageName)) {
                manualAppQQueryDialog(appName, packageName);
                return true;
            } else {
                ToastUtils.showShort("应用名和包名不能为空");
            }
        } else if (item.getItemId() == R.id.menu_item_app_mode_game) {
            SPUtils.getInstance().put(SPConstants.KEY_APP_MODE, Constants.APP_MODE_GAME);
            item.setChecked(true);
            setAppMode(Constants.APP_MODE_GAME);
            return true;
        } else if (item.getItemId() == R.id.menu_item_app_mode_software) {
            SPUtils.getInstance().put(SPConstants.KEY_APP_MODE, Constants.APP_MODE_SOFTWARE);
            item.setChecked(true);
            setAppMode(Constants.APP_MODE_SOFTWARE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAppMode(int mode) {
        if (mode == Constants.APP_MODE_GAME) {
            binding.tlGameName.setHint("游戏名称");
            binding.tlGamePackageName.setHint("游戏包名");
            binding.tlGameSize.setHint("游戏大小");
            binding.tlGameVersion.setHint("游戏版本");
            binding.tlGameVersionCode.setHint("游戏版本值");
            binding.tlGameIntroduction.setHint("游戏介绍");
            binding.rbGameLanguageChineseGame.setText("中文游戏");
            binding.rbGameLanguageEnglishGame.setText("英文游戏");
        } else if (mode == Constants.APP_MODE_SOFTWARE) {
            binding.tlGameName.setHint("软件名称");
            binding.tlGamePackageName.setHint("软件包名");
            binding.tlGameSize.setHint("软件大小");
            binding.tlGameVersion.setHint("软件版本");
            binding.tlGameVersionCode.setHint("软件版本值");
            binding.tlGameIntroduction.setHint("软件介绍");
            binding.rbGameLanguageChineseGame.setText("中文软件");
            binding.rbGameLanguageEnglishGame.setText("英文软件");
        }
    }

    private void manualAppQQueryDialog(String appName, String packageName) {
        if (isAppQuerying.get()) {
            ToastUtils.showShort("正在获取中");
            return;
        }
        AppQueryManager.AppQueryChannel[] appQueryChannels = AppQueryManager.AppQueryChannel.values();
        String[] strings = new String[appQueryChannels.length];
        int[] icons = new int[appQueryChannels.length];
        for (int i = 0; i < appQueryChannels.length; i++) {
            strings[i] = appQueryChannels[i].getCommonName();
            icons[i] = appQueryChannels[i].getIconRes();
        }
        new XPopup.Builder(getContext())
                .asCenterList("获取应用信息", strings, icons, new OnSelectListener() {
                    @Override
                    public void onSelect(int position, String text) {
                        AppQueryManager.createQuerier(appQueryChannels[position].getChannel())
                                .query(appName, packageName, getOnAppQueryListener());
                    }
                })
                .show();
    }

    private ITranslate.OnTranslateListener getOnTranslateListener() {
        return new ITranslate.OnTranslateListener() {
            @Override
            public void onTranslateResult(int code, String msg, String result) {
                if (code == ITranslate.OnTranslateListener.TRANSLATE_SUCCESS) {
                    binding.etGameIntroduction.setText(result);
                    binding.etGameIntroduction.setSelection(binding.etGameIntroduction.getText().length());
                } else {
                    ToastUtils.showShort(msg);
                }
            }
        };
    }

    private void translateIntroduction(String text) {
        int transApi = Integer.parseInt(SettingPreferences.getString(R.string.p_key_current_translate_api, "0"));
        ITranslate.OnTranslateListener onTranslateListener = getOnTranslateListener();
        if (transApi == -1) {
            ThreadUtils.executeBySingle(new ThreadUtils.SimpleTask<Object>() {
                @Override
                public Object doInBackground() throws Throwable {
                    TranslateManager.getInstance().translate(text, onTranslateListener);
                    return null;
                }

                @Override
                public void onSuccess(Object result) {

                }
            });
        } else {
            TranslateManager.createTranslater(TranslateManager.TranslateInterfaceType.values()[transApi].getChannel())
                    .translate(text, onTranslateListener);
        }
    }

    @Override
    public boolean onBackPressed() {
        if (binding.famOriginal.isExpanded()) {
            binding.famOriginal.collapse();
            return true;
        }
        return super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        KeyboardUtils.unregisterSoftInputChangedListener(activity.getWindow());
        super.onDestroy();
    }

    @Override
    public void onAddClick(View view) {
        if (!PermissionManager.getInstance().hasRequestReadWritePermission()) {
            PermissionManager.getInstance().requestReadWritePermission();
            return;
        }
        setAppPicturesOptionMode(false, false);
        pickMultipleMediaResultLauncher.launch("image/*");
    }

    private void setAppPicturesOptionMode(boolean mode, boolean isNotifyDataChange) {
        if (mode) {
            binding.ivAppPictureDelete.setImageResource(R.drawable.ic_complate);
        } else {
            binding.ivAppPictureDelete.setImageResource(R.drawable.ic_edit);
        }
        picturesAdapter.setShowDelete(mode);
        if (isNotifyDataChange) {
            picturesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDataChange(boolean empty) {
        if (empty) {
            setAppPicturesOptionMode(false, false);
            binding.ivAppPictureDelete.setVisibility(View.GONE);
            itemTouchHelper.attachToRecyclerView(null);
        } else {
            binding.ivAppPictureDelete.setVisibility(View.VISIBLE);
            itemTouchHelper.attachToRecyclerView(binding.rvAppPics);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initSpecialInstructionsSpinner();
        initFloatButtonData();
    }
}
