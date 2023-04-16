package fun.qianxiao.originalassistant.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;

import java.util.Arrays;

import fun.qianxiao.originalassistant.R;
import fun.qianxiao.originalassistant.config.SPConstants;
import fun.qianxiao.originalassistant.utils.MyStringUtils;
import fun.qianxiao.originalassistant.utils.SettingPreferences;

/**
 * SettingsActivity
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingg);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.setting_preferences, rootKey);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            if (TextUtils.isEmpty(SettingPreferences.getString(R.string.p_key_special_instructions))) {
                EditTextPreference etpSpecialInstructions = $(R.string.p_key_special_instructions);
                String[] specialInstructionsDefault = getResources().getStringArray(R.array.special_instructions);
                etpSpecialInstructions.setText(MyStringUtils.join(
                        Arrays.copyOfRange(specialInstructionsDefault, 1, specialInstructionsDefault.length),
                        "\n")
                );
            }
        }

        @SuppressWarnings("unchecked")
        private <T> T $(int key) {
            return $(getString(key));
        }

        @SuppressWarnings("unchecked")
        private <T> T $(CharSequence key) {
            return (T) findPreference(key);
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            String key = preference.getKey();
            LogUtils.i(preference.getTitle() + " " + key + ": " + SettingPreferences.get(key));
            if (key.equals(getString(R.string.p_key_switch_post_one_key))) {
                SwitchPreference switchPreference = $(R.string.p_key_switch_title);
                if (SettingPreferences.getBoolean(key)) {
                    SPUtils.getInstance().put(SPConstants.KEY_TITLE_STATUS_BEFORE_SWITCH_POST_ONE_KEY_ON, switchPreference.isChecked());
                    if (!switchPreference.isChecked()) {
                        switchPreference.setChecked(true);
                    }
                } else {
                    boolean beforeStatus = SPUtils.getInstance().getBoolean(SPConstants.KEY_TITLE_STATUS_BEFORE_SWITCH_POST_ONE_KEY_ON);
                    switchPreference.setChecked(beforeStatus);
                }
            } else if (key.equals(getString(R.string.p_key_switch_title))) {
                SPUtils.getInstance().remove(SPConstants.KEY_TITLE_STATUS_BEFORE_SWITCH_POST_ONE_KEY_ON);
            }
            return super.onPreferenceTreeClick(preference);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}