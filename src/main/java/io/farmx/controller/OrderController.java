package io.farmx.controller;

import io.farmx.dto.FarmOrderDTO;
import io.farmx.dto.OrderDTO;
import io.farmx.enums.OrderStatus;
import io.farmx.model.FarmOrder;
import io.farmx.model.Order;
import io.farmx.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * ✅ Create a custom/manual order by providing OrderDTO.
     * Used when a consumer wants to place an order for specific items (not from cart).
     */
    @PostMapping
    @PreAuthorize("hasRole('CONSUMER')")
    public OrderDTO createOrder(@RequestBody OrderDTO orderDTO, Principal principal) {
        return orderService.createOrder(orderDTO, principal);
    }

    /**
     * 🛒 Create an order from the authenticated user's current cart.
     * Automatically splits the cart into sub-orders per farm.
     */
    @PostMapping("/from-cart")
    @PreAuthorize("hasRole('CONSUMER')")
    public OrderDTO createOrderFromCart(Principal principal) {
        return orderService.createOrderFromCart(principal);
    }

    /**
     * 📦 Get all orders placed by the authenticated consumer.
     */
    @GetMapping("/consumer")
    @PreAuthorize("hasRole('CONSUMER')")
    public List<OrderDTO> getConsumerOrders(Principal principal) {
        return orderService.getOrdersForConsumer(principal);
    }

    /**
     * 🔍 Get details of a specific order by ID for the authenticated consumer.
     */
    @GetMapping("/consumer/{id}")
    @PreAuthorize("hasRole('CONSUMER')")
    public OrderDTO getOrderById(@PathVariable Long id, Principal principal) {
        return orderService.getOrderById(id, principal);
    }

    /**
     * 👑 Admin only - View all orders in the system.
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderDTO> getAllOrdersForAdmin() {
        return orderService.getAllOrdersForAdmin();
    }

    /**
     * 🚚 Handler only - View all orders assigned to them.
     */
    @GetMapping("/handler")
    @PreAuthorize("hasRole('HANDLER')")
    public List<OrderDTO> getOrdersForHandler(Principal principal) {
        return orderService.getOrdersForHandler(principal);
    }

    /**
     * 🔁 Handler - Update the status of an order (e.g., to PROCESSING or DELIVERED).
     * Valid transitions should be handled in the service.
     */
    @PutMapping("/handler/{orderId}/status")
    @PreAuthorize("hasRole('HANDLER')")
    public OrderDTO updateOrderStatus(@PathVariable Long orderId, @RequestParam OrderStatus status, Principal principal) {
        return orderService.updateOrderStatus(orderId, status, principal);
    }

    /**
     * 🌾 Farmer - Get all orders that include products from a specific farm.
     */
    @GetMapping("/farm/{farmId}")
    @PreAuthorize("hasRole('FARMER')")
    public List<FarmOrderDTO> getOrdersForFarm(@PathVariable Long farmId, Principal principal) {
        return orderService.getOrdersForFarm(principal, farmId);
    }

    /**
     * 🔄 Farmer - Update the status of their part in a shared order (FarmOrder).
     */
    @PutMapping("/farm-order/{farmOrderId}/status")
    @PreAuthorize("hasRole('FARMER')")
    public FarmOrderDTO updateFarmOrderStatus(
            @PathVariable Long farmOrderId,
            @RequestParam OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime deliveryTime
    ) {
        return orderService.updateFarmOrderStatus(farmOrderId, status, deliveryTime);
    }

    /**
     * 🛡️ Consumer - Get the delivery code of a READY order.
     * This code is used by the handler to confirm delivery.
     */
    @GetMapping("/consumer/{orderId}/delivery-code")
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<String> getDeliveryCode(
            @PathVariable Long orderId,
            Principal principal) {
        return ResponseEntity.ok(orderService.getDeliveryCodeForConsumer(orderId, principal.getName()));
    }

    /**
     * ✅ Handler - Confirm delivery using the delivery code provided by the consumer.
     * If the code is correct and not expired, status changes to DELIVERED.
     */
    @PutMapping("/handler/{orderId}/deliver")
    @PreAuthorize("hasRole('HANDLER')")
    public ResponseEntity<OrderDTO> confirmDelivery(
            @PathVariable Long orderId,
            @RequestParam String code,
            Principal principal) {
        return ResponseEntity.ok(orderService.confirmDeliveryByHandler(orderId, code, principal.getName()));
    }
}
