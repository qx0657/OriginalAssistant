package fun.qianxiao.originalassistant.fragment.original;

import static fun.qianxiao.originalassistant.config.AppConfig.HULUXIA_APP_PACKAGE_NAME;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.recyclerview.widget.GridLayoutManager;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.IntentUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import fun.qianxiao.originalassistant.MainActivity;
import fun.qianxiao.originalassistant.R;
import fun.qianxiao.originalassistant.activity.selectapp.SelectAppActivity;
import fun.qianxiao.originalassistant.appquery.IQuery;
import fun.qianxiao.originalassistant.base.BaseActivity;
import fun.qianxiao.originalassistant.base.BaseFragment;
import fun.qianxiao.originalassistant.bean.AppQueryResult;
import fun.qianxiao.originalassistant.bean.PostInfo;
import fun.qianxiao.originalassistant.config.Constants;
import fun.qianxiao.originalassistant.config.SPConstants;
import fun.qianxiao.originalassistant.databinding.FragmentOriginalBinding;
import fun.qianxiao.originalassistant.fragment.original.adapter.AppPicturesAdapter;
import fun.qianxiao.originalassistant.manager.AppQueryMannager;
import fun.qianxiao.originalassistant.manager.HLXApiManager;
import fun.qianxiao.originalassistant.manager.TranslateManager;
import fun.qianxiao.originalassistant.translate.ITranslate;
import fun.qianxiao.originalassistant.utils.HlxKeyLocal;
import fun.qianxiao.originalassistant.utils.PostContentFormatUtils;
import fun.qianxiao.originalassistant.utils.SettingPreferences;
import fun.qianxiao.originalassistant.view.RecyclerSpace;

/**
 * OriginalFragment
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class OriginalFragment<A extends BaseActivity<?>> extends BaseFragment<FragmentOriginalBinding, A> {
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private AtomicBoolean isAppQuerying = new AtomicBoolean(false);

    private final int APP_QUERY_NOT_AUTO = -3;
    private final int APP_QUERY_MANUAL = -2;
    private final int APP_QUERY_AUTO_ALL = -1;

    @Override
    protected void initListener() {
        setScrollEditTextListener();
        setRadioButtonChangeLister();
        setFloatingActionButtonListener();
        setEdiTextActionLitener();
        setSpecialInstructionsSpinnerListener();
        setSpecialInstructionsEditTextChangeListener();

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
                    String tag_s = "";
                    if (tag != null) {
                        tag_s = (String) tag;
                        String[] hasAdded = tag_s.split("#");
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
                    if (TextUtils.isEmpty(tag_s)) {
                        tag_s = String.valueOf(position);
                    } else {
                        tag_s = tag_s + "#" + position;
                    }
                    binding.etSpecialInstructions.setTag(tag_s);
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

    private void setEdiTextActionLitener() {
        TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    KeyboardUtils.hideSoftInput(v);
                    return true;
                }
                return false;
            }
        };
        binding.etGameName.setOnEditorActionListener(onEditorActionListener);
        binding.etGamePackageName.setOnEditorActionListener(onEditorActionListener);
        binding.etGameSize.setOnEditorActionListener(onEditorActionListener);
        binding.etGameVersion.setOnEditorActionListener(onEditorActionListener);
        binding.etGameVersionCode.setOnEditorActionListener(onEditorActionListener);
        binding.etDownloadUrl.setOnEditorActionListener(onEditorActionListener);
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
    }

    private void setRadioButtonChangeLister() {
        CompoundButton.OnCheckedChangeListener rbGameLanguageCheckedChangeListener = (buttonView, isChecked) -> {
            ViewGroup viewGroup = (ViewGroup) buttonView.getParent();
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                AppCompatRadioButton radioButton = (AppCompatRadioButton) viewGroup.getChildAt(i);
                if (radioButton.getId() != buttonView.getId() && isChecked) {
                    /*
                    Use tag of view to save index and them use PostInfo.AppLanguage.values()[i] to convert to enum.
                     */
                    binding.llCategoryRadioButtonGroup.setTag(i);
                    radioButton.setChecked(false);
                }
            }
        };
        binding.rbGameLanguageChineseGame.setOnCheckedChangeListener(rbGameLanguageCheckedChangeListener);
        binding.rbGameLanguageEnglishGame.setOnCheckedChangeListener(rbGameLanguageCheckedChangeListener);
        binding.rbGameLanguageOtherGame.setOnCheckedChangeListener(rbGameLanguageCheckedChangeListener);
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
                onKeyPost();
            } else {
                gotoApp();
            }
        });
    }

    private void onKeyPost() {
        String key = HlxKeyLocal.read();
        if (TextUtils.isEmpty(key)) {
            ToastUtils.showShort("未登录");
            return;
        }
        HLXApiManager.INSTANCE.checkKey(key, new HLXApiManager.OnCommonBooleanResultListener() {
            @Override
            public void onResult(boolean valid, String errMsg) {
                if (valid) {
                    // TODO
                    ToastUtils.showShort("...");
                } else {
                    ToastUtils.showShort(errMsg);
                }
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

        binding.rvAppPics.setAdapter(new AppPicturesAdapter(Collections.emptyList()));
    }

    private void copyContent() {
        PostInfo postInfo = new PostInfo();
        postInfo.setAppName(binding.etGameName.getText());
        postInfo.setAppPackageName(binding.etGamePackageName.getText());
        postInfo.setAppSize(binding.etGameSize.getText());
        postInfo.setAppVersionName(binding.etGameVersion.getText());
        postInfo.setAppVersionCode(binding.etGameVersionCode.getText());
        LogUtils.i(binding.llCategoryRadioButtonGroup.getTag());
        postInfo.setAppLanguage(PostInfo.AppLanguage.values()[Integer.parseInt(String.valueOf(binding.llCategoryRadioButtonGroup.getTag()))]);
        postInfo.setAppSpecialInstructions(binding.etSpecialInstructions.getText());
        postInfo.setAppIntroduction(binding.etGameIntroduction.getText());
        postInfo.setAppDownloadUrl(binding.etDownloadUrl.getText());

        ClipboardUtils.copyText(PostContentFormatUtils.format(postInfo));

        ToastUtils.showShort("已复制到剪贴板");
    }

    private void gotoApp() {
        String appPackageNameInstalled = null;
        for (String s : HULUXIA_APP_PACKAGE_NAME) {
            if (AppUtils.isAppInstalled(s)) {
                appPackageNameInstalled = s;
                break;
            }
        }
        if (!TextUtils.isEmpty(appPackageNameInstalled)) {
            startActivity(IntentUtils.getLaunchAppIntent(appPackageNameInstalled));
        } else {
            ToastUtils.showShort("没有安装3楼");
        }
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
        initSpecialInstructionsSpinner();
        initAppPicturesRecycleView();
        initFloatButtonData();
        initAppMode();
    }

    private void initAppMode() {
        setAppMode(SPUtils.getInstance().getInt(SPConstants.KEY_APP_MODE, Constants.APP_MODE_GAME));
    }

    private void initFloatButtonData() {
        if (SettingPreferences.getBoolean(R.string.p_key_switch_post_one_key)) {
            binding.fabGotoApp.setTitle("发帖");
        }
    }

    private void initAppPicturesRecycleView() {
        binding.rvAppPics.setLayoutManager(new GridLayoutManager(getContext(), 4));
        binding.rvAppPics.addItemDecoration(new RecyclerSpace(4));
    }

    private void autoAppQuery(String appName, String packageName, IQuery.OnAppQueryListener onAppQueryListener) {
        isAppQuerying.set(true);
        ThreadUtils.executeBySingle(new ThreadUtils.SimpleTask<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                AppQueryMannager.getInstance().query(appName, packageName, onAppQueryListener);
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
                LogUtils.i(code, message, appQueryResult == null ? "appQueryResult null" : appQueryResult.getAppIntroduction(),
                        appQueryResult == null ? "appQueryResult null" : appQueryResult.getAppPictures());
                if (code == IQuery.OnAppQueryListener.QUERY_CODE_SUCCESS) {
                    binding.etGameIntroduction.setText(appQueryResult.getAppIntroduction());
                    if (appQueryResult.getAppPictures() != null && appQueryResult.getAppPictures().size() > 0) {
                        binding.rvAppPics.setAdapter(new AppPicturesAdapter(appQueryResult.getAppPictures()));
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
        if (!NetworkUtils.isAvailable()) {
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
            AppQueryMannager.createQuerier(AppQueryMannager.AppQueryChannel.values()[appQueryChannel].getChannel()).query(appName, packageName, onAppQueryListener);
        }
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
            if (!TextUtils.isEmpty(appName) && !TextUtils.isEmpty(appName)) {
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
        // TODO
        ToastUtils.showShort("开发中");
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
}
