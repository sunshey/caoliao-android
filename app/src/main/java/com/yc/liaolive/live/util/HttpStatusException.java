package com.yc.liaolive.live.util;

/**
 * Version 1.0
 *
 * Date: 2013-10-29 17:43
 * Author: yonnielu
 *
 */

/**
 * Http状态异常
 */
public class HttpStatusException extends Exception {
    public HttpStatusException(String detailMessage) {
        super(detailMessage);
    }
}
