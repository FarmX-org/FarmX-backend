package io.farmx.service;

import io.farmx.dto.FarmOrderDTO;
import io.farmx.dto.OrderDTO;
import io.farmx.dto.OrderItemDTO;
import io.farmx.enums.NotificationType;
import io.farmx.enums.OrderStatus;
import io.farmx.model.*;
import io.farmx.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private HandlerService handlerService;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FarmOrderRepository farmOrderRepository;
    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
	private  FarmRepository farmRepo;

    @Autowired
	private CartRepository cartRepository;
    
    @Autowired
	private CartItemRepository cartItemRepository;
    
    @Autowired private  NotificationService  notificationService;
	
	
	
	//delivery
    public void generateDeliveryCode(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getOrderStatus() != OrderStatus.READY) {
            throw new IllegalStateException("Order must be READY to generate a delivery code");
        }

        String code = String.format("%06d", new SecureRandom().nextInt(1000000));
        order.setDeliveryCode(code);
        
        LocalDateTime expiry;
        if (order.getEstimatedDeliveryTime() != null) {
                    expiry = order.getEstimatedDeliveryTime().plusMinutes(30);
        } else {
               expiry = LocalDateTime.now().plusHours(1);
        }

        order.setDeliveryCodeExpiresAt(expiry);
        orderRepository.save(order);
    }

    public void regenerateDeliveryCodeForConsumer(Long orderId, String username) {
        Consumer consumer = (Consumer) userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getConsumer().getId().equals(consumer.getId())) {
            throw new SecurityException("Access denied: this order doesn't belong to you");
        }

        if (order.getOrderStatus() != OrderStatus.READY) {
            throw new IllegalStateException("Order must be READY to regenerate delivery code");
        }

        generateDeliveryCode(orderId);
    }

	
	public String getDeliveryCodeForConsumer(Long orderId, String username) {
	    Consumer consumer = (Consumer) userRepository.findByUsername(username)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    Order order = orderRepository.findById(orderId)
	            .orElseThrow(() -> new RuntimeException("Order not found"));

	    if (!order.getConsumer().getId().equals(consumer.getId())) {
	        throw new SecurityException("Access denied");
	    }

	    if (order.getOrderStatus() != OrderStatus.READY) {
	        throw new IllegalStateException("Order is not ready");
	    }

	    return order.getDeliveryCode();
	}
	public OrderDTO confirmDeliveryByHandler(Long orderId, String code, String username) {
	    Handler handler = (Handler) userRepository.findByUsername(username)
	            .orElseThrow(() -> new IllegalArgumentException("User not found"));

	    Order order = orderRepository.findById(orderId)
	            .orElseThrow(() -> new IllegalArgumentException("Order not found"));

	    if (!order.getHandler().getId().equals(handler.getId())) {
	        throw new SecurityException("Not your order");
	    }

	    if (order.getOrderStatus() != OrderStatus.READY) {
	        throw new IllegalStateException("Order not ready for delivery");
	    }

	    if (order.getDeliveryCodeExpiresAt().isBefore(LocalDateTime.now())) {
	        throw new IllegalStateException("Code expired");
	    }

	    if (!order.getDeliveryCode().equals(code)) {
	        throw new IllegalArgumentException("Incorrect code");
	    }

	    order.setOrderStatus(OrderStatus.DELIVERED);
	    order.setDeliveredAt(LocalDateTime.now());
	    order.setDeliveryCode(null);
	    order.setDeliveryCodeExpiresAt(null);

	    return convertToDTO(orderRepository.save(order));
	}



	///Get orders
    
	public List<OrderDTO> getAllOrdersForAdmin() {
	    List<Order> orders = orderRepository.findAll();
	    List<OrderDTO> dtoList = new ArrayList<>();
	    for (Order order : orders) {
	        dtoList.add(convertToDTO(order));
	    }
	    return dtoList;
	}

	public List<OrderDTO> getOrdersForHandler(Principal principal) {
	    String username = principal.getName();
	    Handler handler = (Handler) userRepository.findByUsername(username)
	        .orElseThrow(() -> new AccessDeniedException("Handler not found"));

	    List<Order> orders = orderRepository.findByHandlerId(handler.getId());
	    List<OrderDTO> dtoList = new ArrayList<>();
	    for (Order order : orders) {
	        dtoList.add(convertToDTO(order));
	    }
	    return dtoList;
	}


    public List<FarmOrderDTO> getOrdersForFarm(Principal principal, Long farmId) {
        String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("User not found"));

        if (!(user instanceof Farmer)) {
            throw new AccessDeniedException("Only farms allowed");
        }

        Farm farm = farmRepo.findById(farmId)
                .orElseThrow(() -> new NullPointerException("Farm not found"));

        boolean isOwner = (user instanceof Farmer farmer) && farm.getFarmer().getId().equals(farmer.getId());
        if (!isOwner) {
            throw new AccessDeniedException("Unauthorized to show these orders");
        }

        List<FarmOrder> orders = farmOrderRepository.findByFarmId(farmId);

        return orders.stream().map(order -> {
            FarmOrderDTO dto = new FarmOrderDTO();
            dto.setId(order.getId());
            dto.setFarmId(order.getFarm().getId());
            dto.setFarmName(order.getFarm().getName());
            dto.setOrderStatus(order.getOrderStatus());
            dto.setDeliveryTime(order.getDeliveryTime());

            List<OrderItemDTO> itemDTOs = order.getItems().stream().map(item -> {
                OrderItemDTO itemDTO = new OrderItemDTO();
                itemDTO.setId(item.getId());
                itemDTO.setProductId(item.getProduct().getId());
                itemDTO.setProductName(item.getProduct().getPlantedCrop().getCrop().getName());
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setPrice(item.getPrice());
                return itemDTO;
            }).toList();

            dto.setItems(itemDTOs);
            return dto;
        }).toList();
    }


    public List<OrderDTO> getOrdersForConsumer(Principal principal) {
        Consumer consumer = getConsumer(principal);
        List<Order> orders = orderRepository.findByConsumer(consumer);

        List<OrderDTO> dtoList = new ArrayList<>();
        for (Order order : orders) {
            dtoList.add(convertToDTO(order));
        }
        return dtoList;
    }

    public OrderDTO getOrderById(Long id, Principal principal) {
        Consumer consumer = getConsumer(principal);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getConsumer().getId().equals(consumer.getId())) {
            throw new AccessDeniedException("Access denied");
        }
        return convertToDTO(order);
    }


 

   //Check
    private void checkConsumerRole(Principal principal) {
        String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() -> new AccessDeniedException("User not found"));

        if (!(user instanceof Consumer)) {
            throw new AccessDeniedException("Only consumers allowed");
        }
    }

    private Consumer getConsumer(Principal principal) {
        checkConsumerRole(principal);
        return (Consumer) userRepository.findByUsername(principal.getName())
            .orElseThrow(() -> new AccessDeniedException("User not found"));
    }

    
   //الميمعة اللي لفت مخي لف ااخخ لا تعليق
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus, Principal principal) {
        String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!(user instanceof Handler handler)) {
            throw new AccessDeniedException("Only handlers can update order status");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getHandler().getId().equals(handler.getId())) {
            throw new SecurityException("This order does not belong to you");
        }

        // ✅ Prevent fake READY status if farms not ready
        if (newStatus == OrderStatus.READY) {
            boolean allFarmOrdersReady = order.getFarmOrders().stream()
                    .allMatch(fo -> fo.getOrderStatus() == OrderStatus.READY);

            if (!allFarmOrdersReady) {
                throw new IllegalStateException("Cannot mark order as READY until all farm orders are ready.");
            }

            // ✅ Calculate delivery time
            LocalDateTime estimatedDelivery = handlerService.calculateEstimatedDeliveryTime(order);
            order.setEstimatedDeliveryTime(estimatedDelivery);

            // 🔔 Send notifications here just in case updateOrderStatusIfAllFarmOrdersReady() was skipped
            String orderRef = "#" + order.getId();

            notificationService.sendNotification(handler,
                    "Order " + orderRef + " Ready for Delivery",
                    "All farms have prepared their items. Time to deliver! 🚚",
                    NotificationType.ORDER_READY
            );

            notificationService.sendNotification(order.getConsumer(),
                    "Your Order " + orderRef + " is Ready",
                    "Your order is ready and will be delivered soon! 🎉",
                    NotificationType.ORDER_READY
            );
        }

        order.setOrderStatus(newStatus);

        Order updated = orderRepository.save(order);
        return convertToDTO(updated);
    }


     public FarmOrderDTO updateFarmOrderStatus(Long farmOrderId, OrderStatus newStatus, LocalDateTime deliveryTime) {
    	    FarmOrder farmOrder = farmOrderRepository.findById(farmOrderId)
    	            .orElseThrow(() -> new RuntimeException("FarmOrder not found"));

    	    farmOrder.setOrderStatus(newStatus);
    	    if (deliveryTime != null) {
    	        farmOrder.setDeliveryTime(deliveryTime);
    	    }

    	    farmOrderRepository.save(farmOrder);

    	    updateOrderStatusIfAllFarmOrdersReady(farmOrder.getOrder().getId());
    	    FarmOrderDTO dto = new FarmOrderDTO();
    	    dto.setId(farmOrder.getId());
    	    dto.setFarmId(farmOrder.getFarm().getId());
    	    dto.setFarmName(farmOrder.getFarm().getName());
    	    dto.setOrderStatus(farmOrder.getOrderStatus());
    	    dto.setDeliveryTime(farmOrder.getDeliveryTime());

    	    List<OrderItemDTO> itemDTOs = farmOrder.getItems().stream().map(item -> {
    	        OrderItemDTO itemDTO = new OrderItemDTO();
    	        itemDTO.setId(item.getId());
    	        itemDTO.setProductId(item.getProduct().getId());
    	        itemDTO.setProductName(item.getProduct().getPlantedCrop().getCrop().getName());
    	        itemDTO.setQuantity(item.getQuantity());
    	        itemDTO.setPrice(item.getPrice());
    	        return itemDTO;
    	    }).toList();

    	    dto.setItems(itemDTOs);

    	    return dto;
    	}


     public void updateOrderStatusIfAllFarmOrdersReady(Long orderId) {
    	    Order order = orderRepository.findById(orderId)
    	            .orElseThrow(() -> new RuntimeException("Order not found"));

    	    boolean allReady = order.getFarmOrders().stream()
    	        .allMatch(fo -> fo.getOrderStatus() == OrderStatus.READY);

    	    if (allReady) {
    	        order.setOrderStatus(OrderStatus.READY);
    	        LocalDateTime estimatedDelivery = handlerService.calculateEstimatedDeliveryTime(order);
    	        order.setEstimatedDeliveryTime(estimatedDelivery);
    	        orderRepository.save(order);

    	        // 🧠 Notifications
    	        String orderRef = "#" + order.getId();

    	        // ✅ 1. Notify Handler
    	        UserEntity handler = order.getHandler();
    	        notificationService.sendNotification(
    	            handler,
    	            "Order " + orderRef + " Ready for Delivery",
    	            "All farms have prepared their items. Time to deliver! 🚚",
    	            NotificationType.ORDER_READY
    	        );

    	        // ✅ 2. Notify Consumer
    	        UserEntity consumer = order.getConsumer();
    	        notificationService.sendNotification(
    	            consumer,
    	            "Your Order " + orderRef + " is Ready",
    	            "Your order is ready and will be delivered soon! 🎉",
    	            NotificationType.ORDER_READY
    	        );
    	    }
    	}

    
    //new order
      public OrderDTO createOrderFromCart(Principal principal) {
    	    Consumer consumer = getConsumer(principal);

    	    Cart cart = cartRepository.findByConsumer(consumer)
    	        .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

    	    if (cart.getItems().isEmpty()) {
    	        throw new IllegalArgumentException("Cart is empty");
    	    }

    	    Order order = new Order();
    	    order.setConsumer(consumer);
    	    order.setOrderStatus(OrderStatus.PENDING);
    	    order.setCreatedAt(LocalDateTime.now());

    	    Handler handler = handlerService.findLeastBusyHandler();
    	    order.setHandler(handler);
    	 
    	

    	    List<FarmOrder> farmOrders = new ArrayList<>();
    	    double totalAmount = 0;

    	    Map<Long, List<CartItem>> itemsByFarm = new HashMap<>();
    	    for (CartItem cartItem : cart.getItems()) {
    	        Long farmId = cartItem.getProduct().getPlantedCrop().getFarm().getId();
    	        itemsByFarm.computeIfAbsent(farmId, k -> new ArrayList<>()).add(cartItem);
    	    }

    	    for (Map.Entry<Long, List<CartItem>> entry : itemsByFarm.entrySet()) {
    	        Long farmId = entry.getKey();
    	        List<CartItem> items = entry.getValue();

    	        Farm farm = farmRepository.findById(farmId)
    	                .orElseThrow(() -> new IllegalArgumentException("Farm not found"));

    	        FarmOrder farmOrder = new FarmOrder();
    	        farmOrder.setOrder(order);
    	        farmOrder.setFarm(farm);
    	        farmOrder.setOrderStatus(OrderStatus.PENDING);
    	        farmOrder.setDeliveryTime(LocalDateTime.now().plusDays(3));

    	        List<OrderItem> orderItems = new ArrayList<>();
    	        double farmOrderTotal = 0;

    	        for (CartItem cartItem : items) {
    	            Product product = cartItem.getProduct();

    	      
    	            if (!product.isAvailable()) {
    	                throw new IllegalStateException("Product '" + product.getPlantedCrop().getCrop().getName() + "' is not available.");
    	            }

    	            if (cartItem.getQuantity() > product.getQuantity()) {
    	                throw new IllegalStateException("Insufficient quantity for product '" + product.getPlantedCrop().getCrop().getName() + "'. Available: " + product.getQuantity() + ", Requested: " + cartItem.getQuantity());
    	            }

                     product.setQuantity(product.getQuantity() - cartItem.getQuantity());

    	             if (product.getQuantity() == 0) {
    	                product.setAvailable(false);
    	            }

    	            OrderItem orderItem = new OrderItem();
    	            orderItem.setFarmOrder(farmOrder);
    	            orderItem.setProduct(product);
    	            orderItem.setQuantity(cartItem.getQuantity());
    	            orderItem.setPrice(cartItem.getItemTotal());

    	            orderItems.add(orderItem);
    	            farmOrderTotal += cartItem.getItemTotal();
    	        }

    	        farmOrder.setItems(orderItems);
    	        totalAmount += farmOrderTotal;
    	        farmOrders.add(farmOrder);
    	    }

    	    order.setFarmOrders(farmOrders);
    	    order.setTotalAmount(totalAmount);

    	    for (CartItem cartItem : cart.getItems()) {
    	        productRepository.save(cartItem.getProduct());
    	    }

    	    Order savedOrder = orderRepository.save(order);

    	    //notification part
    	    UserEntity handlerUser = savedOrder.getHandler();
    	    notificationService.sendNotification(
    	        handlerUser,
    	        "New Order Assigned",
    	        "You’ve been assigned to a new order #" + savedOrder.getId(),
    	        NotificationType.ORDER_ASSIGNED
    	    );

    	    for (FarmOrder farmOrder : savedOrder.getFarmOrders()) {
    	        UserEntity farmer = farmOrder.getFarm().getFarmer(); 
    	        notificationService.sendNotification(
    	            farmer,
    	            "New Order Received",
    	            "You received a new order for your farm: " + farmOrder.getFarm().getName(),
    	            NotificationType.INFO
    	        );
    	    }
    	    cartItemRepository.deleteAll(cart.getItems());
    	    cart.getItems().clear();
    	    cart.setTotalPrice(0);
    	    cartRepository.save(cart);

    	    return convertToDTO(savedOrder);
    	}


    public OrderDTO createOrder(OrderDTO orderDTO, Principal principal) {
        Consumer consumer = getConsumer(principal);

        Order order = new Order();
        order.setConsumer(consumer);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        
        Handler handler = handlerService.findLeastBusyHandler();
        order.setHandler(handler);

        List<FarmOrder> farmOrders = new ArrayList<>();
        double totalAmount = 0;

        for (FarmOrderDTO farmOrderDTO : orderDTO.getFarmOrders()) {
            FarmOrder newFarmOrder = new FarmOrder();
            newFarmOrder.setOrder(order);

            Farm farm = farmRepository.findById(farmOrderDTO.getFarmId())
                .orElseThrow(() -> new IllegalArgumentException("Farm not found"));
            newFarmOrder.setFarm(farm);

            newFarmOrder.setOrderStatus(OrderStatus.PENDING);
            newFarmOrder.setDeliveryTime(farmOrderDTO.getDeliveryTime());

            List<OrderItem> items = new ArrayList<>();
            double farmOrderTotal = 0;

            for (OrderItemDTO itemDTO : farmOrderDTO.getItems()) {
                Product product = productRepository.findById(itemDTO.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Product not found"));

                OrderItem orderItem = new OrderItem();
                orderItem.setFarmOrder(newFarmOrder);
                orderItem.setProduct(product);
                
                orderItem.setQuantity(itemDTO.getQuantity());
                orderItem.setPrice(product.getPrice() * itemDTO.getQuantity());
                
                items.add(orderItem);
                farmOrderTotal += orderItem.getPrice();
            }

            newFarmOrder.setItems(items);
            totalAmount += farmOrderTotal;

            farmOrders.add(newFarmOrder);
        }


        order.setFarmOrders(farmOrders);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        //notification part
	    UserEntity handlerUser = savedOrder.getHandler();
	    notificationService.sendNotification(
	        handlerUser,
	        "New Order Assigned",
	        "You’ve been assigned to a new order #" + savedOrder.getId(),
	        NotificationType.ORDER_ASSIGNED
	    );

	    for (FarmOrder farmOrder : savedOrder.getFarmOrders()) {
	        UserEntity farmer = farmOrder.getFarm().getFarmer(); 
	        notificationService.sendNotification(
	            farmer,
	            "New Order Received",
	            "You received a new order for your farm: " + farmOrder.getFarm().getName(),
	            NotificationType.INFO
	        );
	    }

        return convertToDTO(savedOrder);
    }


      private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setCreatedAt(order.getCreatedAt());

        List<FarmOrder> farmOrders = order.getFarmOrders();
        List<io.farmx.dto.FarmOrderDTO> farmOrderDTOs = new ArrayList<>();

        for (FarmOrder farmOrder : farmOrders) {
            io.farmx.dto.FarmOrderDTO foDto = new io.farmx.dto.FarmOrderDTO();
            foDto.setId(farmOrder.getId());
            foDto.setFarmId(farmOrder.getFarm().getId());
            foDto.setFarmName(farmOrder.getFarm().getName()); 
            foDto.setOrderStatus(farmOrder.getOrderStatus());
            foDto.setDeliveryTime(farmOrder.getDeliveryTime());

            List<OrderItem> items = farmOrder.getItems();
            List<OrderItemDTO> itemDTOs = new ArrayList<>();

            for (OrderItem item : items) {
                OrderItemDTO itemDTO = new OrderItemDTO();
                itemDTO.setId(item.getId());
                itemDTO.setProductId(item.getProduct().getId());
                itemDTO.setProductName(item.getProduct().getPlantedCrop().getCrop().getName()); 
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setPrice(item.getPrice());
                itemDTOs.add(itemDTO);
            }
            foDto.setItems(itemDTOs);
            farmOrderDTOs.add(foDto);
        }

        dto.setFarmOrders(farmOrderDTOs);
        dto.setEstimatedDeliveryTime(order.getEstimatedDeliveryTime());
        return dto;
    }





}
