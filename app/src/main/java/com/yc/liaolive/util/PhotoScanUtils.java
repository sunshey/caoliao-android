package com.yc.liaolive.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.bean.ImageDirInfo;
import com.yc.liaolive.bean.ImageInfo;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * TinyHung@Outlook.com
 * 2018/9/19
 * 本地相册扫描、文件夹分割
 */

public class PhotoScanUtils {

    private static final String TAG = "PhotoScanUtils";
    private static PhotoScanUtils mInstance;
    private boolean isRuning=false;

    public static synchronized PhotoScanUtils getInstance(){
        synchronized (PhotoScanUtils.class){
            if(null==mInstance){
                mInstance=new PhotoScanUtils();
            }
        }
        return mInstance;
    }

    /**
     * 查询本机所有、部分照片
     * @param dirPath 路径 为空获取所有照片
     * @param isGroups 是否分组
     */
    public void getLocationImages(final String dirPath, final boolean isGroups){
        if(isRuning){
            if(null!=mOnScanListener) mOnScanListener.onFail(-1,"正在扫描中...");
            return;
        }
        new Thread(){
            @Override
            public void run() {
                super.run();
                isRuning=true;
                final List<ImageInfo> imageLists=new ArrayList<>();
                ContentResolver resolver= AppEngine.getApplication().getApplicationContext().getContentResolver();
                if(null!=resolver){
                    Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    // 查询的字段
                    String[] projection = { MediaStore.Images.Media._ID,
                            MediaStore.Images.Media.DISPLAY_NAME,
                            MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE };
////                      // 条件
//                        String selection = MediaStore.Images.Media.MIME_TYPE + "=?";
//                        //过滤文件类型
//                        String[] selectionArgs = { "image/jpeg" };
////                      //按照文件上一次修改的时间排序
//                        String sortOrder = MediaStore.Images.Media.DATE_MODIFIED;
//                        // 查询sd卡上的图片
                    Cursor cursor = resolver.query(uri, projection, null, null, null);
//                        Cursor cursor=resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, selection, selectionArgs, sortOrder);
                    long millis = System.currentTimeMillis();
                    while(cursor.moveToNext()) {
                        //获取图片的名称
                        String name=getCursorParams(cursor,cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                        //获取图片的大小
                        String size=getCursorParams(cursor,cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                        //获取图片的路径
                        String path=getCursorParams(cursor,cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        //最后一次修改文件的时间
                        String modifTime=getCursorParams(cursor,cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
                        ImageInfo imageInfo=new ImageInfo();
                        if(!TextUtils.isEmpty(modifTime))imageInfo.setFileCreazeTime(Long.parseLong(modifTime));
                        imageInfo.setItemType(0);
                        try {
                            imageInfo.setFileSize(Long.parseLong(size));
                        }catch (NumberFormatException e){
                            e.printStackTrace();
                        }finally {
                            imageInfo.setFileName(name);
                            imageInfo.setFilePath(path);
                            //加载全部
                            if(null==dirPath){
                                imageLists.add(imageInfo);
                                //只加载对应路径下文件
                            }else{
                                File file=new File(path);
                                String folder = file.getParent();
                                if(folder.equals(dirPath)){
                                    imageLists.add(imageInfo);
                                }
                            }
                        }
                    }
                    cursor.close();
                    //所有照片排序
                    Collections.sort(imageLists, new Comparator<ImageInfo>() {
                        @Override
                        public int compare(ImageInfo o1, ImageInfo o2) {
                            return o2.getFileCreazeTime().compareTo(o1.getFileCreazeTime());
                        }
                    });
                    isRuning=false;
                    //主界面先渲染画面
                    if(null!=mOnScanListener) mOnScanListener.onSuccess(imageLists,dirPath);

                    if(!isGroups) return;
                    try {
                        long millis3 = System.currentTimeMillis();
                        HashMap<String,ImageDirInfo> folders=new HashMap<>();
                        //对数据分组
                        for (int i = 0; i < imageLists.size(); i++) {
                            if(null!=imageLists.get(i).getFilePath()){
                                String filePath = imageLists.get(i).getFilePath();
                                File file = new File(filePath);
                                if (null!=file && file.canRead()) {
                                    String folder = file.getParent();
                                    String folderKey = folder.toLowerCase(Locale.CHINESE);
                                    ImageDirInfo dirInfo;
                                    if (!folders.containsKey(folderKey)) {
                                        dirInfo = new ImageDirInfo();
                                        dirInfo.setDirPath(folder);
                                        dirInfo.setDirName(FileUtils.getFileName(folder));
                                        dirInfo.setFilePath(filePath);
                                        dirInfo.count++;
                                        folders.put(folderKey, dirInfo);
                                    } else if(folders.containsKey(folderKey)){
                                        dirInfo = folders.get(folderKey);
                                        if(null!=dirInfo) dirInfo.count++;
                                    }
                                }
                            }
                        }
                        HashSet<ImageDirInfo> resultSet = new HashSet<ImageDirInfo>(folders.values());
                        List<ImageDirInfo> result = new ArrayList<ImageDirInfo>(resultSet);
                        //对已分组的相册列表排序
                        Collections.sort(result, new Comparator<ImageDirInfo>() {
                            @Override
                            public int compare(ImageDirInfo lhs, ImageDirInfo rhs) {
                                return lhs.getDirName().compareTo(rhs.getDirName());
                            }
                        });
                        ImageDirInfo imageDirInfo = new ImageDirInfo();
                        imageDirInfo.setDirName("全部照片");
                        imageDirInfo.setDirPath(null);
                        imageDirInfo.setSelected(true);
                        imageDirInfo.count=imageLists.size();
                        result.add(0,imageDirInfo);
                        if(null!=mOnScanListener) mOnScanListener.onTabs(result);
                    }catch (RuntimeException e){
                        e.printStackTrace();
                        isRuning=false;
                    }
                }else{
                    if(null!=mOnScanListener) mOnScanListener.onSuccess(imageLists,dirPath);
                }
            }
        }.start();
    }

    /**
     * 根据路径获取多媒体文件参数信息
     * @param cursor
     * @param columnIndex
     * @return
     */
    private String getCursorParams(Cursor cursor, int columnIndex) {
        if(null==cursor) return "0";
        if(-1==columnIndex||0==columnIndex) return "0";
        return cursor.getString(columnIndex);
    }

    public void onDestroy() {
        isRuning=false;
        mOnScanListener=null;
    }

    public interface OnScanListener{
        void onFail(int code,String errorMsg);
        void onTabs(List<ImageDirInfo> tabs);
        void onSuccess(List<ImageInfo> imageInfos,String dirPath);
    }
    private OnScanListener mOnScanListener;

    public PhotoScanUtils setOnScanListener(OnScanListener onScanListener) {
        mOnScanListener = onScanListener;
        return mInstance;
    }
}