package com.tobot.tobot.control.demand;

/**
 * Created by YF-04 on 2017/11/14.
 */

public interface DownloadBehavior {

    public static final int DOWNLOAD_SUCCESS=897131;
    public static final int DOWNLOAD_FAILED=3664625;
    void download() throws Exception;
}
