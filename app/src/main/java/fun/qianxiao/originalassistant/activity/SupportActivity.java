package fun.qianxiao.originalassistant.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URISyntaxException;

import fun.qianxiao.originalassistant.base.BaseActivity;
import fun.qianxiao.originalassistant.databinding.ActivitySupportBinding;

/**
 * SupportActivity
 *
 * @Author QianXiao
 * @Date 2023/4/13
 */
public class SupportActivity extends BaseActivity<ActivitySupportBinding> {
    @Override
    protected void initListener() {
        binding.llSupportQq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardUtils.copyText("1540223760");
                ToastUtils.showShort("QQ号已复制至剪贴板");
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("mqqapi://forward/url?url_prefix=aHR0cHM6Ly9tcXEudGVucGF5LmNvbS92Mi9oeWJyaWQvd3d3L21vYmlsZV9xcS9wYXltZW50L2luZGV4LnNodG1sP193dj0xMDI3JmZyb209MTMmX3ZhY2Y9cXc=&version=1&src_type=web"));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.showShort("无缘");
                }
            }
        });
        binding.llSupportWechat.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                try {
                    AssetManager assetManager = getAssets();
                    InputStream inputStream = assetManager.open("wx.jpg");
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    File file = new File(PathUtils.getExternalDcimPath() + File.separator + "原创助手赞助码.jpg");
                    LogUtils.i(file.toString());
                    if (FileUtils.createOrExistsFile(file)) {
                        FileOutputStream fos = new FileOutputStream(file);
                        if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)) {
                            fos.flush();
                            fos.close();
                            Uri uri = Uri.fromFile(file);
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                            ToastUtils.showShort("收款二维码已保存至相册，请微信扫码转账");
                        } else {
                            throw new Exception();
                        }
                    } else {
                        ToastUtils.showShort("请检查App读写权限");
                        return;
                    }

                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"));
                    intent.putExtra("LauncherUI.From.Scaner.Shortcut", true);
                    // Intent.FLAG_RECEIVER_FOREGROUND|Intent.FLAG_RECEIVER_REGISTERED_ONLY_BEFORE_BOOT
                    intent.setFlags(335544320);
                    intent.setAction("android.intent.action.VIEW");
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.showShort("无缘");
                }
            }
        });
        binding.llSupportAlipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String urlCode = "FKX101374U56AWFTQXXCI21";
                    Intent intent = Intent.parseUri(
                            "intent://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2F{urlCode}%3F_s%3Dweb-other&_t=1472443966571#Intent;scheme=alipayqr;package=com.eg.android.AlipayGphone;end".replace("{urlCode}", urlCode),
                            Intent.URI_INTENT_SCHEME);
                    startActivity(intent);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    ToastUtils.showShort("无缘");
                }
            }
        });
    }

    @Override
    protected void initData() {
        setTitle("捐赠支持");
        showBackIcon();
    }
}
