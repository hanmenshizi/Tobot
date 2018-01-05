package com.tobot.tobot.utils.okhttpblock.builder;

import java.util.Map;

/**
 * Created by Javen on 17/9/26.
 */
public interface HasParamsable
{
    OkHttpRequestBuilder params(Map<String, String> params);
    OkHttpRequestBuilder addParams(String key, String val);
}
