package io.farmx.service;

import io.farmx.dto.UserProfileDTO;
import io.farmx.model.UserEntity;
import io.farmx.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserEntity> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserEntity updateUserProfile(String username, UserEntity updatedData) {
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        if(updatedData.getName() != null) user.setName(updatedData.getName());
        if(updatedData.getPhone() != null) user.setPhone(updatedData.getPhone());
        if(updatedData.getCity() != null) user.setCity(updatedData.getCity());
        if(updatedData.getStreet() != null) user.setStreet(updatedData.getStreet());
        if(updatedData.getEmail() != null) user.setEmail(updatedData.getEmail());
        if(updatedData.getProfilePhotoUrl() != null) user.setProfilePhotoUrl(updatedData.getProfilePhotoUrl());

        return userRepository.save(user);
    }
    

public UserProfileDTO toDTO(UserEntity user) {
    return new UserProfileDTO(
        user.getUsername(),
        user.getName(),
        user.getPhone(),
        user.getCity(),
        user.getStreet(),
        user.getEmail(),
        user.getProfilePhotoUrl(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getRoles().stream()
            .map(role -> role.getName())
            .collect(Collectors.toList())
    );
}

    public void updateEntityFromDTO(UserEntity user, UserProfileDTO dto) {
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getCity() != null) user.setCity(dto.getCity());
        if (dto.getStreet() != null) user.setStreet(dto.getStreet());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getProfilePhotoUrl() != null) user.setProfilePhotoUrl(dto.getProfilePhotoUrl());
    }

}
