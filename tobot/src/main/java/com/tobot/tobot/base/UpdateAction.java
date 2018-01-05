package com.tobot.tobot.base;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.tobot.tobot.control.demand.DemandModel;
import com.tobot.tobot.db.bean.UserDBManager;
import com.tobot.tobot.db.model.User;
import com.tobot.tobot.entity.ActionEntity;
import com.tobot.tobot.utils.AppTools;
import com.tobot.tobot.utils.SHA1;
import com.tobot.tobot.utils.TobotUtils;
import com.tobot.tobot.utils.Transform;
import com.tobot.tobot.utils.okhttpblock.OkHttpUtils;
import com.tobot.tobot.utils.okhttpblock.callback.StringCallback;
import com.tobot.tobot.utils.socketblock.SocketConnectCoherence;

import okhttp3.Call;

/**
 * Created by Javen on 2017/10/20.
 */

public class UpdateAction {
    private Context mContext;
    private String uuid;
    private SavaDanceResource saveDanceResource;
    private SavaActionResource savaActionResource;

    public UpdateAction(Context context){
        this.mContext = context;
    }

    public void getList(){
        if (AppTools.netWorkAvailable(mContext)) {
            getActionList();
            getDanceList();
        }
    }

    private void getActionList() {
        uuid = Transform.getGuid();
        OkHttpUtils.get()
                .url(Constants.ACTION_LIST + uuid + "/" + SHA1.gen(Constants.identifying + uuid))
                .addParams("nonce", uuid)//伪随机数
                .addParams("sign", SHA1.gen(Constants.identifying + uuid))//签名
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i("Javen","获取动作列表失败:"+call);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i("Javen","获取动作列表成功:"+response);
//                        ActionEntity actionList = new Gson().fromJson(response, ActionEntity.class);
                        //保存时间
                        User user = new User();
                        user.setRequestTime(TobotUtils.getCurrentlyDate());
                        UserDBManager.getManager().insertOrUpdate(user);
                        if (savaActionResource!=null){
                            savaActionResource.save(response);
                        }
                    }
                });
    }


    private void getDanceList() {
        uuid = Transform.getGuid();
        OkHttpUtils.get()
                .url(Constants.DANCE_LIST + uuid + "/" + SHA1.gen(Constants.identifying + uuid))
                .addParams("nonce", uuid)//伪随机数
                .addParams("sign", SHA1.gen(Constants.identifying + uuid))//签名
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i("Javen","获取舞蹈列表失败:"+call);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i("Javen","获取舞蹈列表成功:"+response);
//                        ActionEntity danceList = new Gson().fromJson(response, ActionEntity.class);
                       if (saveDanceResource != null){
                           saveDanceResource.save(response);
                       }
                    }
                });
    }


    public SavaDanceResource getSaveDanceResource() {
        return saveDanceResource;
    }

    public void setSaveDanceResource(SavaDanceResource saveDanceResource) {
        this.saveDanceResource = saveDanceResource;
    }

    public SavaActionResource getSavaActionResource() {
        return savaActionResource;
    }

    public void setSavaActionResource(SavaActionResource savaActionResource) {
        this.savaActionResource = savaActionResource;
    }

    public interface SavaDanceResource{
        void save(String demand);
    };

    public interface SavaActionResource{
        void save(String demand);
    };


}
