package io.farmx.controller;

import io.farmx.dto.OrderDTO;
import io.farmx.enums.OrderStatus;
import io.farmx.model.FarmOrder;
import io.farmx.model.Order;
import io.farmx.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * Create an order manually (not from cart).
     * This might be used for admin-created orders or custom use cases.
     */
    @PostMapping
    @PreAuthorize("hasRole('CONSUMER')")
    public OrderDTO createOrder(@RequestBody OrderDTO orderDTO, Principal principal) {
        return orderService.createOrder(orderDTO, principal);
    }

    /**
     * Create an order based on the authenticated consumer's current cart.
     * This is the default way for consumers to place orders.
     */
    @PostMapping("/from-cart")
    @PreAuthorize("hasRole('CONSUMER')")
    public OrderDTO createOrderFromCart(Principal principal) {
        return orderService.createOrderFromCart(principal);
    }

    // Consumer - Get their own orders
    @GetMapping("/consumer")
    @PreAuthorize("hasRole('CONSUMER')")
    public List<OrderDTO> getConsumerOrders(Principal principal) {
        return orderService.getOrdersForConsumer(principal);
    }

    // Consumer - Get order details by order ID
    @GetMapping("/consumer/{id}")
    @PreAuthorize("hasRole('CONSUMER')")
    public OrderDTO getOrderById(@PathVariable Long id, Principal principal) {
        return orderService.getOrderById(id, principal);
    }

    // Admin - View all orders in the system
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Order> getAllOrdersForAdmin() {
        return orderService.getAllOrdersForAdmin();
    }

    // Handler - View their assigned orders
    @GetMapping("/handler")
    @PreAuthorize("hasRole('HANDLER')")
    public List<Order> getOrdersForHandler(Principal principal) {
        return orderService.getOrdersForHandler(principal);
    }

    // Handler - Update order status (e.g., from PENDING to PROCESSING or DELIVERED)
    @PutMapping("/handler/{orderId}/status")
    @PreAuthorize("hasRole('HANDLER')")
    public OrderDTO updateOrderStatus(@PathVariable Long orderId, @RequestParam OrderStatus status, Principal principal) {
        return orderService.updateOrderStatus(orderId, status, principal);
    }

    // Farmer - View orders related to their farm
    @GetMapping("/farm/{farmId}")
    @PreAuthorize("hasRole('FARMER')")
    public List<FarmOrder> getOrdersForFarm(@PathVariable Long farmId, Principal principal) {
        return orderService.getOrdersForFarm(principal, farmId);
    }

    // Farmer - Update status of a specific FarmOrder (their part of a shared order)
    @PutMapping("/farm-order/{farmOrderId}/status")
    @PreAuthorize("hasRole('FARMER')")
    public FarmOrder updateFarmOrderStatus(@PathVariable Long farmOrderId, @RequestParam OrderStatus status) {
        return orderService.updateFarmOrderStatus(farmOrderId, status);
    }

}
