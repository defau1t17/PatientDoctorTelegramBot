package org.patientbot.patienttelegrambot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = PatientTelegramBotApplicationTests.class)
@TestPropertySource(locations = "classpath:application.properties")
class PatientTelegramBotApplicationTests {

	@Test
	void contextLoads() {
	}

}
