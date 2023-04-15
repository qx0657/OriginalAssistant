package fun.qianxiao.originalassistant.fragment.original;

import static fun.qianxiao.originalassistant.config.AppConfig.HULUXIA_APP_PACKAGE_NAME;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
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
import androidx.appcompat.widget.AppCompatRadioButton;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.IntentUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fun.qianxiao.originalassistant.MainActivity;
import fun.qianxiao.originalassistant.R;
import fun.qianxiao.originalassistant.activity.selectapp.SelectAppActivity;
import fun.qianxiao.originalassistant.base.BaseActivity;
import fun.qianxiao.originalassistant.base.BaseFragment;
import fun.qianxiao.originalassistant.bean.PostInfo;
import fun.qianxiao.originalassistant.databinding.FragmentOriginalBinding;
import fun.qianxiao.originalassistant.utils.PostContentFormatUtils;
import fun.qianxiao.originalassistant.utils.SettingPreferences;

/**
 * OriginalFragment
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class OriginalFragment<A extends BaseActivity<?>> extends BaseFragment<FragmentOriginalBinding, A> {
    private ActivityResultLauncher<Intent> activityResultLauncher;

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
            cleanAllInputContent();
        });
        binding.fabCopyContent.setOnClickListener(view -> {
            binding.famOriginal.collapse();
            copyContent();
        });
        binding.fabGotoApp.setOnClickListener(view -> {
            binding.famOriginal.collapse();
            gotoApp();
        });
    }

    private void selectApp() {
        activityResultLauncher.launch(new Intent(activity, SelectAppActivity.class));
    }

    private void cleanAllInputContent() {
        binding.etGameName.setText("");
        binding.etGamePackageName.setText("");
        binding.etGameSize.setText("");
        binding.etGameVersion.setText("");
        binding.etGameVersionCode.setText("");
        binding.etSpecialInstructions.setText("");
        binding.etGameIntroduction.setText("");
        binding.etDownloadUrl.setText("");
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
                            // getSize auto byte2FitMemorySize
                            binding.etGameSize.setText(FileUtils.getSize(packageInfo.applicationInfo.sourceDir));
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        initSpecialInstructionsSpinner();
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

    @Override
    public void onDestroy() {
        KeyboardUtils.unregisterSoftInputChangedListener(getActivity().getWindow());
        super.onDestroy();
    }
}
