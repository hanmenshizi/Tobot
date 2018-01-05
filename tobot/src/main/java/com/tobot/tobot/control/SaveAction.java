package com.tobot.tobot.control;

import android.content.Context;

import com.tobot.tobot.MainActivity;
import com.tobot.tobot.base.UpdateAction;
import com.tobot.tobot.control.demand.DemandFactory;
import com.tobot.tobot.control.demand.DemandModel;
import com.tobot.tobot.utils.TobotUtils;
import com.tobot.tobot.utils.socketblock.SocketConnectCoherence;

/**
 * Created by Javen on 2017/10/23.
 */

public class SaveAction {
    private Context mContext;
    private UpdateAction mUpdateAction;

    public SaveAction(Context context, UpdateAction updateAction) {
        this.mUpdateAction = updateAction;
        this.mContext = context;
    }

    public void setDanceResource() {
        if (mUpdateAction != null) {
            mUpdateAction.setSaveDanceResource(new UpdateAction.SavaDanceResource() {
                @Override
                public void save(String demand) {
                    //功能实现
                    try {
                        DemandFactory demandFactory = DemandFactory.getInstance(mContext);
                        demandFactory.downloadDanceResource(demand);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void setActionResource() {
        if (mUpdateAction != null) {
            mUpdateAction.setSavaActionResource(new UpdateAction.SavaActionResource() {
                @Override
                public void save(String demand) {

                }
            });
        }
    }


}
