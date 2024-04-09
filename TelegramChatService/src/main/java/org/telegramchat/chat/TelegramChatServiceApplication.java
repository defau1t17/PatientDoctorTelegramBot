package org.telegramchat.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TelegramChatServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelegramChatServiceApplication.class, args);
    }

}
