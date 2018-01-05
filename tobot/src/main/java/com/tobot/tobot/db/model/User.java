package com.tobot.tobot.db.model;

import java.io.Serializable;
import java.util.List;

import com.tobot.tobot.sqlite.annotation.Column;
import com.tobot.tobot.sqlite.annotation.Id;
import com.tobot.tobot.sqlite.annotation.Table;

import android.content.Context;

/**
 * 用户数据
 */

@Table(name = "tab_tobot_user")
public class User implements Serializable {

	public static final long serialVersionUID = 4733464888738356502L;

	public static final String OFF_LINE = "0";

	public static final String ON_LINE = "1";
	
	@Id(name = "keyId")
	public String keyId = "tobot";

	@Column(name = "Account")
	public String Account;

	@Column(name = "Password")
	private String Password;

	@Column(name = "Ultr")
	private String Ultr;//机器人首次使用标记

	@Column(name = "UltrFack")
	private String UltrFack;//机器人伪首次使用标记

	@Column(name = "UltrAP")
	private String UltrAP = "3";//机器人首次AP联网使用标记 -- -- 0:正在连网/1:连接成功/2:ap连接失败/3:ap联网处于关闭状态

	@Column(name = "requestTime")
	private String requestTime;//列表获取时间

	@Column(name = "Mobile")
	private String Mobile = "0";//列表获取时间


	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}
	
	public String getAccount() { return Account; }

	public void setAccount(String account) {
		Account = account;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public String getUltr() { return Ultr; }

	public void setUltr(String ultr) { Ultr = ultr; }

	public String getUltrFack() {
		return UltrFack;
	}

	public void setUltrFack(String ultrFack) {
		UltrFack = ultrFack;
	}

	public String getUltrAP() { return UltrAP; }

	public void setUltrAP(String ultrAP) { UltrAP = ultrAP; }

	public String getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(String Time) {
		requestTime = Time;
	}

	public String getMobile() {
		return Mobile;
	}

	public void setMobile(String mobile) {
		Mobile = mobile;
	}




	@Override
	public boolean equals(Object object) {
		if (!(object instanceof User)) {
			return false;
		}

		final User obj = (User) object;
		if (Account != null ? !Account.equals(obj.Account)
				: obj.Account != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = 0;
		result = 29 * result + (Account != null ? Account.hashCode() : 0);
		result = 29 * result + (Password != null ? Password.hashCode() : 0);
		return result;
	}


}
