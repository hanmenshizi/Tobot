package com.tobot.tobot.db.bean;

import android.util.Log;

import java.util.HashMap;
import java.util.List;

import com.tobot.tobot.base.Constants;
import com.tobot.tobot.db.BaseDBManager;
import com.tobot.tobot.db.model.Memory;
import com.tobot.tobot.db.model.User;
import com.tobot.tobot.utils.TobotUtils;


public class UserDBManager extends BaseDBManager<User> {

	static UserDBManager manager;

	private UserDBManager() {
		super(User.class);
	}

	public static UserDBManager getManager() {
		if (manager == null) {
			Log.i("Javen_UserDBManager","UserDBManager为空");
			manager = new UserDBManager();
		}
		return manager;
	}

	public void insert(User obj) {
		clear();
		mBeanDao.insert(obj);

	}

	public User getCurrentUser() {
		List<User> list = mBeanDao.queryList();
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

//	public User getUserByPhone(String phone) {
//		HashMap<String, String> map = new HashMap<String, String>();
//		if (CompileUtil.IsPhone(phone)) {
//			map.put("phone", phone);
//		} else {
//			map.put("account", phone);
//		}
//		List<User> list = mBeanDao.queryList(map);
//		if (list == null || list.isEmpty()) {
//			return null;
//		} else {
//			return list.get(0);
//		}
//	}
	
	public void insertOrUpdate(List<User> TAB) {
		for (User obj : TAB) {
			insertOrUpdate(obj);
		}
	}
	
	public void insertOrUpdate(User obj) {
//		delete(obj);
//		mBeanDao.insert(obj);
		if (TobotUtils.isNotEmpty(mBeanDao.get("tobot"))) {
			Log.i("Javen","insertOrUpdate update");
			mBeanDao.update(obj);
		} else {
			Log.i("Javen","insertOrUpdate insert");
			clear();
			mBeanDao.insert(obj);
		}

	}

	public User queryById(String keyId) {
		return mBeanDao.get(keyId);
	}

	public List<User> queryList() {
		return mBeanDao.queryList();
	}

	public void clear() {
		mBeanDao.truncate();
	}
	
	public void delete(User obj) {
		mBeanDao.delete(obj.getKeyId());
	}
	
	public static void destory() {
		if (manager == null) {
			return;
		}
		manager.close();
		manager = null;
	}


	@Override
	public String getDatabaseName() {
		return Constants.TOBOT_DB_NAME;
	}
	
	
}
