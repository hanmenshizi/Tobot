package com.tobot.tobot.utils.okhttpblock.builder;


import com.tobot.tobot.utils.okhttpblock.OkHttpUtils;
import com.tobot.tobot.utils.okhttpblock.request.OtherRequest;
import com.tobot.tobot.utils.okhttpblock.request.RequestCall;

/**
 * Created by Javen on 17/9/26.
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
