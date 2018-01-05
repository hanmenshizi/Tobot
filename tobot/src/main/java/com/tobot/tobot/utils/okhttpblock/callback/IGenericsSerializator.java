package com.tobot.tobot.utils.okhttpblock.callback;

/**
 * Created by Javen on 17/9/26.
 */
public interface IGenericsSerializator {
    <T> T transform(String response, Class<T> classOfT);
}
