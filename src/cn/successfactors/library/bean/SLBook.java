package cn.successfactors.library.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public class SLBook implements Serializable {
	
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
	private int bookTotalQuantity;
	private int bookInStoreQuantity;
	private int bookAvailableQuantity;
	private String bookPicUrl;
	private String bookAddDate;
	
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
	public int getBookTotalQuantity() {
		return bookTotalQuantity;
	}
	public void setBookTotalQuantity(int bookTotalQuantity) {
		this.bookTotalQuantity = bookTotalQuantity;
	}
	public int getBookInStoreQuantity() {
		return bookInStoreQuantity;
	}
	public void setBookInStoreQuantity(int bookInStoreQuantity) {
		this.bookInStoreQuantity = bookInStoreQuantity;
	}
	public int getBookAvailableQuantity() {
		return bookAvailableQuantity;
	}
	public void setBookAvailableQuantity(int bookAvailableQuantity) {
		this.bookAvailableQuantity = bookAvailableQuantity;
	}
	public String getBookPicUrl() {
		return bookPicUrl;
	}
	public void setBookPicUrl(String bookPicUrl) {
		this.bookPicUrl = bookPicUrl;
	}
	public String getBookAddDate() {
		return bookAddDate;
	}
	public void setBookAddDate(String bookAddDate) {
		this.bookAddDate = bookAddDate;
	}

	public static String getWords(String strContent) {
		int num= 600;
		if(strContent == null) {
			return "......";
		}
		
		if(strContent.length() > num)
			return strContent.subSequence(0, num) + "......";
		else
			return strContent + "......";
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
		this.setBookTotalQuantity(mapInfo.containsKey("bookTotalQuantity")?(Integer)mapInfo.get("bookTotalQuantity"):0);
		this.setBookInStoreQuantity(mapInfo.containsKey("bookInStoreQuantity")?(Integer)mapInfo.get("bookInStoreQuantity"):0);
		this.setBookAvailableQuantity(mapInfo.containsKey("bookAvailableQuantity")?(Integer)mapInfo.get("bookAvailableQuantity"):0);
		this.setBookPicUrl(mapInfo.containsKey("bookPicUrl")?(String)mapInfo.get("bookPicUrl"):"");
		this.setBookAddDate(mapInfo.containsKey("bookAddDate")?(String)mapInfo.get("bookAddDate"):null);
		
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
		returnInfo.put("bookIntro", getWords(bookIntro));
		returnInfo.put("bookTotalQuantity", bookTotalQuantity);
		returnInfo.put("bookInStoreQuantity", bookInStoreQuantity);
		returnInfo.put("bookAvailableQuantity", bookAvailableQuantity);
		returnInfo.put("bookPicUrl", bookPicUrl);
		returnInfo.put("bookAddDate", bookAddDate);
		
		return returnInfo;
	}
	
}
