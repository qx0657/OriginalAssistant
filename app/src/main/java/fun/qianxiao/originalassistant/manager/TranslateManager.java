package fun.qianxiao.originalassistant.manager;

import androidx.annotation.NonNull;

import fun.qianxiao.originalassistant.translate.BaiduTranslate;
import fun.qianxiao.originalassistant.translate.GoogleTranslate;
import fun.qianxiao.originalassistant.translate.ITranslate;
import fun.qianxiao.originalassistant.translate.Translate;
import fun.qianxiao.originalassistant.translate.YoudaoTranslate;

/**
 * TranslateManager
 *
 * @Author QianXiao
 * @Date 2023/4/16
 */
public class TranslateManager {
    private volatile static TranslateManager instance;

    public enum TranslateInterfaceType {
        /**
         * 自动; 有道; 百度; 谷歌;
         */
        AUTO_TRANSLATE, YOUDAO_TRANSLATE, BAIDU_TRANSLATE, GOOGLE_TRANSLATE;
    }

    private TranslateManager() {
    }

    public static TranslateManager getInstance() {
        if (instance == null) {
            synchronized (TranslateManager.class) {
                if (instance == null) {
                    instance = new TranslateManager();
                }
            }
        }
        return instance;
    }

    public static @NonNull
    <T extends Translate<?>> ITranslate createTranslater(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public void translate(TranslateInterfaceType type, String en, ITranslate.OnTranslateListener onTranslateListener) {
        boolean auto = false;
        switch (type) {
            case AUTO_TRANSLATE:
                createTranslater(BaiduTranslate.class).translate(en, new ITranslate.OnTranslateListener() {
                    @Override
                    public void onTranslateResult(int code, String msg, String result) {
                        if (code == ITranslate.OnTranslateListener.TRANSLATE_ERROR) {
                            createTranslater(YoudaoTranslate.class).translate(en, onTranslateListener);
                        } else {
                            onTranslateListener.onTranslateResult(code, msg, result);
                        }
                    }
                });
            case YOUDAO_TRANSLATE:
                createTranslater(YoudaoTranslate.class).translate(en, onTranslateListener);
                break;
            case BAIDU_TRANSLATE:
                createTranslater(BaiduTranslate.class).translate(en, onTranslateListener);
                break;
            case GOOGLE_TRANSLATE:
                createTranslater(GoogleTranslate.class).translate(en, onTranslateListener);
                break;
            default:
                break;
        }
    }

}
