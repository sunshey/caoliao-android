package com.yc.liaolive.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.contants.Constant;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author yuyh.
 * @date 16/4/9.
 */
public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();

    public static boolean unzipFile(String inputZip, String destinationDirectory) {
        int buffer = 2048;
        List<String> zipFiles = new ArrayList<>();
        File sourceZipFile = new File(inputZip);
        File unzipDirectory = new File(destinationDirectory);

        createDir(unzipDirectory.getAbsolutePath());

        ZipFile zipFile;
        try {
            zipFile = new ZipFile(sourceZipFile, ZipFile.OPEN_READ);
            Enumeration zipFileEntries = zipFile.entries();

            while (zipFileEntries.hasMoreElements()) {

                ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
                String currentEntry = entry.getName();
                File destFile = new File(unzipDirectory, currentEntry);

                if (currentEntry.endsWith(Constant.SUFFIX_ZIP)) {
                    zipFiles.add(destFile.getAbsolutePath());
                }
                File destinationParent = destFile.getParentFile();
                createDir(destinationParent.getAbsolutePath());

                if (!entry.isDirectory()) {

                    if (destFile != null && destFile.exists()) {

                        continue;
                    }
                    BufferedInputStream is = new BufferedInputStream(zipFile.getInputStream(entry));
                    int currentByte;
                    // buffer for writing file
                    byte[] data = new byte[buffer];

                    FileOutputStream fos = new FileOutputStream(destFile);
                    BufferedOutputStream dest = new BufferedOutputStream(fos, buffer);

                    while ((currentByte = is.read(data, 0, buffer)) != -1) {
                        dest.write(data, 0, currentByte);
                    }
                    dest.flush();
                    dest.close();
                    is.close();
                }
            }
            zipFile.close();
            for (Iterator iter = zipFiles.iterator(); iter.hasNext(); ) {
                String zipName = (String) iter.next();
                unzipFile(zipName, destinationDirectory + File.separatorChar
                        + zipName.substring(0, zipName.lastIndexOf(Constant.SUFFIX_ZIP)));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 读取Assets文件
     *
     * @param fileName
     * @return
     */
    public static byte[] readAssets(String fileName) {
        if (fileName == null || fileName.length() <= 0) {
            return null;
        }
        byte[] buffer = null;
        try {
            InputStream fin = VideoApplication.getInstance().getAssets().open("uploader" + fileName);
            int length = fin.available();
            buffer = new byte[length];
            fin.read(buffer);
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return buffer;
        }
    }

    /**
     * 创建根缓存目录
     * @return
     */
    public static String createRootPath(Context context) {
        String cacheRootPath = "";
        //SD卡已挂载，使用SD卡缓存目录，这个缓存补录数据不会随着应用的卸载而清除
        if (isSdCardAvailable()) {
            // /sdcard/Android/data/<application package>/cache
            if(null!=context.getExternalCacheDir()){
                cacheRootPath = context.getExternalCacheDir().getPath();//SD卡内部临时缓存目录
            }
        //内部缓存目录，会随着应用的卸载而清除
        } else {
            // /data/data/<application package>/cache
            if(null!=context.getCacheDir()){
                cacheRootPath = context.getCacheDir().getPath();//应用内部临时缓存目录
            }else{
                File cacheDirectory = getCacheDirectory(context, null);
                if(null!=cacheDirectory){
                    cacheRootPath=cacheDirectory.getAbsolutePath();
                }
            }
        }
        return cacheRootPath;
    }

    /**
     * 获取临时文件缓存目录
     * @return
     */
    public static String getFileDir(Context context) {
        String cacheRootPath = null;
        if(null!=context.getFilesDir()){
            cacheRootPath=context.getFilesDir().getPath();
        }else if(isSdCardAvailable()){
            if(null!=context.getExternalCacheDir()){
                cacheRootPath = context.getExternalCacheDir().getPath();//SD卡内部临时缓存目录
            }
        }else if(null!=context.getCacheDir()){
            cacheRootPath= context.getCacheDir().getPath();
        }else{
            File cacheDirectory = getCacheDirectory(context, null);
            if(null!=cacheDirectory){
                cacheRootPath=cacheDirectory.getAbsolutePath();
            }
        }
        return cacheRootPath;
    }

    /**
     * 获取临时数据缓存目录
     * @param context
     * @return
     */
    public static String getCacheDir(Context context) {
        String cacheRootPath = null;
        if(null!=context.getCacheDir()){
            cacheRootPath= context.getCacheDir().getPath();
        } else if(null!=context.getFilesDir()){
            cacheRootPath=context.getFilesDir().getPath();
        }else if(isSdCardAvailable()){
            if(null!=context.getExternalCacheDir()){
                cacheRootPath = context.getExternalCacheDir().getPath();//SD卡内部临时缓存目录
            }
        }else{
            File cacheDirectory = getCacheDirectory(context, null);
            if(null!=cacheDirectory){
                cacheRootPath=cacheDirectory.getAbsolutePath();
            }
        }
        return cacheRootPath;
    }


    public static boolean isSdCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取应用专属缓存目录
     * android 4.4及以上系统不需要申请SD卡读写权限
     * 因此也不用考虑6.0系统动态申请SD卡读写权限问题，切随应用被卸载后自动清空 不会污染用户存储空间
     * @param context 上下文
     * @param type 文件夹类型 可以为空，为空则返回API得到的一级目录
     * @return 缓存文件夹 如果没有SD卡或SD卡有问题则返回内存缓存目录，否则优先返回SD卡缓存目录
     */
    public static File getCacheDirectory(Context context,String type) {
        File appCacheDir = getExternalCacheDirectory(context,type);
        if (appCacheDir == null){
            appCacheDir = getInternalCacheDirectory(context,type);
        }

        if (appCacheDir == null){
            Log.e("getCacheDirectory","getCacheDirectory fail ,the reason is mobile phone unknown exception !");
        }else {
            if (!appCacheDir.exists()&&!appCacheDir.mkdirs()){
                Log.e("getCacheDirectory","getCacheDirectory fail ,the reason is make directory fail !");
            }
        }
        return appCacheDir;
    }

    /**
     * 获取SD卡缓存目录
     * @param context 上下文
     * @param type 文件夹类型 如果为空则返回 /storage/emulated/0/Android/data/app_package_name/cache
     *             否则返回对应类型的文件夹如Environment.DIRECTORY_PICTURES 对应的文件夹为 .../data/app_package_name/files/Pictures
     * {@link android.os.Environment#DIRECTORY_MUSIC},
     * {@link android.os.Environment#DIRECTORY_PODCASTS},
     * {@link android.os.Environment#DIRECTORY_RINGTONES},
     * {@link android.os.Environment#DIRECTORY_ALARMS},
     * {@link android.os.Environment#DIRECTORY_NOTIFICATIONS},
     * {@link android.os.Environment#DIRECTORY_PICTURES}, or
     * {@link android.os.Environment#DIRECTORY_MOVIES}.or 自定义文件夹名称
     * @return 缓存目录文件夹 或 null（无SD卡或SD卡挂载失败）
     */
    public static File getExternalCacheDirectory(Context context,String type) {
        File appCacheDir = null;
        if( Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (TextUtils.isEmpty(type)){
                appCacheDir = context.getExternalCacheDir();
            }else {
                appCacheDir = context.getExternalFilesDir(type);
            }

            if (appCacheDir == null){// 有些手机需要通过自定义目录
                appCacheDir = new File(Environment.getExternalStorageDirectory(),"Android/data/"+context.getPackageName()+"/cache/"+type);
            }

            if (appCacheDir == null){
                Log.e("getExternalDirectory","getExternalDirectory fail ,the reason is sdCard unknown exception !");
            }else {
                if (!appCacheDir.exists()&&!appCacheDir.mkdirs()){
                    Log.e("getExternalDirectory","getExternalDirectory fail ,the reason is make directory fail !");
                }
            }
        }else {
            Log.e("getExternalDirectory","getExternalDirectory fail ,the reason is sdCard nonexistence or sdCard mount fail !");
        }
        return appCacheDir;
    }

    /**
     * 获取内存缓存目录
     * @param type 子目录，可以为空，为空直接返回一级目录
     * @return 缓存目录文件夹 或 null（创建目录文件失败）
     * 注：该方法获取的目录是能供当前应用自己使用，外部应用没有读写权限，如 系统相机应用
     */
    public static File getInternalCacheDirectory(Context context,String type) {
        File appCacheDir = null;
        if (TextUtils.isEmpty(type)){
            appCacheDir = context.getCacheDir();// /data/data/app_package_name/cache
        }else {
            appCacheDir = new File(context.getFilesDir(),type);// /data/data/app_package_name/files/type
        }

        if (!appCacheDir.exists()&&!appCacheDir.mkdirs()){
            Log.e("getInternalDirectory","getInternalDirectory fail ,the reason is make directory fail !");
        }
        return appCacheDir;
    }


    /**
     * 递归创建文件夹
     *
     * @param dirPath
     * @return 创建失败返回""
     */
    public static String createDir(String dirPath) {
        try {
            File file = new File(dirPath);
            if (file.getParentFile().exists()) {

                file.mkdir();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());

                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dirPath;
    }

    /**
     * 递归创建文件夹
     *
     * @param file
     * @return 创建失败返回""
     */
    public static String createFile(File file) {
        try {
            if (file.getParentFile().exists()) {

                file.createNewFile();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                file.createNewFile();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将内容写入文件
     *
     * @param filePath eg:/mnt/sdcard/demo.txt
     * @param content  内容
     * @param isAppend 是否追加
     */
    public static void writeFile(String filePath, String content, boolean isAppend) {

        try {
            FileOutputStream fout = new FileOutputStream(filePath, isAppend);
            byte[] bytes = content.getBytes();
            fout.write(bytes);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(String filePathAndName, String fileContent) {
        try {
            OutputStream outstream = new FileOutputStream(filePathAndName);
            OutputStreamWriter out = new OutputStreamWriter(outstream);
            out.write(fileContent);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Raw下的文件内容
     *
     * @param context
     * @param resId
     * @return 文件内容
     */
    public static String getFileFromRaw(Context context, int resId) {
        if (context == null) {
            return null;
        }

        StringBuilder s = new StringBuilder();
        try {
            InputStreamReader in = new InputStreamReader(context.getResources().openRawResource(resId));
            BufferedReader br = new BufferedReader(in);
            String line;
            while ((line = br.readLine()) != null) {
                s.append(line);
            }
            return s.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] getBytesFromFile(File f) {
        if (f == null) {
            return null;
        }
        try {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            for (int n; (n = stream.read(b)) != -1; ) {
                out.write(b, 0, n);
            }
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
        }
        return null;
    }

    /**
     * 文件拷贝
     *
     * @param src  源文件
     * @param desc 目的文件
     */
    public static void fileChannelCopy(File src, File desc) {
        //createFile(src);
        createFile(desc);
        FileInputStream fi = null;
        FileOutputStream fo = null;
        try {
            fi = new FileInputStream(src);
            fo = new FileOutputStream(desc);
            FileChannel in = fi.getChannel();//得到对应的文件通道
            FileChannel out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fo != null) fo.close();
                if (fi != null) fi.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 转换文件大小
     *
     * @param fileLen 单位B
     * @return
     */
    public static String formatFileSizeToString(long fileLen) {
        DecimalFormat df = new DecimalFormat("0.00");
        String fileSizeString = "";
        if (fileLen < 1024) {
            fileSizeString = df.format((double) fileLen) + "B";
        } else if (fileLen < 1048576) {
            fileSizeString = df.format((double) fileLen / 1024) + "K";
        } else if (fileLen < 1073741824) {
            fileSizeString = df.format((double) fileLen / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileLen / 1073741824) + "G";
        }
        return fileSizeString;
    }

    public static String getFileSize(File file){
        if(file.exists()&&file.isFile()){
            return formatFileSizeToString(file.length());
        }
        return null;
    }
    /**
     * 删除指定文件
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static boolean deleteFile(File file) {
        try {
            return deleteFileOrDirectory(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除指定文件，如果是文件夹，则递归删除
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static boolean deleteFileOrDirectory(File file) throws IOException {
        if(null==file||!file.exists()) return false;
        try {
            if (file != null && file.isFile()) {
                return file.delete();
            }
            if (file != null && file.isDirectory()) {
                File[] childFiles = file.listFiles();
                // 删除空文件夹
                if (childFiles == null || childFiles.length == 0) {
                    return file.delete();
                }
                // 递归删除文件夹下的子文件
                for (int i = 0; i < childFiles.length; i++) {
                    deleteFileOrDirectory(childFiles[i]);
                }
                return file.delete();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取文件夹大小
     *
     * @return
     * @throws Exception
     */
    public static long getFolderSize(String dir) throws Exception {
        File file = new File(dir);
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i].getAbsolutePath());
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /***
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * 获取文件内容
     *
     * @param path
     * @return
     */
    public static String getFileOutputString(String path, String charset) {
        try {
            File file = new File(path);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset), 8192);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append("\n").append(line);
            }
            bufferedReader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 递归获取所有文件
     *
     * @param root
     * @param ext  指定扩展名
     */
    private synchronized void getAllFiles(File root, String ext) {
        List<File> list = new ArrayList<>();
        File files[] = root.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    getAllFiles(f, ext);
                } else {
                    if (f.getName().endsWith(ext) && f.length() > 50)
                        list.add(f);
                }
            }
        }
    }

    public static String getCharset(String fileName) {
        BufferedInputStream bis = null;
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            bis = new BufferedInputStream(new FileInputStream(fileName));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1)
                return charset;
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE
                    && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF
                    && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8";
                checked = true;
            }
            bis.mark(0);
            if (!checked) {
                while ((read = bis.read()) != -1) {
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) // 双字节 (0xC0 - 0xDF)
                            // (0x80 - 0xBF),也可能在GB编码内
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) {// 也有可能出错，但是几率较小
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return charset;
    }

    public static String getCharset1(String fileName) throws IOException {
        BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName));
        int p = (bin.read() << 8) + bin.read();

        String code;
        switch (p) {
            case 0xefbb:
                code = "UTF-8";
                break;
            case 0xfffe:
                code = "Unicode";
                break;
            case 0xfeff:
                code = "UTF-16BE";
                break;
            default:
                code = "GBK";
        }
        return code;
    }

    public static void saveWifiTxt(String src, String desc) {
        byte[] LINE_END = "\n".getBytes();
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(src), getCharset(src));
            BufferedReader br = new BufferedReader(isr);

            FileOutputStream fout = new FileOutputStream(desc, true);
            String temp;
            while ((temp = br.readLine()) != null) {
                byte[] bytes = temp.getBytes();
                fout.write(bytes);
                fout.write(LINE_END);
            }
            br.close();
            fout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取系统缓存路径
     * @param context
     * @return
     */
    public static String getDiskCacheDir(Context context) {
        String cachePath = null;
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !Environment.isExternalStorageRemovable()) {
                if (context.getExternalCacheDir() != null) {
                    cachePath = context.getExternalCacheDir().getPath();
                } else {
                    context.getCacheDir().getPath();
                }
            } else {
                cachePath = context.getCacheDir().getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cachePath;
    }

    /**
     * 读取表情配置文件
     *
     * @param context
     * @return
     */
    public static List<String> getEmojiFile(Context context) {
        try {
            List<String> list = new ArrayList<String>();
            InputStream in = context.getResources().getAssets().open("emoji");
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String str = null;
            while ((str = br.readLine()) != null) {
                list.add(str);
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public final static String FILE_EXTENSION_SEPARATOR = ".";


    public static boolean writeFile(String filePath, InputStream stream) {
        return writeFile(filePath, stream, false);
    }


    public static boolean writeFile(String filePath, InputStream stream, boolean append) {
        return writeFile(filePath != null ? new File(filePath) : null, stream, append);
    }


    public static boolean writeFile(File file, InputStream stream) {
        return writeFile(file, stream, false);
    }

    public static boolean writeFile(File file, InputStream stream, boolean append) {
        OutputStream o = null;
        try {
            makeDirs(file.getAbsolutePath());
            o = new FileOutputStream(file, append);
            byte data[] = new byte[1024];
            int length = -1;
            while ((length = stream.read(data)) != -1) {
                o.write(data, 0, length);
            }
            o.flush();
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            IOUtils.close(o);
            IOUtils.close(stream);
        }
    }

    /**
     * move file
     *
     * @param sourceFilePath
     * @param destFilePath
     */
    public static void moveFile(String sourceFilePath, String destFilePath) {
        if (TextUtils.isEmpty(sourceFilePath) || TextUtils.isEmpty(destFilePath)) {
            throw new RuntimeException("Both sourceFilePath and destFilePath cannot be null.");
        }
        moveFile(new File(sourceFilePath), new File(destFilePath));
    }

    /**
     * move file
     *
     * @param srcFile
     * @param destFile
     */
    public static void moveFile(File srcFile, File destFile) {
        boolean rename = srcFile.renameTo(destFile);
        if (!rename) {
            copyFile(srcFile.getAbsolutePath(), destFile.getAbsolutePath());
            deleteFile(srcFile.getAbsolutePath());
        }
    }


    public static boolean copyFile(String sourceFilePath, String destFilePath) {

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(sourceFilePath);
        } catch (FileNotFoundException e) {
            return false;
        }
        return writeFile(destFilePath, inputStream);
    }


    public static List<String> readFileToList(String filePath, String charsetName) {
        File file = new File(filePath);
        List<String> fileContent = new ArrayList<String>();
        if (file == null || !file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);
            }
            return fileContent;
        } catch (IOException e) {
            return null;
        } finally {
            IOUtils.close(reader);
        }
    }


    public static String getFileNameWithoutExtension(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        }

        int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosi = filePath.lastIndexOf(File.separator);
        if (filePosi == -1) {
            return (extenPosi == -1 ? filePath : filePath.substring(0, extenPosi));
        }
        if (extenPosi == -1) {
            return filePath.substring(filePosi + 1);
        }
        return (filePosi < extenPosi ? filePath.substring(filePosi + 1, extenPosi) : filePath.substring(filePosi + 1));
    }


    public static String getFileName(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? filePath : filePath.substring(filePosi + 1);
    }

    public static String getFolderName(String filePath) {

        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
    }

    public static String getFileExtension(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return filePath;
        }

        int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosi = filePath.lastIndexOf(File.separator);
        if (extenPosi == -1) {
            return "";
        }
        return (filePosi >= extenPosi) ? "" : filePath.substring(extenPosi + 1);
    }

    public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        if (StringUtils.isEmpty(folderName)) {
            return false;
        }

        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
    }

    /**
     * @param filePath
     * @return
     * @see #makeDirs(String)
     */
    public static boolean makeFolders(String filePath) {
        return makeDirs(filePath);
    }

    /**
     * Indicates if this file represents a file on the underlying file system.
     *
     * @param filePath
     * @return
     */
    public static boolean isFileExist(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return false;
        }

        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }

    /**
     * Indicates if this file represents a directory on the underlying file system.
     *
     * @param directoryPath
     * @return
     */
    public static boolean isFolderExist(String directoryPath) {
        if (StringUtils.isBlank(directoryPath)) {
            return false;
        }

        File dire = new File(directoryPath);
        return (dire.exists() && dire.isDirectory());
    }

    /**
     * delete file or directory
     * <ul>
     * <li>if path is null or empty, return true</li>
     * <li>if path not exist, return true</li>
     * <li>if path exist, delete recursion. return true</li>
     * <ul>
     *
     * @param path
     * @return
     */
    public static boolean deleteFile(String path) {
        try {
            if(TextUtils.isEmpty(path)) return false;
            if (StringUtils.isBlank(path)) {
                return true;
            }

            File file = new File(path);
            if (!file.exists()) {
                return true;
            }
            if (file.isFile()) {
                return file.delete();
            }
            if (!file.isDirectory()) {
                return false;
            }
            for (File f : file.listFiles()) {
                if (f.isFile()) {
                    f.delete();
                } else if (f.isDirectory()) {
                    deleteFile(f.getAbsolutePath());
                }
            }
            return file.delete();
        }catch (RuntimeException e){

        }catch (Exception e){

        }
        return false;
    }

    /**
     * get file size
     * <ul>
     * <li>if path is null or empty, return -1</li>
     * <li>if path exist and it is a file, return file size, else return -1</li>
     * <ul>
     *
     * @param path
     * @return returns the length of this file in bytes. returns -1 if the file does not exist.
     */
    public static long getFileSize(String path) {
        if (StringUtils.isBlank(path)) {
            return -1;
        }

        File file = new File(path);
        return (file.exists() && file.isFile() ? file.length() : -1);
    }


    public static String formatSize(long size) {
        double tmpSize = size;
        if (tmpSize / 1024 < 1) {
            return "" + (int) (tmpSize) + "B";
        } else if (tmpSize / 1024 / 1024 < 1) {
            return "" + (int) (tmpSize / 1024) + "KB";
        } else if (tmpSize / 1024 / 1024 / 1024 < 1) {
            return "" + leaveTwo(tmpSize, 1024 * 1024) + "MB";
        } else if (tmpSize / 1024 / 1024 / 1024 / 1024 < 1) {
            return "" + leaveTwo(tmpSize, 1024 * 1024 * 1024) + "GB";
        } else if (tmpSize / 1024 / 1024 / 1024 / 1024 / 1024 < 1) {
            return "" + leaveTwo(tmpSize, 1024 * 1024 * 1024 * 1024) + "TB";
        } else {
            return null;
        }
    }

    private static double leaveTwo(double num, long base) {
        int tmp = (int) (num / base * 100);
        return 1.0 * tmp / 100;
    }

    public static String[] getDirAndFileName(String path) {
        try {
            int index = path.lastIndexOf("/");
            if (index > 0) {
                String dir = path.substring(0, index);
                String fileName = path.substring(index + 1);
                return new String[]{dir, fileName};
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return null;
    }


    public static boolean renameFile(String sourceFilePath, String downPath) {
        boolean flag = false;
        try {
            if (sourceFilePath != null && downPath != null) {
                File file = new File(sourceFilePath);
                if (file.exists()) {
                    File downFile = new File(downPath);
                    file.renameTo(downFile);
                    flag = true;
                }
            }
        } catch (Exception e) {
        }
        return flag;
    }

    /** 检测文件是否可用 */
    public static boolean checkFile(File f) {
        if (f != null && f.exists() && f.canRead() && (f.isDirectory() || (f.isFile() && f.length() > 0))) {
            return true;
        }
        return false;
    }

    /** 检测文件是否可用 */
    public static boolean checkFile(String path) {
        if (StringUtils.isNotEmpty(path)) {
            File f = new File(path);
            if (f != null && f.exists() && f.canRead() && (f.isDirectory() || (f.isFile() && f.length() > 0)))
                return true;
        }
        return false;
    }

    /**
     * 保存BitMap到相册中
     * @param bitmap
     * @param outPathFileName
     * @return
     */
    public static String saveBitmap(Bitmap bitmap, String outPathFileName) {
        File f = new File( outPathFileName);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            fOut.close();
            return f.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getMd5ByFile(File file) throws FileNotFoundException {

        String value = null;
        FileInputStream in = new FileInputStream(file);
        try {
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    public static File getCacheDiskPath(Context var0, String var1) {
        String var2 = "/mnt/sdcard/Android/data/com.video.newqu/cache";
        if(var0 != null) {
            if("mounted".equals(Environment.getExternalStorageState())) {
                try {
                    var2 = var0.getExternalCacheDir().getPath();
                } catch (Exception var6) {
                    android.util.Log.e("cache", "[getDiskCacheDir]", var6);

                    try {
                        var2 = var0.getCacheDir().getPath();
                    } catch (Exception var5) {
                        android.util.Log.e("cache", "[getDiskCacheDir]", var5);
                    }
                }
            } else {
                try {
                    var2 = var0.getCacheDir().getPath();
                } catch (Exception var4) {
                    android.util.Log.e("cache", "[getDiskCacheDir]", var4);
                }
            }
        }

        return new File(var2 + File.separator + var1);
    }

    public static double showFileAvailable(Context var0) {
        String var1 = "";
        if("mounted".equals(Environment.getExternalStorageState())) {
            try {
                var1 = var0.getExternalCacheDir().getPath();
            } catch (Exception var7) {
                android.util.Log.e("[ImageCache]", "[getDiskCacheDir]", var7);

                try {
                    var1 = var0.getCacheDir().getPath();
                } catch (Exception var6) {
                    android.util.Log.e("[ImageCache]", "[getDiskCacheDir]", var6);
                }
            }
        } else {
            label33: {
                if(DeviceUtils.isZte()) {
                    var1 = "/mnt/sdcard-ext/";
                    if((new File(var1)).exists()) {
                        break label33;
                    }

                    var1 = "/mnt/sdcard/";
                    if((new File(var1)).exists()) {
                        break label33;
                    }
                }

                var1 = var0.getCacheDir().getPath();
            }
        }

        try {
            StatFs var2;
            long var3 = (long)(var2 = new StatFs(var1)).getBlockSize();
            return (double)((long)var2.getAvailableBlocks() * var3) / 1048576.0D;
        } catch (Exception var5) {

            return 0.0D;
        }
    }

    public static void saveBitmapLocation(Bitmap videoThumbnail, String resourceUrl) {

        if(TextUtils.isEmpty(resourceUrl)) return;
        if(null==videoThumbnail) return;
        File file = new File(Constant.IMAGE_PATH);
        if(!file.exists()&&!file.isDirectory()){
            file.mkdirs();
        }

        File filePath=new File(Constant.IMAGE_PATH,FileUtils.getFileName(resourceUrl));
        if(!filePath.exists()&&!filePath.isFile()){
            try {
                FileOutputStream fileOutputStream=new FileOutputStream(filePath);
                videoThumbnail.compress(Bitmap.CompressFormat.PNG,90,fileOutputStream);
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static Uri getUriForFile(Context context,File file) {
        if(null==file) return null;
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context, context.getPackageName()+".provider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    public interface IoListener{
        void onSuccess();
        void onFail();
    }

    /**
     * 拷贝 assets目录下制定文件到指定SD卡指定目录下
     * @param context
     * @param assetsPath
     * @param savePath
     */
    public static void copyFilesFromAssets(Context context, String assetsPath, String savePath,IoListener listener){
        try {
            InputStream is =context. getClass().getResourceAsStream(assetsPath);
//            InputStream is = context.getAssets().open(assetsPath);
            FileOutputStream fos = new FileOutputStream(new File(savePath));
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                // buffer字节
                fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
            }
            fos.flush();// 刷新缓冲区
            is.close();
            fos.close();
            if(null!=listener) listener.onSuccess();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            if(null!=listener) listener.onFail();
        }
    }
}