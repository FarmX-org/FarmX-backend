package io.farmx.service;

import org.springframework.stereotype.Service;

import io.farmx.enums.OrderStatus;
import io.farmx.model.Handler;
import io.farmx.model.Order;
import io.farmx.repository.HandlerRepository;
import io.farmx.repository.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HandlerService {

    @Autowired
    private HandlerRepository handlerRepository;

    @Autowired
    private OrderRepository orderRepository;

    public LocalDateTime calculateEstimatedDeliveryTime(Order order) {
        LocalDateTime now = LocalDateTime.now();
        int deliveryBufferInHours = 2;

        boolean allReady = order.getFarmOrders().stream()
            .allMatch(fo -> fo.getOrderStatus() == OrderStatus.READY);

        if (allReady) {
            return now.plusHours(deliveryBufferInHours);
        } else {
         
        	return null;
        }
    }
    public Handler findLeastBusyHandler() {
        List<Handler> handlers = handlerRepository.findAll();

        Handler leastBusy = null;
        int minPendingOrders = Integer.MAX_VALUE;

        for (Handler handler : handlers) {
            int pendingCount = orderRepository.countByHandlerAndOrderStatus(handler, OrderStatus.PENDING);
            if (pendingCount < minPendingOrders) {
                minPendingOrders = pendingCount;
                leastBusy = handler;
            }
        }

        if (leastBusy == null) {
            throw new RuntimeException("No handlers available");
        }

        return leastBusy;
    }
}
