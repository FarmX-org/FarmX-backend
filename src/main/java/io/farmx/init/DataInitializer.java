package io.farmx.init;

import io.farmx.model.Handler;
import io.farmx.model.Role;
import io.farmx.model.UserEntity;
import io.farmx.repository.RoleRepository;
import io.farmx.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional 
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initRoles() {
        createRoleIfNotFound("Admin");
        createRoleIfNotFound("Farmer");
        createRoleIfNotFound("Consumer");
        createRoleIfNotFound("Handler");
        createOrderHandlerUserIfNotFound();
        createAdminUserIfNotFound(); 
        }

   private void createAdminUserIfNotFound() {
        String adminUsername = "admin";
        String adminEmail = "admin@farmx.com";

        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            UserEntity admin = new UserEntity();
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode("1234"));
            admin.setName("Super Admin");
            admin.setPhone("0000000000");
            admin.setCity("Admin City");
            admin.setStreet("Admin Street");
            admin.setEmail(adminEmail);

            Role adminRole = roleRepository.findByName("Admin")
                    .orElseThrow(() -> new RuntimeException("Role Admin not found"));

            admin.setRoles(Collections.singletonList(adminRole));
            userRepository.save(admin);

            logger.info("Admin user created with username: {}", adminUsername);
        } else {
            logger.info("Admin user already exists.");
        }
    }

   private void createOrderHandlerUserIfNotFound() {
	    String handlerUsername = "handler";
	    String handlerEmail = "handler@farmx.com";

	    if (userRepository.findByUsername(handlerUsername).isEmpty()) {
	    	Handler handler = new Handler();
	        handler.setUsername(handlerUsername);
	        handler.setPassword(passwordEncoder.encode("1234"));         handler.setName("Handler");
	        handler.setPhone("1111111111");
	        handler.setCity("Warehouse City");
	        handler.setStreet("Packing Dept");
	        handler.setEmail(handlerEmail);

	        Role handlerRole = roleRepository.findByName("Handler")
	                .orElseThrow(() -> new RuntimeException("Role Handler not found"));

	        handler.setRoles(Collections.singletonList(handlerRole));
	        userRepository.save(handler);

	        logger.info("OrderHandler user created with username: {}", handlerUsername);
	    } else {
	        logger.info("OrderHandler user already exists.");
	    }
	}


    private void createRoleIfNotFound(String roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
            logger.info("Inserted role: {}", roleName);
        } else {
            logger.info("Role already exists: {}", roleName);
        }
    }
    
}
