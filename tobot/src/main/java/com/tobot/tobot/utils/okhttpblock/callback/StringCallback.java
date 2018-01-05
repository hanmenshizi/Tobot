package com.tobot.tobot.utils.okhttpblock.callback;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by Javen on 17/9/26.
 */
public abstract class StringCallback extends Callback<String> {
    @Override
    public String parseNetworkResponse(Response response, int id) throws IOException {
        return response.body().string();
    }

}
