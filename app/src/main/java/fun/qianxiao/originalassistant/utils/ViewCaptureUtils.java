package fun.qianxiao.originalassistant.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import com.blankj.utilcode.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * ViewCaptureUtils
 *
 * @Author QianXiao
 * @Date 2023/5/18
 */
public class ViewCaptureUtils {

    /**
     * view2Bitmap
     *
     * @param view view
     * @return Bitmap
     */
    public static Bitmap view2Bitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        return bitmap;
    }

    /**
     * view2PicFile
     *
     * @param view     view
     * @param filePath file path
     * @return if success
     */
    public static boolean view2PicFile(View view, String filePath) {
        Bitmap bitmap = view2Bitmap(view);
        File file = new File(filePath);
        if (FileUtils.createOrExistsFile(file)) {
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file);
                boolean success = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                if (success) {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
