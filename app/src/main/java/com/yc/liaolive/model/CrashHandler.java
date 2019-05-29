package com.yc.liaolive.model;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.util.Logger;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * TinyHung@Outlook.com
 * 2018/5/9
 * 异常捕获
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        try {
            if(null!=AppEngine.getApplication()){
                uploadReport(saveReport(e));
            }
        }catch (Exception es){
        }
    }
    /**
     * 生成错误日志
     * @param ex
     * @return
     */
    private File saveReport(Throwable ex) {
        FileWriter writer = null;
        PrintWriter printWriter = null;
        try {
            File file = new File(AppEngine.getApplication().getFilesDir(), "error_log" + System.currentTimeMillis());
            writer = new FileWriter(file);
            printWriter = new PrintWriter(writer);
            writer.append("========Build==========\n");
            writer.append(String.format("BOARD\t%s\n", Build.BOARD));
            writer.append(String.format("BOOTLOADER\t%s\n", Build.BOOTLOADER));
            writer.append(String.format("BRAND\t%s\n", Build.BRAND));
            writer.append(String.format("CPU_ABI\t%s\n", Build.CPU_ABI));
            writer.append(String.format("CPU_ABI2\t%s\n", Build.CPU_ABI2));
            writer.append(String.format("DEVICE\t%s\n", Build.DEVICE));
            writer.append(String.format("DISPLAY\t%s\n", Build.DISPLAY));
            writer.append(String.format("FINGERPRINT\t%s\n", Build.FINGERPRINT));
            writer.append(String.format("HARDWARE\t%s\n", Build.HARDWARE));
            writer.append(String.format("HOST\t%s\n", Build.HOST));
            writer.append(String.format("ID\t%s\n", Build.ID));
            writer.append(String.format("MANUFACTURER\t%s\n", Build.MANUFACTURER));
            writer.append(String.format("MODEL\t%s\n", Build.MODEL));
            writer.append(String.format("SERIAL\t%s\n", Build.SERIAL));
            writer.append(String.format("PRODUCT\t%s\n", Build.PRODUCT));

            writer.append("========APP==========\n");
            try {
                PackageInfo packageInfo = AppEngine.getApplication().getPackageManager().getPackageInfo(AppEngine.getApplication().getPackageName(), 0);
                int versionCode = packageInfo.versionCode;
                String versionName = packageInfo.versionName;
                writer.append(String.format("versionCode\t%s\n", versionCode));
                writer.append(String.format("versionName\t%s\n", versionName));

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            writer.append("========Exception==========\n");
            ex.printStackTrace(printWriter);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }

    /**
     * 上传错误日志
     * @param report
     */
    private void uploadReport(File report) {
        OutputStream os = null;
        FileInputStream fis = null;
        try {
            URL url = new URL("www.baidu.com" + "/ErrorReportServlet");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            os = urlConnection.getOutputStream();
            fis = new FileInputStream(report);
            byte[] buf = new byte[1024 * 8];
            int len = 0;
            while ((len = fis.read(buf)) != -1) {
                os.write(buf, 0, len);
            }
            int responseCode = urlConnection.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(os);
            close(fis);
        }
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
