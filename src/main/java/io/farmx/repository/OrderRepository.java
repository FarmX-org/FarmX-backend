package io.farmx.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import io.farmx.enums.OrderStatus;
import io.farmx.model.Consumer;
import io.farmx.model.Handler;
import io.farmx.model.Order;



public interface OrderRepository extends JpaRepository<Order, Long> {

    int countByHandlerAndOrderStatus(Handler handler, OrderStatus status);

	List<Order> findByConsumer(Consumer consumer);
	List<Order> findByHandlerId(Long handlerId);


}
