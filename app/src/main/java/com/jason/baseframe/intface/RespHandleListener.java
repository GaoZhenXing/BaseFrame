package com.jason.baseframe.intface;

/**
 * Created by Administrator on 2016/2/18.
 */
public interface RespHandleListener {

    class ErrCode {

        public static final int ERR_SUCCEED = 0;
        public static final int ERR_NETWORK_NOT_AVAILABLE = -1;
        public static final int ERR_TIME_OUT = -2;
        public static final int ERR_SERVER_ERROR = -3;
        public static final int ERR_CLIENT_ERROR = -4;
        public static final int ERR_UNKNOWN_ERROR = -5;

    }
    void onError(int code);
    void onReqBegin();
    void onReqEnd(String jsonResp);

}