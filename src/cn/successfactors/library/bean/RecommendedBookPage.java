package cn.successfactors.library.bean;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings({ "serial" })
public class RecommendedBookPage implements Serializable {
	
	private ArrayList<SLRecommendedBook> theBooks;
	private int itemsNumPerPage;
	private int pageNum;
	private int totalPageNum;
	
	public RecommendedBookPage() {
		
	}
	
	public RecommendedBookPage(int itemsNumPerPage, int pageNum) {
		this.itemsNumPerPage = itemsNumPerPage;
		this.pageNum = pageNum;
	}
	
	public ArrayList<SLRecommendedBook> getTheBooks() {
		return theBooks;
	}
	public void setTheBooks(ArrayList<SLRecommendedBook> theBooks) {
		this.theBooks = theBooks;
	}
	public int getItemsNumPerPage() {
		return itemsNumPerPage;
	}

	public void setItemsNumPerPage(int itemsNumPerPage) {
		this.itemsNumPerPage = itemsNumPerPage;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getTotalPageNum() {
		return totalPageNum;
	}

	public void setTotalPageNum(int totalPageNum) {
		this.totalPageNum = totalPageNum;
	}
	
}
