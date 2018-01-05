package com.tobot.tobot.db.bean;

import android.util.Log;

import com.tobot.tobot.base.Constants;
import com.tobot.tobot.db.BaseDBManager;
import com.tobot.tobot.db.model.Answer;
import com.tobot.tobot.db.model.Memory;

import java.util.List;

/**
 * Created by Javnet on 2017/12/15.
 */

public class MemoryDBManager extends BaseDBManager<Memory> {
    static MemoryDBManager manager;

    public MemoryDBManager() {
        super(Memory.class);
    }

    public static MemoryDBManager getManager() {
        if (manager == null) {
            manager = new MemoryDBManager();
        }
        return manager;
    }

    public void insert(Memory obj) {
        Long l = mBeanDao.insert(obj);
        Log.e("Javen","insert l:"+l);
    }

    public void insert(List<Memory> list) {
        for (Memory obj : list) {
            mBeanDao.insert(obj);
        }
    }

    public Memory queryById(String keyId) {
        return mBeanDao.get(keyId);
    }

    public Memory queryByElement(Object keyId,Object fields) {
        try {
            return mBeanDao.queryByElement(keyId,fields);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //新增加根据id和元素模糊查询
    public Memory queryByElement(Object fields) {
        try {
            return mBeanDao.queryByElement(fields);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertOrUpdate(List<Memory> TAB) {
        for (Memory obj : TAB) {
            insertOrUpdate(obj);
        }
    }

    public void insertOrUpdate(Memory obj) {
        delete(obj);
        mBeanDao.insert(obj);
    }

    public void delete(Memory obj) {
        mBeanDao.delete(obj.getKeyId());
    }


    public List<Memory> queryList() {
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

    public Memory getCurrentMemory() {
        List<Memory> list = mBeanDao.queryList();
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
