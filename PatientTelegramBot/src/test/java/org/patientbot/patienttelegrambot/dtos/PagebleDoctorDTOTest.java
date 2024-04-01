package org.patientbot.patienttelegrambot.dtos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PagebleDoctorDTOTest {

    private PagebleDoctorDTO pagebleDoctorDTOUnderTest;

    @BeforeEach
    void setUp() {
        pagebleDoctorDTOUnderTest = new PagebleDoctorDTO();
    }

    @Test
    void testToString() {
        // Setup
        // Run the test
        final String result = pagebleDoctorDTOUnderTest.toString();

        // Verify the results
        assertEquals("", result);
    }
}
