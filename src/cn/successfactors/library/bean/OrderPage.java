package cn.successfactors.library.bean;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings({ "serial" })
public class OrderPage implements Serializable {
	
	private ArrayList<SLOrder> theOrders;
	private int itemsNumPerPage;
	private int pageNum;
	private int totalPageNum;
	
	public OrderPage() {
		
	}
	
	public OrderPage(int itemsNumPerPage, int pageNum) {
		this.itemsNumPerPage = itemsNumPerPage;
		this.pageNum = pageNum;
	}
	
	public ArrayList<SLOrder> getTheOrders() {
		return theOrders;
	}
	public void setTheOrders(ArrayList<SLOrder> theOrders) {
		this.theOrders = theOrders;
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
