package com.chatmatchingservice.springchatmatching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages = "com.chatmatchingservice.springchatmatching")
public class SpringChatMatchingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringChatMatchingApplication.class, args);
        System.out.println("Hello World!");
    }

}
