package com.tobot.tobot.db.bean;

import com.tobot.tobot.base.Constants;
import com.tobot.tobot.db.model.Answer;
import com.tobot.tobot.db.BaseDBManager;

import java.util.List;

/**
 * Created by Javen on 2017/12/13.
 */

public class AnswerDBManager extends BaseDBManager<Answer> {
    static AnswerDBManager manager;

    public AnswerDBManager() {
        super(Answer.class);
    }

    public static AnswerDBManager getManager() {
        if (manager == null) {
            manager = new AnswerDBManager();
        }
        return manager;
    }

    public void insert(Answer obj) {
        Long l = mBeanDao.insert(obj);
    }

    public void insert(List<Answer> list) {
        for (Answer obj : list) {
            mBeanDao.insert(obj);
        }
    }

    public Answer queryById(String keyId) {
        return mBeanDao.get(keyId);
    }

    //新增加根据元素模糊查询
    public Answer queryByElement(String fields) {
        try {
            return mBeanDao.queryByElement(fields);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertOrUpdate(List<Answer> TAB) {
        for (Answer obj : TAB) {
            insertOrUpdate(obj);
        }
    }

    public void insertOrUpdate(Answer obj) {
        delete(obj);
        mBeanDao.insert(obj);
    }

    public void delete(Answer obj) {
        mBeanDao.delete(obj.getKeyId());
    }

    public List<Answer> queryList() {
        return mBeanDao.queryList();
    }

    public void clear() {
        mBeanDao.truncate();
    }

    public static void destory() {
        if (manager == null) {
            return;
        }
        manager.close();
        manager = null;
    }

    public Answer getCurrentAnswer() {
        List<Answer> list = mBeanDao.queryList();
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public String getDatabaseName() {
        return Constants.TOBOT_DB_NAME;
    }


}