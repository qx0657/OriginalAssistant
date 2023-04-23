package fun.qianxiao.originalassistant.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;

/**
 * TODO
 *
 * @Author QianXiao
 * @Date 2023/4/23
 */
public class PictureFileUtils {

    public static Pair<Integer, Integer> getWidthAndHeight(String pathName) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = 1;
        options.inJustDecodeBounds = false;
        return new Pair<>(options.outWidth, options.outHeight);
    }
}
