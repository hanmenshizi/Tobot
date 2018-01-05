package com.tobot.tobot.utils.okhttpblock.utils;

/**
 * Created by Javen on 17/9/26.
 */
public class Exceptions
{
    public static void illegalArgument(String msg, Object... params)
    {
        throw new IllegalArgumentException(String.format(msg, params));
    }


}
