package edu.upenn.cis455.storage;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class User {

	// Primary key is pKey
	@PrimaryKey
	private String userName;
	private String password;
	
	public User() {
		
	}
	
	public User(String name, String pwd) {
		this.userName = name;
		this.password = pwd;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	@Override
	public String toString() {
		return this.userName + " : " + this.password;
	}
}
