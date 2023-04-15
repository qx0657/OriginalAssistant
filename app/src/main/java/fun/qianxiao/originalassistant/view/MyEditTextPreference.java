package fun.qianxiao.originalassistant.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceViewHolder;

import fun.qianxiao.originalassistant.R;

/**
 * MyEditTextPreference
 *
 * @Author QianXiao
 * @Date 2023/4/15
 */
public class MyEditTextPreference extends EditTextPreference {
    private boolean summarySingleLine;
    private int summaryEllipsize;
    private String editTextHint;
    private int editTextInputType;
    private boolean editTextSingleLine;

    public MyEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public MyEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public MyEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MyEditTextPreference(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyEditTextPreference);
        summarySingleLine = a.getBoolean(R.styleable.MyEditTextPreference_summarySingleLine, false);
        summaryEllipsize = a.getInt(R.styleable.MyEditTextPreference_summaryEllipsize, 0);
        editTextHint = a.getString(R.styleable.MyEditTextPreference_editTextHint);
        editTextSingleLine = a.getBoolean(R.styleable.MyEditTextPreference_editTextSingleLine, false);
        editTextInputType = a.getInt(R.styleable.MyEditTextPreference_editTextInputType, 0);
        a.recycle();
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        TextView textView = (TextView) holder.findViewById(android.R.id.summary);
        textView.setSingleLine(summarySingleLine);
        switch (summaryEllipsize) {
            case 1:
                textView.setEllipsize(TextUtils.TruncateAt.START);
                break;
            case 2:
                textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                break;
            case 3:
                textView.setEllipsize(TextUtils.TruncateAt.END);
                break;
            default:
                break;
        }
        setOnBindEditTextListener(null);
    }

    @Override
    public void setOnBindEditTextListener(@Nullable OnBindEditTextListener onBindEditTextListener) {
        super.setOnBindEditTextListener(new OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                editText.setSingleLine(editTextSingleLine);
                switch (editTextInputType) {
                    case 1:
                        editText.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case 2:
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        break;
                    default:
                        break;
                }
                editText.setHint(editTextHint);
                editText.setSelection(editText.getText().length());
                if (onBindEditTextListener != null) {
                    onBindEditTextListener.onBindEditText(editText);
                }
            }
        });
    }
}
