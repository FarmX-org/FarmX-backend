package io.farmx.controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.farmx.dto.UserProfileDTO;
import io.farmx.model.UserEntity;
import io.farmx.repository.UserRepository;
import io.farmx.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getMyProfile(Principal principal) {
        String username = principal.getName();
        return userService.getUserByUsername(username)
            .map(user -> ResponseEntity.ok(userService.toDTO(user)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileDTO> updateMyProfile(Principal principal, @RequestBody UserProfileDTO dto) {
        String username = principal.getName();
        UserEntity user = userService.getUserByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        userService.updateEntityFromDTO(user, dto);
        UserEntity updated = userRepository.save(user);
        return ResponseEntity.ok(userService.toDTO(updated));
    }


    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserProfileDTO>> getAllUsers() {
        Iterable<UserEntity> usersIterable = userRepository.findAll();

        List<UserProfileDTO> dtos = StreamSupport.stream(usersIterable.spliterator(), false)
            .map(userService::toDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}
