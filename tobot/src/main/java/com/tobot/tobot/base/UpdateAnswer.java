package com.tobot.tobot.base;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tobot.tobot.db.bean.AnswerDBManager;
import com.tobot.tobot.db.model.Answer;
import com.tobot.tobot.utils.SHA1;
import com.tobot.tobot.utils.TobotUtils;
import com.tobot.tobot.utils.Transform;
import com.tobot.tobot.utils.okhttpblock.OkHttpUtils;
import com.tobot.tobot.utils.okhttpblock.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Call;

/**
 * Created by Javen on 2017/12/11.
 */

public class UpdateAnswer {
    private String TAG = "Javen UpdateAnswer";
    private String uuid;

    public UpdateAnswer(){
        getAnswer();
    }

    private void getAnswer(){
        uuid = Transform.getGuid();
        OkHttpUtils.get()
                .url(Constants.ANSWER_LIST + uuid + "/" + SHA1.gen(Constants.identifying + uuid))
                .addParams("nonce", uuid)//伪随机数
                .addParams("sign", SHA1.gen(Constants.identifying + uuid))//签名
                .addParams("robotId", TobotUtils.getDeviceId(Constants.DeviceId, Constants.Path))//机器人设备ID
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i("Javen","获取问答列表失败:"+e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i("Javen","获取问答列表成功:"+response);
                        try {
                            JSONObject  jsonObject = new JSONObject (response);
                            List<Answer> answerList = new Gson().fromJson(
                                    jsonObject.getString("data"),
                                    new TypeToken<List<Answer>>() {
                                    }.getType());
                            if (answerList.size() > 0){
                                AnswerDBManager.getManager().clear();
                                for (Answer answers : answerList){
                                    Log.i(TAG,"队列循环中");
                                    answers.setQuestion(answers.getQuestion().replaceAll("[\\p{P}‘’“”]", ""));
                                    answers.setKeyId(Transform.getGuid());
                                }
                                AnswerDBManager.getManager().insert(answerList);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
