package cn.successfactors.library.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public class SLRecommendedBook implements Serializable {
	
	private String bookName;
	private String bookAuthor;
	private String bookISBN;
	private String bookPublisher;
	private String bookPublishDate;
	private String bookLanguage;
	private double bookPrice;
	private String bookClass;
	private String bookContributor;
	private String bookIntro;
	private String bookPicUrl;
	
	private String recUserName;
	private String recUserEmail;
	private String recStatus;
	private String recDate;
	private int recRate;
	private double countPrice;
	
	private String sessionKey;
	
	public String getSessionKey() {
		return sessionKey;
	}
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
	public String getBookName() {
		return bookName;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	public String getBookAuthor() {
		return bookAuthor;
	}
	public void setBookAuthor(String bookAuthor) {
		this.bookAuthor = bookAuthor;
	}
	public String getBookISBN() {
		return bookISBN;
	}
	public void setBookISBN(String bookISBN) {
		this.bookISBN = bookISBN;
	}
	public String getBookPublisher() {
		return bookPublisher;
	}
	public void setBookPublisher(String bookPublisher) {
		this.bookPublisher = bookPublisher;
	}
	public String getBookPublishDate() {
		return bookPublishDate;
	}
	public void setBookPublishDate(String bookPublishDate) {
		this.bookPublishDate = bookPublishDate;
	}
	public String getBookLanguage() {
		return bookLanguage;
	}
	public void setBookLanguage(String bookLanguage) {
		this.bookLanguage = bookLanguage;
	}
	public double getBookPrice() {
		return bookPrice;
	}
	public void setBookPrice(double bookPrice) {
		this.bookPrice = bookPrice;
	}
	public String getBookClass() {
		return bookClass;
	}
	public void setBookClass(String bookClass) {
		this.bookClass = bookClass;
	}
	public String getBookContributor() {
		return bookContributor;
	}
	public void setBookContributor(String bookContributor) {
		this.bookContributor = bookContributor;
	}
	public String getBookIntro() {
		return bookIntro;
	}
	public void setBookIntro(String bookIntro) {
		this.bookIntro = bookIntro;
	}
	public String getBookPicUrl() {
		return bookPicUrl;
	}
	public void setBookPicUrl(String bookPicUrl) {
		this.bookPicUrl = bookPicUrl;
	}
	public String getRecUserEmail() {
		return recUserEmail;
	}
	public void setRecUserEmail(String recUserEmail) {
		this.recUserEmail = recUserEmail;
	}
	public String getRecStatus() {
		return recStatus;
	}
	public void setRecStatus(String recStatus) {
		this.recStatus = recStatus;
	}
	public String getRecDate() {
		return recDate;
	}
	public void setRecDate(String recDate) {
		this.recDate = recDate;
	}
	
	public String getRecUserName() {
		return recUserName;
	}
	public void setRecUserName(String recUserName) {
		this.recUserName = recUserName;
	}
	public int getRecRate() {
		return recRate;
	}
	public void setRecRate(int recRate) {
		this.recRate = recRate;
	}
	public double getCountPrice() {
		return countPrice;
	}
	public void setCountPrice(double countPrice) {
		this.countPrice = countPrice;
	}
	
	public void parseMap(Map mapInfo) {
		
		this.setBookName(mapInfo.containsKey("bookName")?(String)mapInfo.get("bookName"):"");
		this.setBookAuthor(mapInfo.containsKey("bookAuthor")?(String)mapInfo.get("bookAuthor"):"");
		this.setBookISBN(mapInfo.containsKey("bookISBN")?(String)mapInfo.get("bookISBN"):"");
		this.setBookPublisher(mapInfo.containsKey("bookPublisher")?(String)mapInfo.get("bookPublisher"):"");
		this.setBookPublishDate(mapInfo.containsKey("bookPublishDate")?(String)mapInfo.get("bookPublishDate"):null);
		this.setBookLanguage(mapInfo.containsKey("bookLanguage")?(String)mapInfo.get("bookLanguage"):"");
		this.setBookPrice(mapInfo.containsKey("bookPrice")?(Double)mapInfo.get("bookPrice"):0.0);
		this.setBookClass(mapInfo.containsKey("bookClass")?(String)mapInfo.get("bookClass"):"");
		this.setBookContributor(mapInfo.containsKey("bookContributor")?(String)mapInfo.get("bookContributor"):"");
		this.setBookIntro(mapInfo.containsKey("bookIntro")?(String)mapInfo.get("bookIntro"):"");
		this.setBookPicUrl(mapInfo.containsKey("bookPicUrl")?(String)mapInfo.get("bookPicUrl"):"");
		
		this.setRecUserName(mapInfo.containsKey("recUserName")?(String)mapInfo.get("recUserName"):"");
		this.setRecUserEmail(mapInfo.containsKey("recUserEmail")?(String)mapInfo.get("recUserEmail"):"");
		this.setRecStatus(mapInfo.containsKey("recStatus")?(String)mapInfo.get("recStatus"):"");
		this.setRecDate(mapInfo.containsKey("recDate")?(String)mapInfo.get("recDate"):null);
		this.setRecRate(mapInfo.containsKey("recRate")?(Integer)mapInfo.get("recRate"):0);
		this.setCountPrice(mapInfo.containsKey("countPrice")?(Double)mapInfo.get("countPrice"):0.0);
		
	}
	
	public Map toMap() {
		
		Map returnInfo = new HashMap();
		
		returnInfo.put("bookName", bookName);
		returnInfo.put("bookAuthor", bookAuthor);
		returnInfo.put("bookISBN", bookISBN);
		returnInfo.put("bookPublisher", bookPublisher);
		returnInfo.put("bookPublishDate", bookPublishDate);
		returnInfo.put("bookLanguage", bookLanguage);
		returnInfo.put("bookPrice", bookPrice);
		returnInfo.put("bookClass", bookClass);
		returnInfo.put("bookContributor", bookContributor);
		returnInfo.put("bookPicUrl", bookPicUrl);
		
		returnInfo.put("recUserName", recUserName);
		returnInfo.put("recUserEmail", recUserEmail);
		returnInfo.put("recStatus", recStatus);
		returnInfo.put("recDate", recDate);
		returnInfo.put("recRate", recRate);
		returnInfo.put("countPrice", countPrice);
		
		return returnInfo;
	}
}
