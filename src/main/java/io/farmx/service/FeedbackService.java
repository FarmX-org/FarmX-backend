package io.farmx.service;

import io.farmx.dto.FeedbackDTO;
import io.farmx.enums.FeedbackType;
import io.farmx.enums.OrderStatus;
import io.farmx.model.*;
import io.farmx.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private OrderRepository orderRepository;
 


    public void submitFeedback(FeedbackDTO dto, Principal principal) {

        String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!(user instanceof Consumer)) {
            throw new IllegalArgumentException("Only consumers can give feedback");
        }

       Order order = orderRepository.findById(dto.getOrderId())
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("Feedback is allowed only after order is delivered");
        }

      if (!order.getConsumer().getId().equals(user.getId())) {
            throw new IllegalArgumentException("User did not place this order");
        }

         Feedback feedback = new Feedback();
        feedback.setConsumer((Consumer) user);
        feedback.setRating(dto.getRating());
        feedback.setComment(dto.getComment());
        feedback.setFeedbackType(dto.getFeedbackType());
        feedback.setOrder(order);

        switch(dto.getFeedbackType()) {
            case PRODUCT:
                Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

                boolean productInOrder = order.getFarmOrders().stream()
                    .flatMap(farmOrder -> farmOrder.getItems().stream())
                    .anyMatch(item -> item.getProduct().getId().equals(product.getId()));

                if (!productInOrder) {
                    throw new IllegalArgumentException("Product not part of this order");
                }
              boolean existsProductFeedback = feedbackRepository
                    .existsByConsumerAndProductAndOrder((Consumer) user, product, order);

                if (existsProductFeedback) {
                    throw new IllegalArgumentException("Feedback already given for this product in this order");
                }

                feedback.setProduct(product);
                break;

            case FARM:
                Farm farm = farmRepository.findById(dto.getFarmId())
                    .orElseThrow(() -> new IllegalArgumentException("Farm not found"));
               
                boolean farmInOrder = order.getFarmOrders().stream()
                    .anyMatch(farmOrder -> farmOrder.getFarm().getId().equals(farm.getId()));

                if (!farmInOrder) {
                    throw new IllegalArgumentException("Farm not part of this order");
                }

                boolean existsFarmFeedback = feedbackRepository
                    .existsByConsumerAndFarmAndOrder((Consumer) user, farm, order);

                if (existsFarmFeedback) {
                    throw new IllegalArgumentException("Feedback already given for this farm in this order");
                }

                feedback.setFarm(farm);
                break;
        }

        feedbackRepository.save(feedback);

         if (feedback.getFeedbackType() == FeedbackType.FARM) {
            updateFarmRating(feedback.getFarm());
        }
    }

    private void updateFarmRating(Farm farm) {
        List<Feedback> feedbacks = feedbackRepository.findByFarmId(farm.getId());
        double avgRating = feedbacks.stream()
            .mapToInt(Feedback::getRating)
            .average()
            .orElse(0.0);
        farm.setRating(avgRating);
        farm.setRatingCount(feedbacks.size());
        farmRepository.save(farm);
    }
    
    
    public List<FeedbackDTO> getFeedbacksForFarmer(Principal principal) {
        String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!(user instanceof Farmer)) {
            throw new IllegalArgumentException("Only farmers can access their feedbacks");
        }
        Farmer farmer = (Farmer) user;

        List<Farm> farms = farmRepository. findAllByFarmer(farmer);

        List<Feedback> farmFeedbacks = feedbackRepository.findByFarmIn(farms);

       List<Product> products = productRepository.findByPlantedCrop_FarmIn(farms);

       List<Feedback> productFeedbacks = feedbackRepository.findByProductIn(products);

        List<FeedbackDTO> allFeedbacks = 
            Stream.concat(farmFeedbacks.stream(), productFeedbacks.stream())
                  .map(this::convertToDTO)
                  .collect(Collectors.toList());

        return allFeedbacks;
    }
  
    
    public List<FeedbackDTO> getAllFeedbacks() {
        List<Feedback> feedbacks = feedbackRepository.findAll();

        return feedbacks.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    private FeedbackDTO convertToDTO(Feedback feedback) {
        FeedbackDTO dto = new FeedbackDTO();
        dto.setOrderId(feedback.getOrder().getId());
        dto.setFeedbackType(feedback.getFeedbackType());
        dto.setRating(feedback.getRating());
        dto.setComment(feedback.getComment());
        if (feedback.getProduct() != null) {
            dto.setProductId(feedback.getProduct().getId());
            dto.setProductName(feedback.getProduct().getPlantedCrop().getCrop().getName());
            dto.setFarmId(feedback.getProduct().getPlantedCrop().getFarm().getId());
            dto.setFarmName(feedback.getProduct().getPlantedCrop().getFarm().getName());   }
        if (feedback.getFarm() != null) {
            dto.setFarmId(feedback.getFarm().getId());
            dto.setFarmName(feedback.getFarm().getName()); 
            }
        return dto;
    }


}


