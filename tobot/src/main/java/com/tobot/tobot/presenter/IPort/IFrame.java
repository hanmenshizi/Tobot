package com.tobot.tobot.presenter.IPort;

import com.turing123.robotframe.RobotFrameManager;

/**
 * Created by Javen on 2017/12/7.
 */

public interface IFrame {
    void onInitiate(boolean whence);
    RobotFrameManager startRobotFramework() throws Exception;

}
