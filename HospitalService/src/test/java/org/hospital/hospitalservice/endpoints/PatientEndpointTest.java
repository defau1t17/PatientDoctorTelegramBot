package org.hospital.hospitalservice.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hospital.hospitalservice.dtos.DoctorDTO;
import org.hospital.hospitalservice.dtos.PatientDTO;
import org.hospital.hospitalservice.entities.Doctor;
import org.hospital.hospitalservice.entities.Patient;
import org.hospital.hospitalservice.entities.PatientState;
import org.hospital.hospitalservice.entities.User;
import org.hospital.hospitalservice.services.PatientService;
import org.hospital.hospitalservice.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PatientEndpoint.class)
class PatientEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService mockPatientService;
    @MockBean
    private UserService mockUserService;
    private Patient patient;

    private final int pageSize = 1;

    private final int pageNumber = 0;


    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        patient = spy(Patient.class);
        patient.setUser(spy(User.class));
    }


    @Test
    void testGetPatientByID() throws Exception {
        when(mockPatientService.findByID(anyLong())).thenReturn(Optional.of(patient));

        final MockHttpServletResponse response = mockMvc.perform(get("/patients/{id}", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(objectMapper.writeValueAsString(patient), response.getContentAsString());
    }

    @Test
    void testGetPatientByID_PatientServiceReturnsAbsent() throws Exception {
        when(mockPatientService.findByID(anyLong())).thenReturn(Optional.empty());

        final MockHttpServletResponse response = mockMvc.perform(get("/patients/{id}", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    void testGetPatientByChatID() throws Exception {
        when(mockPatientService.findByChatID(anyLong())).thenReturn(Optional.of(patient));

        final MockHttpServletResponse response = mockMvc.perform(get("/patients/chat/{chatID}", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(objectMapper.writeValueAsString(patient), response.getContentAsString());
    }

    @Test
    void testGetPatientByChatID_PatientServiceReturnsAbsent() throws Exception {
        when(mockPatientService.findByChatID(anyLong())).thenReturn(Optional.empty());

        final MockHttpServletResponse response = mockMvc.perform(get("/patients/chat/{chatID}", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    void testGetPatients() throws Exception {

        final Page<Patient> page = new PageImpl<>(List.of(spy(Patient.class)), PageRequest.of(pageNumber, pageSize), 1L);
        when(mockPatientService.findAll(anyInt(), anyInt())).thenReturn(page);

        final MockHttpServletResponse response = mockMvc.perform(get("/patients")
                        .param("page", anyString())
                        .param("size", anyString())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(objectMapper.writeValueAsString(page), response.getContentAsString());
    }

    @Test
    void testGetPatients_PatientServiceReturnsNoItems() throws Exception {

        final Page<Patient> emptyPage = new PageImpl<>(List.of(), PageRequest.of(pageNumber, pageSize), 1L);
        when(mockPatientService.findAll(0, 0)).thenReturn(emptyPage);

        final MockHttpServletResponse response = mockMvc.perform(get("/patients")
                        .param("page", anyString())
                        .param("size", anyString())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("", response.getContentAsString());
    }

    @Test
    void testCreatePatient() throws Exception {
        when(mockPatientService.validateBeforeSave(any(Patient.class))).thenReturn(true);
        when(mockPatientService.create(any(Patient.class))).thenReturn(patient);

        final MockHttpServletResponse response = mockMvc.perform(post("/patients")
                        .content(objectMapper.writeValueAsString(spy(PatientDTO.class)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(objectMapper.writeValueAsString(patient), response.getContentAsString());
    }

    @Test
    void testCreatePatientValidationFailure() throws Exception {
        when(mockPatientService.validateBeforeSave(any())).thenReturn(false);

        final MockHttpServletResponse response = mockMvc.perform(post("/patients")
                        .content(objectMapper.writeValueAsString(spy(PatientDTO.class)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    void testUpdatePatientChatID() throws Exception {
        User user = spy(User.class);
        when(mockUserService.findUserByToken(anyString())).thenReturn(Optional.ofNullable(spy(User.class)));
        when(mockUserService.updateUserChatID(any(User.class), anyLong())).thenReturn(user);

        final MockHttpServletResponse response = mockMvc.perform(patch("/patients")
                        .param("chatID", "0")
                        .param("token", "token")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(objectMapper.writeValueAsString(user), response.getContentAsString());
    }

    @Test
    void testUpdatePatientChatID_UserServiceFindUserByTokenReturnsAbsent() throws Exception {
        when(mockUserService.findUserByToken(anyString())).thenReturn(Optional.empty());

        final MockHttpServletResponse response = mockMvc.perform(patch("/patients")
                        .param("chatID", "0")
                        .param("token", "token")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    void testDeletePatientByID() throws Exception {
        when(mockPatientService.findByID(any())).thenReturn(Optional.of(patient));

        final MockHttpServletResponse response = mockMvc.perform(delete("/patients/{id}", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("deleted successfully", response.getContentAsString());

        verify(mockPatientService).delete(any());

    }

    @Test
    void testDeletePatientByID_PatientServiceFindByIDReturnsAbsent() throws Exception {
        when(mockPatientService.findByID(anyLong())).thenReturn(Optional.empty());

        final MockHttpServletResponse response = mockMvc.perform(delete("/patients/{id}", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    void testDeletePatientByChatID() throws Exception {
        when(mockPatientService.findByChatID(anyLong())).thenReturn(Optional.of(patient));

        final MockHttpServletResponse response = mockMvc.perform(delete("/patients/chat/{chatID}", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("deleted successfully", response.getContentAsString());

        verify(mockPatientService).deleteByChatID(anyLong());
    }

    @Test
    void testDeletePatientByChatID_PatientServiceFindByChatIDReturnsAbsent() throws Exception {
        when(mockPatientService.findByChatID(anyLong())).thenReturn(Optional.empty());

        final MockHttpServletResponse response = mockMvc.perform(delete("/patients/chat/{chatID}", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }
}
