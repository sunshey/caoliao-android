package com.yc.liaolive.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by zhangkai on 16/11/8.
 */
public class ZipUtil {
    private static byte[] ZIP_HEADER_1 = new byte[]{80, 75, 3, 4};
    private static byte[] ZIP_HEADER_2 = new byte[]{80, 75, 5, 6};


    /**
     * 判断文件是否为一个压缩文件
     *
     * @param file
     * @return
     */
    public static boolean isArchiveFile(File file) {
        if (file == null) {
            return false;
        }

        if (file.isDirectory()) {
            return false;
        }

        boolean isArchive = false;
        InputStream input = null;
        try {
            input = new FileInputStream(file);
            byte[] buffer = new byte[4];
            int length = input.read(buffer, 0, 4);
            if (length == 4) {
                isArchive = (Arrays.equals(ZIP_HEADER_1, buffer)) || (Arrays.equals(ZIP_HEADER_2, buffer));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }

        return isArchive;
    }
}
