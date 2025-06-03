package io.farmx.service;

import io.farmx.dto.SignUpDto;
import io.farmx.dto.UserProfileDTO;
import io.farmx.model.Consumer;
import io.farmx.model.Farmer;
import io.farmx.model.Role;
import io.farmx.model.UserEntity;
import io.farmx.repository.RoleRepository;
import io.farmx.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
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
    
    public UserEntity createUser(SignUpDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already taken");
        }

        String roleName = dto.getRole();
        UserEntity user;

        switch (roleName.toLowerCase()) {
            case "farmer":
                user = new Farmer();
                break;
            case "consumer":
                user = new Consumer();
                break;
            case "admin":
                user = new UserEntity(); 
                break;
            default:
                throw new RuntimeException("Invalid role. Must be 'Farmer', 'Consumer', or 'Admin'.");
        }

        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setCity(dto.getCity());
        user.setStreet(dto.getStreet());
        user.setEmail(dto.getEmail());
        user.setProfilePhotoUrl(dto.getProfilePhoto());

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles(Collections.singletonList(role));

        return userRepository.save(user);
    }


public UserProfileDTO toDTO(UserEntity user) {
    return new UserProfileDTO(
        user.getId(),
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
