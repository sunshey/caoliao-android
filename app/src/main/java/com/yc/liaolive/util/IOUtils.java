package com.yc.liaolive.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * @since 2016-01-18
 */
public class IOUtils {
    public static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean deleteFile(String path) {
        File file = new File(path);
        return file.exists() && file.isFile() && file.delete();
    }
}
