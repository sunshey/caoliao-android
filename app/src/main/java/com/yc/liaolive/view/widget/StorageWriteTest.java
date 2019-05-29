package com.yc.liaolive.view.widget;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
/**
 * TinyHung@Outlook.com
 * 2018/7/17
 */



/**
 * Created by YanZhenjie on 2018/1/16.
 */
public class StorageWriteTest  {

    public StorageWriteTest() {

    }

    public boolean test() {
        File directory = Environment.getExternalStorageDirectory();
        if (!directory.exists() || !directory.canWrite()) return false;
        File parent = new File(directory, "Android");
        if (parent.exists() && parent.isFile()) if (!parent.delete()) return false;
        if (!parent.exists()) if (!parent.mkdirs()) return false;
        File file = new File(parent, "ANDROID.PERMISSION.TEST");
        if (file.exists()) return file.delete();
        else try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
