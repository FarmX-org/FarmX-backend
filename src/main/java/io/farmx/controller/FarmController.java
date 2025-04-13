package io.farmx.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FarmController {
    @GetMapping("/hello")
    public Map<String, String> sayHello(@RequestParam String name) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello " + name);
        return response;
    }
    @PostMapping("/hello-body")
    public Map<String, String> sayHelloPost(@RequestBody Map<String, String> requestBody) {
        String name = requestBody.get("name");
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello " + name);
        return response;
    }

}


