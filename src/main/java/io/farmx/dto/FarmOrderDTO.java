package io.farmx.dto;

import io.farmx.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public class FarmOrderDTO {
    private Long id;
    private Long farmId;
    private OrderStatus orderStatus;
    private LocalDateTime deliveryTime;
    private List<OrderItemDTO> items;

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getFarmId() {
        return farmId;
    }
    public void setFarmId(Long farmId) {
        this.farmId = farmId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }
    public void setDeliveryTime(LocalDateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }
    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }
}
