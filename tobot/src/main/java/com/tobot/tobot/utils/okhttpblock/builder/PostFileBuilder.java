package com.tobot.tobot.utils.okhttpblock.builder;

import com.tobot.tobot.utils.okhttpblock.request.PostFileRequest;
import com.tobot.tobot.utils.okhttpblock.request.RequestCall;

import java.io.File;

import okhttp3.MediaType;

/**
 * Created by Javen on 17/9/26.
 */
public class PostFileBuilder extends OkHttpRequestBuilder<PostFileBuilder> {
    private File file;
    private MediaType mediaType;


    public OkHttpRequestBuilder file(File file) {
        this.file = file;
        return this;
    }

    public OkHttpRequestBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    @Override
    public RequestCall build() {
        return new PostFileRequest(url, tag, params, headers, file, mediaType, id).build();
    }


}
