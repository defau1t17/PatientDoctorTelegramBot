package org.telegrambots.doctortelegrambot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = DoctorTelegramBotApplicationTests.class)
@TestPropertySource(locations = "classpath:application.properties")
class DoctorTelegramBotApplicationTests {

    @Test
    void contextLoads() {
    }

}
