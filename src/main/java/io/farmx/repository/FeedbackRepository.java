package io.farmx.repository;

import io.farmx.model.Consumer;
import io.farmx.model.Farm;
import io.farmx.model.Feedback;
import io.farmx.model.Order;
import io.farmx.model.Product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByProductId(Long productId);
    List<Feedback> findByFarmId(Long farmId);
    boolean existsByProductIdAndConsumerId(Long productId, Long consumerId);
    boolean existsByConsumerAndProductAndOrder(Consumer consumer, Product product, Order order);
    List<Feedback> findByFarmIn(List<Farm> farms);
    List<Feedback> findByProductIn(List<Product> products);

    boolean existsByConsumerAndFarmAndOrder(Consumer consumer, Farm farm, Order order);

    List<Feedback> findByFarm(Farm farm);
    List<Feedback> findByProduct(Product product);
    List<Feedback> findByProduct_Id(Long productId);
}
