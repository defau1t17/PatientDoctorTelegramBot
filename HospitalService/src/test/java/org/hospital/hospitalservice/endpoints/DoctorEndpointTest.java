package org.hospital.hospitalservice.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hospital.hospitalservice.dtos.DoctorDTO;
import org.hospital.hospitalservice.entities.*;
import org.hospital.hospitalservice.repositories.DoctorRepository;
import org.hospital.hospitalservice.repositories.UserRepository;
import org.hospital.hospitalservice.services.DoctorService;
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
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(DoctorEndpoint.class)
class DoctorEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoctorService mockDoctorService;
    @MockBean
    private UserService mockUserService;
    @MockBean
    private DoctorRepository mockDoctorRepository;
    @MockBean
    private UserRepository mockUserRepository;
    @MockBean
    private PatientService mockPatientService;

    private final int pageSize = 1;

    private final int pageNumber = 0;

    private final ObjectMapper objectMapper = new ObjectMapper();


    private Doctor doctor;

    @BeforeEach
    void setUp() {
        doctor = spy(Doctor.class);
        doctor.setUser(spy(User.class));
    }

    @Test
    void testGetAllDoctors() throws Exception {
        Page<Doctor> page = new PageImpl<>(List.of(spy(Doctor.class)), PageRequest.of(pageNumber, pageSize), 1L);
        when(mockDoctorRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(mockDoctorService.findAll(anyInt(), anyInt())).thenReturn(page);
        MvcResult mvcResult = mockMvc.perform(get("/doctors")
                        .param("pageNumber", anyString())
                        .param("pageSize", anyString())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        assertEquals(objectMapper.writeValueAsString(page), mvcResult.getResponse().getContentAsString());
    }

    @Test
    void testGetAllDoctors_DoctorServiceReturnsNoItems() throws Exception {
        Page<Doctor> emptyPage = new PageImpl<>(List.of(), PageRequest.of(pageNumber, pageSize), 1L);
        when(mockDoctorService.findAll(anyInt(), anyInt())).thenReturn(emptyPage);
        final MockHttpServletResponse response = mockMvc.perform(get("/doctors")
                        .param("pageNumber", anyString())
                        .param("pageSize", anyString())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(objectMapper.writeValueAsString(emptyPage), response.getContentAsString());
    }

    @Test
    void testGetDoctorByID() throws Exception {
        when(mockDoctorService.findByID(anyLong())).thenReturn(Optional.of(doctor));
        final MockHttpServletResponse response = mockMvc.perform(get("/doctors/{id}", eq(0))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(new ObjectMapper().writeValueAsString(doctor), response.getContentAsString());
        verify(mockDoctorService).findByID(anyLong());
    }

    @Test
    void testGetDoctorByID_DoctorServiceReturnsAbsent() throws Exception {
        when(mockDoctorService.findByID(anyLong())).thenReturn(Optional.empty());
        final MockHttpServletResponse response = mockMvc.perform(get("/doctors/{id}", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    void getAllDoctorsByPatient() throws Exception {
        doctor.setPatients(List.of(spy(Patient.class), spy(Patient.class)));
        when(mockDoctorRepository.findAllByPatientID(anyLong())).thenReturn(Optional.of(List.of(doctor)));
        final MockHttpServletResponse response = mockMvc.perform(get("/doctors/patient/{id}", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNotEquals(doctor.getPatients().size(), 0);
        assertEquals(objectMapper.writeValueAsString(List.of(doctor)), response.getContentAsString());
    }

    @Test
    void testTest_DoctorRepositoryReturnsAbsent() throws Exception {
        when(mockDoctorRepository.findAllByPatientID(anyLong())).thenReturn(Optional.empty());
        final MockHttpServletResponse response = mockMvc.perform(get("/doctors/patient/{id}", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    void testFindDoctorByChatID() throws Exception {
        when(mockDoctorService.findByChatID(anyLong())).thenReturn(Optional.of(doctor));
        final MockHttpServletResponse response = mockMvc.perform(get("/doctors/chat/{chatID}", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(objectMapper.writeValueAsString(doctor), response.getContentAsString());
    }

    @Test
    void testFindDoctorByChatID_DoctorServiceReturnsAbsent() throws Exception {
        when(mockDoctorService.findByChatID(anyLong())).thenReturn(Optional.empty());
        final MockHttpServletResponse response = mockMvc.perform(get("/doctors/chat/{chatID}", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    void testCreateNewDoctor() throws Exception {
        when(mockDoctorService.validateBeforeSave(any(Doctor.class))).thenReturn(true);
        when(mockUserRepository.save(any(User.class))).thenReturn(new User());
        when(mockDoctorService.create(any(Doctor.class))).thenReturn(doctor);
        final MockHttpServletResponse response = mockMvc.perform(post("/doctors")
                        .content(objectMapper.writeValueAsString(spy(DoctorDTO.class)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(objectMapper.writeValueAsString(doctor), response.getContentAsString());
    }

    @Test
    void testCreateNewDoctorValidationFailure() throws Exception {

        when(mockDoctorService.validateBeforeSave(any(Doctor.class))).thenReturn(false);
        final MockHttpServletResponse response = mockMvc.perform(post("/doctors")
                        .content(objectMapper.writeValueAsString(spy(DoctorDTO.class)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    void testAssignPatientToDoctor() throws Exception {
        when(mockDoctorService.assignPatientToDoctor(anyLong(), anyLong())).thenReturn(true);
        final MockHttpServletResponse response = mockMvc.perform(post("/doctors/{id}/add/patient/{patientID}", anyLong(), anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("assigned successfully", response.getContentAsString());
    }

    @Test
    void testAssignPatientToDoctorFailure() throws Exception {
        when(mockDoctorService.assignPatientToDoctor(anyLong(), anyLong())).thenReturn(false);
        final MockHttpServletResponse response = mockMvc.perform(post("/doctors/{id}/add/patient/{patientID}", anyLong(), anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }


    @Test
    void testUpdateDoctorShift() throws Exception {
        Doctor optional = spy(Doctor.class);
        when(mockDoctorService.findByChatID(anyLong())).thenReturn(Optional.of(optional));
        when(mockDoctorService.changeDoctorShiftStatus(any(Doctor.class))).thenReturn(doctor);
        when(mockDoctorService.update(any(Doctor.class))).thenReturn(doctor);
        final MockHttpServletResponse response = mockMvc.perform(post("/doctors/{chatID}/shift", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNotEquals(objectMapper.writeValueAsString(optional), response.getContentAsString());
    }

    @Test
    void testUpdateDoctorShift_DoctorServiceFindByChatIDReturnsAbsent() throws Exception {
        when(mockDoctorService.findByChatID(anyLong())).thenReturn(Optional.empty());
        final MockHttpServletResponse response = mockMvc.perform(post("/doctors/{chatID}/shift", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    void testDeleteDoctor() throws Exception {
        when(mockDoctorService.findByID(anyLong())).thenReturn(Optional.of(doctor));
        final MockHttpServletResponse response = mockMvc.perform(delete("/doctors/{id}", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("deleted successfully", response.getContentAsString());
        verify(mockDoctorService).deleteByID(anyLong());
    }

    @Test
    void testDeleteDoctor_DoctorServiceFindByIDReturnsAbsent() throws Exception {
        when(mockDoctorService.findByID(anyLong())).thenReturn(Optional.empty());
        final MockHttpServletResponse response = mockMvc.perform(delete("/doctors/{id}", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    void testDeleteDoctorByChatID() throws Exception {
        when(mockDoctorService.findByChatID(anyLong())).thenReturn(Optional.of(doctor));
        final MockHttpServletResponse response = mockMvc.perform(delete("/doctors/chat/{chatID}", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("deleted successfully", response.getContentAsString());
        verify(mockDoctorService).deleteByChatID(anyLong());
    }

    @Test
    void testDeleteDoctorByChatID_DoctorServiceFindByChatIDReturnsAbsent() throws Exception {
        when(mockDoctorService.findByChatID(anyLong())).thenReturn(Optional.empty());
        final MockHttpServletResponse response = mockMvc.perform(delete("/doctors/chat/{chatID}", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    void testUnhookPatientToDoctor() throws Exception {
        Patient optionalPatient = spy(Patient.class);
        doctor.setPatients(List.of(optionalPatient));

        when(mockDoctorService.findByID(anyLong())).thenReturn(Optional.of(doctor));
        when(mockPatientService.findByID(anyLong())).thenReturn(Optional.of(optionalPatient));
        when(mockDoctorService.update(any())).thenReturn(doctor);
        when(mockDoctorService.unhookPatientFromDoctor(anyLong(), anyLong())).thenReturn(true);

        final MockHttpServletResponse response = mockMvc.perform(
                        delete("/doctors/{id}/remove/patient/{patientID}", anyLong(), anyLong())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("unhooked successfully", response.getContentAsString());
    }

    @Test
    void updateDoctorChatID() throws Exception {
        User user = spy(User.class);
        when(mockUserService.findUserByToken(anyString())).thenReturn(Optional.of(spy(User.class)));
        when(mockUserService.updateUserChatID(any(User.class), anyLong())).thenReturn(spy(User.class));

        final MockHttpServletResponse response = mockMvc.perform(patch("/doctors")
                        .param("chatID", "0")
                        .param("token", "token")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(objectMapper.writeValueAsString(user), response.getContentAsString());

        verify(mockUserService).findUserByToken(anyString());
        verify(mockUserService).updateUserChatID(any(User.class), anyLong());
    }

    @Test
    void testUpdatePatientChatID_UserServiceFindUserByTokenReturnsAbsent() throws Exception {
        when(mockUserService.findUserByToken(anyString())).thenReturn(Optional.empty());
        final MockHttpServletResponse response = mockMvc.perform(patch("/doctors")
                        .param("chatID", "0")
                        .param("token", "token")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }
}
