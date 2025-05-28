package io.farmx.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;

import io.farmx.config.JWTGenerator;
import io.farmx.dto.AuthResponseDTO;
import io.farmx.dto.LoginDto;
import io.farmx.dto.SignUpDto;
import io.farmx.model.Consumer;
import io.farmx.model.Farmer;
import io.farmx.model.Role;
import io.farmx.model.UserEntity;
import io.farmx.repository.RoleRepository;
import io.farmx.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AuthController {

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JWTGenerator jwtGenerator;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          RoleRepository roleRepository, PasswordEncoder passwordEncoder, JWTGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
    }


@PostMapping("login")
public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDto loginDto, HttpServletResponse response){
    logger.info("Attempting login for user: {}", loginDto.getUsername());

    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
            loginDto.getUsername(),
            loginDto.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String token = jwtGenerator.generateToken(authentication);
    ResponseCookie cookie = ResponseCookie.from("accessToken", token)
            .httpOnly(true)
            .secure(false) 
            .path("/")
            .sameSite("Strict")
            .maxAge(60 * 60) 
            .build();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    List<String> roles = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

    logger.info("Login successful for user: {}", loginDto.getUsername());
    return new ResponseEntity<>(new AuthResponseDTO(token, roles), HttpStatus.OK);
}

@PostMapping("signup")
public ResponseEntity<String> signUp(@RequestBody SignUpDto signUpDto) {
  if (userRepository.existsByUsername(signUpDto.getUsername())) {
        logger.warn("Username is already taken: {}", signUpDto.getUsername());
        return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
    }

    if (userRepository.existsByEmail(signUpDto.getEmail())) {
        logger.warn("Email is already taken: {}", signUpDto.getEmail());
        return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
    }
   String roleName = signUpDto.getRole();
    if (roleName == null || 
        (!roleName.equalsIgnoreCase("Farmer") && !roleName.equalsIgnoreCase("Consumer"))) {
        logger.warn("Invalid role provided: {}", roleName);
        return new ResponseEntity<>("Invalid role. Must be either 'Farmer' or 'Consumer'.", HttpStatus.BAD_REQUEST);
    }
  UserEntity user;

    if (roleName.equalsIgnoreCase("Farmer")) {
        user = new Farmer();
    } else {
        Consumer consumer = new Consumer();  
        user = consumer;
    }
    user.setUsername(signUpDto.getUsername());
    user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
    user.setName(signUpDto.getName());
    user.setPhone(signUpDto.getPhone());
    user.setCity(signUpDto.getCity());
    user.setStreet(signUpDto.getStreet());
    user.setEmail(signUpDto.getEmail());
    user.setProfilePhotoUrl(signUpDto.getProfilePhoto());


        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new RuntimeException("Role '" + roleName + "' not found"));
    user.setRoles(Collections.singletonList(role));

    userRepository.save(user);

    logger.info("User successfully registered: {} with role: {}", signUpDto.getUsername(), roleName);
    return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
}

}





