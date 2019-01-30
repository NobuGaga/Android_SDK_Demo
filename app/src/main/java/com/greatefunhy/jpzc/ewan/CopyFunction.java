package com.greatefunhy.jpzc.ewan;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by 毕伟雄 on 2018/2/2.
 * 复制粘贴类
 */

public class CopyFunction {
    private static ClipboardManager mInstance;

    public static void Init(Activity activity) {
        if (null == mInstance)
            mInstance = (ClipboardManager)activity.getSystemService(CLIPBOARD_SERVICE);
    }

    public static void Copy(String text) {
        if (null != mInstance) {
            ClipData myClip = ClipData.newPlainText(null, text);
            mInstance.setPrimaryClip(myClip);
        }
    }

    public static String Paste() {
        if (null != mInstance) {
            ClipData data = mInstance.getPrimaryClip();
            if (null != data) {
                ClipData.Item item = data.getItemAt(0);
                if (null != item) {
                    String text = item.getText().toString();
                    if (null == text)
                        text = "";
                }
            }
        }
        return "";
    }
}

