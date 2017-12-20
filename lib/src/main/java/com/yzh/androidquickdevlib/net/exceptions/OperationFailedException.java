package com.yzh.androidquickdevlib.net.exceptions;

/**
 * 网络请求失败异常
 */
public class OperationFailedException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1376652137646725671L;

    public OperationFailedException() {
    }

    public OperationFailedException(String detailMessage) {
        super(detailMessage);
    }

    public OperationFailedException(Throwable throwable) {
        super(throwable);
    }

    public OperationFailedException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
