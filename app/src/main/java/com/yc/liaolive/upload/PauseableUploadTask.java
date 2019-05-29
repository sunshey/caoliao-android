package com.yc.liaolive.upload;

import android.text.TextUtils;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.utils.IOUtils;
import com.alibaba.sdk.android.oss.model.CompleteMultipartUploadRequest;
import com.alibaba.sdk.android.oss.model.CompleteMultipartUploadResult;
import com.alibaba.sdk.android.oss.model.InitiateMultipartUploadRequest;
import com.alibaba.sdk.android.oss.model.InitiateMultipartUploadResult;
import com.alibaba.sdk.android.oss.model.ListPartsRequest;
import com.alibaba.sdk.android.oss.model.ListPartsResult;
import com.alibaba.sdk.android.oss.model.PartETag;
import com.alibaba.sdk.android.oss.model.PartSummary;
import com.alibaba.sdk.android.oss.model.UploadPartRequest;
import com.alibaba.sdk.android.oss.model.UploadPartResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;

/**
 * TinyHung@Outlook.com
 * 2017/9/4.
 * OSS文件分片上传任务
 */
public class PauseableUploadTask {

    public static final String TAG="PauseableUploadTask";
    private OSS oss;
    private PauseableUploadRequest request;
    private OSSCompletedCallback<PauseableUploadRequest, PauseableUploadResult> callback;
    private List<PartETag> partETags = new ArrayList<>();

    private long currentUploadLength = 0;
    private long fileLength = 0;

    private boolean isPaused = false;
    private boolean isComplete = false;
    private Map<String, String> mParams;
    private Map<String, String> mParamVars;

    public PauseableUploadTask(OSS oss, PauseableUploadRequest request, OSSCompletedCallback<PauseableUploadRequest, PauseableUploadResult> callback) {
        this.oss = oss;
        this.request = request;
        this.callback = callback;
    }

    public synchronized void pause() {
        isPaused  = true;
    }
    public synchronized boolean isPause() {
        return isPaused;
    }

    public synchronized void setComplete() { isComplete = true; }
    public synchronized boolean isComplete() { return isComplete; }

    //使用指定的uploadId进行上传
    public void upload(String uploadId){
        if(!TextUtils.isEmpty(uploadId)){
            try {
                final String bucket = request.getBucket();
                final String object = request.getObjectKey();
                final String localFile = request.getLocalFile();
                final int partSize = request.getPartSize();
                //先通过uploadId去看对应的Id的Parts情况
                ListPartsRequest listParts = new ListPartsRequest(bucket, object, uploadId);
                ListPartsResult listResult = oss.listParts(listParts);

                for (PartSummary part : listResult.getParts()) {
                    partETags.add(new PartETag(part.getPartNumber(), part.getETag()));
                }

                long blockSize = partSize;
                int currentUploadIndex = partETags.size() + 1;
                File uploadFile = new File(localFile);
                fileLength = uploadFile.length();
                int totalBlockNum;

                final OSSProgressCallback<PauseableUploadRequest> progressCallback = request.getProgressCallback();

                totalBlockNum = (int) (fileLength / blockSize) + (fileLength % blockSize == 0 ? 0 : 1);
                if (currentUploadIndex <= totalBlockNum) {
                    currentUploadLength = blockSize * (currentUploadIndex - 1);
                } else {
                    currentUploadLength = fileLength;
                }

                InputStream in = new FileInputStream(uploadFile);

                long at = 0;
                while (at < currentUploadLength) {
                    long realSkip = in.skip(currentUploadLength - at);
                    if (realSkip == -1) {
                        throw new IOException("Skip failed! [fileLength]: " + fileLength + " [needSkip]: " + currentUploadLength);
                    }
                    at += realSkip;
                }

                while (currentUploadIndex <= totalBlockNum) {
                    UploadPartRequest uploadPart = new UploadPartRequest(bucket, object, uploadId, currentUploadIndex);

                    uploadPart.setProgressCallback(new OSSProgressCallback<UploadPartRequest>() {
                        //通过UploadPart的进度回调来换算整体的进度
                        @Override
                        public void onProgress(UploadPartRequest request, long currentSize, long totalSize) {

                            if (progressCallback != null) {
                                progressCallback.onProgress(PauseableUploadTask.this.request, currentUploadLength + currentSize, fileLength);
                            }
                        }
                    });

                    int toUpload = (int) Math.min(blockSize, fileLength - currentUploadLength);
                    uploadPart.setPartContent(IOUtils.readStreamAsBytesArray(in, toUpload));
                    UploadPartResult uploadPartResult = oss.uploadPart(uploadPart);

                    partETags.add(new PartETag(currentUploadIndex, uploadPartResult.getETag()));

                    currentUploadLength += toUpload;
                    currentUploadIndex++;

                    //如果暂停了就直接退出，需要断点续传的话直接使用同样的参数构建任务上传即可

                    if (isPause()) {

                        return;
                    }
                }

                CompleteMultipartUploadRequest complete = new CompleteMultipartUploadRequest(bucket, object, uploadId, partETags);

                //回调参数
                if(null!=mParams&&mParams.size()>0){
                    complete.setCallbackParam(mParams);
                }
                if(null!=mParamVars&&mParamVars.size()>0){
                    complete.setCallbackVars(mParamVars);
                }

                CompleteMultipartUploadResult completeResult = oss.completeMultipartUpload(complete);

                PauseableUploadResult result = new PauseableUploadResult(completeResult);
                setComplete();
                callback.onSuccess(request, result);
            }
            catch (IOException e) {
                ClientException clientException = new ClientException(e.toString(), e);
                callback.onFailure(request, clientException, null);
            }
            catch (ServiceException e) {
                callback.onFailure(request, null, e);
            }
            catch (ClientException e) {
                callback.onFailure(request, e, null);
            }
            catch (ConcurrentModificationException e){

            }
        }
    }

    /**
     * 设置上传参数
     * @param params
     * @param paramsVar
     */
    public void setUserPrams(Map<String,String> params,Map<String,String> paramsVar){
        this.mParams=params;
        this.mParamVars=paramsVar;
    }


    //如果是第一次上传，需要初始化uploadId
    public String initUpload() throws ClientException, ServiceException {
        try {
            if(null!=request){
                String bucket = request.getBucket();
                String object = request.getObjectKey();
                InitiateMultipartUploadRequest init = new InitiateMultipartUploadRequest(bucket, object);
                InitiateMultipartUploadResult initResult = oss.initMultipartUpload(init);
                return initResult.getUploadId();
            }
            return "0";
        }
        catch (ServiceException e) {
            if(null!=callback)callback.onFailure(request, null, e);

            throw e;
        }
        catch (ClientException e) {
            if(null!=callback&&null!=request) callback.onFailure(request, e, null);
            throw e;
        }
    }
}
