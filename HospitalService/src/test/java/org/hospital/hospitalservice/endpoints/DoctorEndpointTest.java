package org.hospital.hospitalservice.endpoints;

import org.hospital.hospitalservice.entities.Doctor;
import org.hospital.hospitalservice.entities.DoctorPosition;
import org.hospital.hospitalservice.entities.DoctorShift;
import org.hospital.hospitalservice.entities.ShiftStatus;
import org.hospital.hospitalservice.services.DoctorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@WebMvcTest(DoctorEndpoint.class)
class DoctorEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoctorService mockDoctorService;

    @Test
    void testGetAllDoctors() throws Exception {
        // Setup
        // Configure DoctorService.findAll(...).
        final Doctor doctor = new Doctor();
        doctor.setId(0);
        doctor.setDoctorPosition(DoctorPosition.CHIEF_PHYSICIAN);
        doctor.setWorkroom(0);
        doctor.setDoctorShift(DoctorShift.DAILY_SHIFT);
        doctor.setShiftStatus(ShiftStatus.OPENED);
        final Page<Doctor> doctors = new PageImpl<>(List.of(doctor));
        when(mockDoctorService.findAll(0, 0)).thenReturn(doctors);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/doctors")
                        .param("pageNumber", "0")
                        .param("pageSize", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }

    @Test
    void testGetAllDoctors_DoctorServiceReturnsNoItems() throws Exception {
        // Setup
        when(mockDoctorService.findAll(0, 0)).thenReturn(new PageImpl<>(Collections.emptyList()));

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/doctors")
                        .param("pageNumber", "0")
                        .param("pageSize", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }

    @Test
    void testGetDoctorByID() throws Exception {
        // Setup
        // Configure DoctorService.findByID(...).
        final Doctor doctor = new Doctor();
        doctor.setId(0);
        doctor.setDoctorPosition(DoctorPosition.CHIEF_PHYSICIAN);
        doctor.setWorkroom(0);
        doctor.setDoctorShift(DoctorShift.DAILY_SHIFT);
        doctor.setShiftStatus(ShiftStatus.OPENED);
        final Optional<Doctor> optionalDoctor = Optional.of(doctor);
        when(mockDoctorService.findByID(0L)).thenReturn(optionalDoctor);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/doctors/{id}")
                        .param("id", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }

    @Test
    void testGetDoctorByID_DoctorServiceReturnsAbsent() throws Exception {
        // Setup
        when(mockDoctorService.findByID(0L)).thenReturn(Optional.empty());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/doctors/{id}")
                        .param("id", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }

    @Test
    void testFindDoctorByChatID() throws Exception {
        // Setup
        // Configure DoctorService.findByChatID(...).
        final Doctor doctor = new Doctor();
        doctor.setId(0);
        doctor.setDoctorPosition(DoctorPosition.CHIEF_PHYSICIAN);
        doctor.setWorkroom(0);
        doctor.setDoctorShift(DoctorShift.DAILY_SHIFT);
        doctor.setShiftStatus(ShiftStatus.OPENED);
        final Optional<Doctor> optionalDoctor = Optional.of(doctor);
        when(mockDoctorService.findByChatID(0L)).thenReturn(optionalDoctor);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/doctors/chat/{chatID}")
                        .param("chatID", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }

    @Test
    void testFindDoctorByChatID_DoctorServiceReturnsAbsent() throws Exception {
        // Setup
        when(mockDoctorService.findByChatID(0L)).thenReturn(Optional.empty());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/doctors/chat/{chatID}")
                        .param("chatID", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }

    @Test
    void testCreateNewDoctor() throws Exception {
        // Setup
        // Configure DoctorService.validateBeforeSave(...).
        final Doctor entity = new Doctor();
        entity.setId(0);
        entity.setDoctorPosition(DoctorPosition.CHIEF_PHYSICIAN);
        entity.setWorkroom(0);
        entity.setDoctorShift(DoctorShift.DAILY_SHIFT);
        entity.setShiftStatus(ShiftStatus.OPENED);
        when(mockDoctorService.validateBeforeSave(entity)).thenReturn(false);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/doctors")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }

    @Test
    void testCreateNewDoctor_DoctorServiceValidateBeforeSaveReturnsTrue() throws Exception {
        // Setup
        // Configure DoctorService.validateBeforeSave(...).
        final Doctor entity = new Doctor();
        entity.setId(0);
        entity.setDoctorPosition(DoctorPosition.CHIEF_PHYSICIAN);
        entity.setWorkroom(0);
        entity.setDoctorShift(DoctorShift.DAILY_SHIFT);
        entity.setShiftStatus(ShiftStatus.OPENED);
        when(mockDoctorService.validateBeforeSave(entity)).thenReturn(true);

        // Configure DoctorService.create(...).
        final Doctor doctor = new Doctor();
        doctor.setId(0);
        doctor.setDoctorPosition(DoctorPosition.CHIEF_PHYSICIAN);
        doctor.setWorkroom(0);
        doctor.setDoctorShift(DoctorShift.DAILY_SHIFT);
        doctor.setShiftStatus(ShiftStatus.OPENED);
        final Doctor entity1 = new Doctor();
        entity1.setId(0);
        entity1.setDoctorPosition(DoctorPosition.CHIEF_PHYSICIAN);
        entity1.setWorkroom(0);
        entity1.setDoctorShift(DoctorShift.DAILY_SHIFT);
        entity1.setShiftStatus(ShiftStatus.OPENED);
        when(mockDoctorService.create(entity1)).thenReturn(doctor);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/doctors")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }
}
