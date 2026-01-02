package com.chatmatchingservice.springchatmatching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SpringChatMatchingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringChatMatchingApplication.class, args);
    }

}
