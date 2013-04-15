package cn.successfactors.library.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public class SLUser implements Serializable {
	
	private String userName;
	private String userEmail;
	private String userPassword;
	private String userType;
	private String userDepartment;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getUserDepartment() {
		return userDepartment;
	}
	public void setUserDepartment(String userDepartment) {
		this.userDepartment = userDepartment;
	}
	
	public void parseMap(Map mapInfo) {

		this.setUserName(mapInfo.containsKey("userName")?(String)mapInfo.get("userName"):"");
		this.setUserEmail(mapInfo.containsKey("userEmail")?(String)mapInfo.get("userEmail"):"");
		this.setUserPassword(mapInfo.containsKey("userPassword")?(String)mapInfo.get("userPassword"):"");
		this.setUserType(mapInfo.containsKey("userType")?(String)mapInfo.get("userType"):"");
		this.setUserDepartment(mapInfo.containsKey("userDepartment")?(String)mapInfo.get("userDepartment"):"");
		
	}
	
	public Map toMap() {

		Map returnInfo = new HashMap();

		returnInfo.put("userName", userName);
		returnInfo.put("userEmail", userEmail);
		returnInfo.put("userPassword", userPassword);
		returnInfo.put("userType", userType);
		returnInfo.put("userDepartment", userDepartment);
		
		return returnInfo;
	}
	
}
