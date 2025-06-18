package io.farmx.dto;

import io.farmx.enums.FeedbackType;

public class FeedbackDTO {
	    private Long orderId;
	    private FeedbackType feedbackType;
	    private int rating;
	    private String comment;
	    private Long productId;
	    private String productName; 
	    private Long farmId;
	    private String farmName;  

	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public Long getFarmId() {
		return farmId;
	}
	public void setFarmId(Long farmId) {
		this.farmId = farmId;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public FeedbackType getFeedbackType() {
		return feedbackType;
	}
	public void setFeedbackType(FeedbackType feedbackType) {
		this.feedbackType = feedbackType;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getFarmName() {
		return farmName;
	}
	public void setFarmName(String farmName) {
		this.farmName = farmName;
	}

	
    
}
