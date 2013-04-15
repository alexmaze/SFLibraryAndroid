package cn.successfactors.library.bean;

public class RecommandBook {

	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public String getRecommander() {
		return recommander;
	}
	public void setRecommander(String recommander) {
		this.recommander = recommander;
	}
	public int getHotRate() {
		return hotRate;
	}
	public void setHotRate(int hotRate) {
		this.hotRate = hotRate;
	}
	
	private String imageUrl;
	private String name;
	private String author;
	private Double price;
	private String recommander;
	private int hotRate;
	
	
	
}
