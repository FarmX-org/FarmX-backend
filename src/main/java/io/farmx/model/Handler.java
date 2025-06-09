package io.farmx.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import jakarta.persistence.OneToMany;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "handlers")
public class Handler extends UserEntity {

    @OneToMany(mappedBy = "handler")
    private List<Order> orders = new ArrayList<>();

    // Getter and Setter
    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
    
   // int countByHandlerAndOrderStatus(Handler handler, OrderStatus status);

}
