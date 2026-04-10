package com.example.ziwa;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        // https://localhost:8000/hello
        // ziwa.org/hello
        // ziwa.org/home
        for (int i = 0; i < 1000000000; i++) {

        }
        return "Hello World!";
    }

    @GetMapping("/home")
    public String getGreeting() {
        return "welcome to the home page ^_^";
    }
}
