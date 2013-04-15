package cn.successfactors.library.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public class SLOrder implements Serializable {
	
	private int orderId;
	private String userEmail;
	private String bookISBN;
	private String orderDate;
	private String status;
	
	//关联实体
	private SLBook theBook;
	private SLUser theUser;
	
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getBookISBN() {
		return bookISBN;
	}
	public void setBookISBN(String bookISBN) {
		this.bookISBN = bookISBN;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public SLBook getTheBook() {
		return theBook;
	}
	public void setTheBook(SLBook theBook) {
		this.theBook = theBook;
	}
	public SLUser getTheUser() {
		return theUser;
	}
	public void setTheUser(SLUser theUser) {
		this.theUser = theUser;
	}
	
	public void parseMap(Map mapInfo) {

		this.setOrderId(mapInfo.containsKey("orderId")?(Integer)mapInfo.get("orderId"):null);
		this.setUserEmail(mapInfo.containsKey("userEmail")?(String)mapInfo.get("userEmail"):"");
		this.setBookISBN(mapInfo.containsKey("bookISBN")?(String)mapInfo.get("bookISBN"):"");
		this.setOrderDate(mapInfo.containsKey("orderDate")?(String)mapInfo.get("orderDate"):null);
		this.setStatus(mapInfo.containsKey("status")?(String)mapInfo.get("status"):"");
		
	}
	
	public Map toMap() {

		Map returnInfo = new HashMap();
		
		returnInfo.put("icon", "reports.png");
		returnInfo.put("orderId", orderId);
		returnInfo.put("userEmail", userEmail);
		returnInfo.put("bookISBN", bookISBN);
		returnInfo.put("bookISBN", bookISBN);
		returnInfo.put("orderDate",orderDate);
		returnInfo.put("status", status);
		
		//------------------------------------------------------
		if (theUser != null) {
			returnInfo.put("userName", theUser.getUserName());
		}
		if (theBook != null) {
			returnInfo.put("bookName", theBook.getBookName());
			returnInfo.put("bookPicUrl", theBook.getBookPicUrl());
		}
		
		return returnInfo;
	}
	
	
}
