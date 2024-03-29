package fun.qianxiao.originalassistant.translate;

import androidx.annotation.MainThread;

/**
 * ITranslate
 *
 * @Author QianXiao
 * @Date 2023/4/16
 */
public interface ITranslate {

    interface OnTranslateListener {
        int TRANSLATE_SUCCESS = 0;
        int TRANSLATE_ERROR = -1;

        /**
         * onTranslateResult
         * Note that exec in MainThread
         *
         * @param code   code
         * @param msg    msg
         * @param result chinese result
         */
        @MainThread
        void onTranslateResult(int code, String msg, String result);
    }

    /**
     * translate
     *
     * @param text                text
     * @param onTranslateListener onTranslateListener
     */
    void translate(String text, OnTranslateListener onTranslateListener);
}
