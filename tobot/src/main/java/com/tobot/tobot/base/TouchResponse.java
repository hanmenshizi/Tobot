package com.tobot.tobot.base;

import android.content.Context;

import com.tobot.tobot.R;

import java.util.Random;

/**
 * Created by Javen on 2017/8/18.
 */

public class TouchResponse {

    private static Random random = new Random();

    public static String getResponse(Context context){
        switch (Math.abs(random.nextInt())%10){
            case 0:
                return context.getResources().getString(R.string.voice0);
            case 1:
                return context.getResources().getString(R.string.voice1);
            case 2:
                return context.getResources().getString(R.string.voice2);
            case 3:
                return context.getResources().getString(R.string.voice3);
            case 4:
                return context.getResources().getString(R.string.voice4);
            case 5:
                return context.getResources().getString(R.string.voice5);
            case 6:
                return context.getResources().getString(R.string.voice6);
            case 7:
                return context.getResources().getString(R.string.voice7);
            case 8:
                return context.getResources().getString(R.string.voice8);
            case 9:
                return context.getResources().getString(R.string.voice9);
        }
        return null;
    }

}
