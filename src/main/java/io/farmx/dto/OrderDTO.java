package io.farmx.dto;

import io.farmx.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {
    private Long id;
    private double totalAmount;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
    private List<FarmOrderDTO> farmOrders;
    
    private LocalDateTime estimatedDeliveryTime;
    private String deliveryCode;
    private LocalDateTime deliveryCodeExpiresAt;


    public LocalDateTime getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }
    public void setEstimatedDeliveryTime(LocalDateTime estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<FarmOrderDTO> getFarmOrders() {
        return farmOrders;
    }
    public void setFarmOrders(List<FarmOrderDTO> farmOrders) {
        this.farmOrders = farmOrders;
    }
	public String getDeliveryCode() {
		return deliveryCode;
	}
	public void setDeliveryCode(String deliveryCode) {
		this.deliveryCode = deliveryCode;
	}
	public LocalDateTime getDeliveryCodeExpiresAt() {
		return deliveryCodeExpiresAt;
	}
	public void setDeliveryCodeExpiresAt(LocalDateTime deliveryCodeExpiresAt) {
		this.deliveryCodeExpiresAt = deliveryCodeExpiresAt;
	}
}
