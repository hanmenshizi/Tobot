package com.tobot.tobot.utils.okhttpblock.builder;

import com.tobot.tobot.utils.okhttpblock.request.PostStringRequest;
import com.tobot.tobot.utils.okhttpblock.request.RequestCall;

import okhttp3.MediaType;

/**
 * Created by Javen on 17/9/26.
 */
public class PostStringBuilder extends OkHttpRequestBuilder<PostStringBuilder> {
    private String content;
    private MediaType mediaType;


    public PostStringBuilder content(String content) {
        this.content = content;
        return this;
    }

    public PostStringBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    @Override
    public RequestCall build() {
        return new PostStringRequest(url, tag, params, headers, content, mediaType, id).build();
    }


}
