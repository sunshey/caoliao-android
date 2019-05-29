package com.kaikai.securityhttp.net.interfaces;


import com.kaikai.securityhttp.net.entry.Response;
import com.kaikai.securityhttp.net.entry.UpFileInfo;
import com.kaikai.securityhttp.net.exception.NullResonseListenerException;
import com.kaikai.securityhttp.net.listeners.OnHttpResonseListener;

import java.io.IOException;
import java.util.Map;

import okhttp3.MediaType;

/**
 * Created by zhangkai on 16/8/18.
 */
public interface IHttpRequest {

    Response get(String url, Map<String, String> params, Map<String, String> headers, boolean isEncryptResponse) throws
            IOException;

    void aget(String url, Map<String, String> params, Map<String, String> headers, boolean isEncryptResponse, final OnHttpResonseListener
            httpResonseListener) throws IOException,
            NullResonseListenerException;

    Response post(String url, Map<String, String> params, Map<String, String> headers, boolean isrsa, boolean iszip,
                  boolean
                          isEncryptResponse) throws IOException, NullResonseListenerException;

    public Response post(String url, Map<String, String> header, MediaType type, String
            body) throws IOException, NullResonseListenerException;

    void apost(String url, Map<String, String> params, Map<String, String> headers, boolean isrsa, boolean iszip,
               boolean
                       isEncryptResponse, final OnHttpResonseListener httpResonseListener)
            throws
            IOException, NullResonseListenerException;

    Response uploadFile(String url, UpFileInfo upFileInfo, Map<String, String> params, Map<String, String> headers,
                        boolean
                                isEncryptResponse
    ) throws IOException;


    void auploadFile(String url, UpFileInfo upFileInfo, Map<String, String> params, Map<String, String> headers, boolean
            isEncryptResponse,
                     OnHttpResonseListener
                             httpResonseListener) throws IOException, NullResonseListenerException;


    void cancel(String url);

}
