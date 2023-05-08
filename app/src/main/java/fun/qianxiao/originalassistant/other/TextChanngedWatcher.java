package fun.qianxiao.originalassistant.other;

import android.text.TextWatcher;

/**
 * TextChanngedWatcher
 *
 * @Author QianXiao
 * @Date 2023/5/9
 */
public abstract class TextChanngedWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }
}
