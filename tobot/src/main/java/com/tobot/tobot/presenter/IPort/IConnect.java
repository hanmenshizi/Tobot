package com.tobot.tobot.presenter.IPort;

/**
 * Created by Javen on 2017/8/8.
 */

public interface IConnect {
    void initialize(boolean toggle);
    void link();
    void shunt();
    void shuntVoice();
    void shut();
    void isLoad(boolean load);
    void onAgain();
}
