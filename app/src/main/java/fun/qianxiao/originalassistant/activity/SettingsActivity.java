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
import androidx.preference.PreferenceFragmentCompat;

import java.util.Arrays;

import fun.qianxiao.originalassistant.R;
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