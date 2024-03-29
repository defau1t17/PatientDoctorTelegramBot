package org.hospital.hospitalservice.endpoints;

import org.hospital.hospitalservice.entities.Patient;
import org.hospital.hospitalservice.entities.PatientState;
import org.hospital.hospitalservice.services.PatientService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PatientEndpoint.class)
class PatientEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService mockPatientService;

    @Test
    void testGetPatientByID() throws Exception {
        // Setup
        // Configure PatientService.findByID(...).
        final Patient patient = new Patient();
        patient.setId(0);
        patient.setDisease("disease");
        patient.setPatientState(PatientState.STABLE);
        patient.setChamberNumber(0);
        patient.setDescription("description");
        final Optional<Patient> optionalPatient = Optional.of(patient);
        when(mockPatientService.findByID(0L)).thenReturn(optionalPatient);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/patients/{id}", 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }

    @Test
    void testGetPatientByID_PatientServiceReturnsAbsent() throws Exception {
        // Setup
        when(mockPatientService.findByID(0L)).thenReturn(Optional.empty());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/patients/{id}", 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }

    @Test
    void testGetPatientByChatID() throws Exception {
        // Setup
        // Configure PatientService.findByChatID(...).
        final Patient patient = new Patient();
        patient.setId(0);
        patient.setDisease("disease");
        patient.setPatientState(PatientState.STABLE);
        patient.setChamberNumber(0);
        patient.setDescription("description");
        final Optional<Patient> optionalPatient = Optional.of(patient);
        when(mockPatientService.findByChatID(0L)).thenReturn(optionalPatient);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/patients/chat/{chatID}", 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }

    @Test
    void testGetPatientByChatID_PatientServiceReturnsAbsent() throws Exception {
        // Setup
        when(mockPatientService.findByChatID(0L)).thenReturn(Optional.empty());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/patients/chat/{chatID}", 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }

    @Test
    void testGetPatients() throws Exception {
        // Setup
        // Configure PatientService.findAll(...).
        final Patient patient = new Patient();
        patient.setId(0);
        patient.setDisease("disease");
        patient.setPatientState(PatientState.STABLE);
        patient.setChamberNumber(0);
        patient.setDescription("description");
        final Page<Patient> patients = new PageImpl<>(List.of(patient));
        when(mockPatientService.findAll(0, 0)).thenReturn(patients);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/patients")
                        .param("page", "0")
                        .param("size", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }

    @Test
    void testGetPatients_PatientServiceReturnsNoItems() throws Exception {
        // Setup
        when(mockPatientService.findAll(0, 0)).thenReturn(new PageImpl<>(Collections.emptyList()));

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/patients")
                        .param("page", "0")
                        .param("size", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }

    @Test
    void testCreatePatient() throws Exception {
        // Setup
        // Configure PatientService.validateBeforeSave(...).
        final Patient entity = new Patient();
        entity.setId(0);
        entity.setDisease("disease");
        entity.setPatientState(PatientState.STABLE);
        entity.setChamberNumber(0);
        entity.setDescription("description");
        when(mockPatientService.validateBeforeSave(entity)).thenReturn(false);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/patients")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }

    @Test
    void testCreatePatient_PatientServiceValidateBeforeSaveReturnsTrue() throws Exception {
        // Setup
        // Configure PatientService.validateBeforeSave(...).
        final Patient entity = new Patient();
        entity.setId(0);
        entity.setDisease("disease");
        entity.setPatientState(PatientState.STABLE);
        entity.setChamberNumber(0);
        entity.setDescription("description");
        when(mockPatientService.validateBeforeSave(entity)).thenReturn(true);

        // Configure PatientService.create(...).
        final Patient patient = new Patient();
        patient.setId(0);
        patient.setDisease("disease");
        patient.setPatientState(PatientState.STABLE);
        patient.setChamberNumber(0);
        patient.setDescription("description");
        final Patient entity1 = new Patient();
        entity1.setId(0);
        entity1.setDisease("disease");
        entity1.setPatientState(PatientState.STABLE);
        entity1.setChamberNumber(0);
        entity1.setDescription("description");
        when(mockPatientService.create(entity1)).thenReturn(patient);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/patients")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }

    @Test
    void testDeletePatientByID() throws Exception {
        // Setup
        // Configure PatientService.findByID(...).
        final Patient patient = new Patient();
        patient.setId(0);
        patient.setDisease("disease");
        patient.setPatientState(PatientState.STABLE);
        patient.setChamberNumber(0);
        patient.setDescription("description");
        final Optional<Patient> optionalPatient = Optional.of(patient);
        when(mockPatientService.findByID(0L)).thenReturn(optionalPatient);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(delete("/patients/{id}", 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());

        // Confirm PatientService.delete(...).
        final Patient entity = new Patient();
        entity.setId(0);
        entity.setDisease("disease");
        entity.setPatientState(PatientState.STABLE);
        entity.setChamberNumber(0);
        entity.setDescription("description");
        verify(mockPatientService).delete(entity);
    }

    @Test
    void testDeletePatientByID_PatientServiceFindByIDReturnsAbsent() throws Exception {
        // Setup
        when(mockPatientService.findByID(0L)).thenReturn(Optional.empty());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(delete("/patients/{id}", 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }

    @Test
    void testDeletePatientByChatID() throws Exception {
        // Setup
        // Configure PatientService.findByChatID(...).
        final Patient patient = new Patient();
        patient.setId(0);
        patient.setDisease("disease");
        patient.setPatientState(PatientState.STABLE);
        patient.setChamberNumber(0);
        patient.setDescription("description");
        final Optional<Patient> optionalPatient = Optional.of(patient);
        when(mockPatientService.findByChatID(0L)).thenReturn(optionalPatient);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(delete("/patients/chat/{chatID}", 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
        verify(mockPatientService).deleteByChatID(0L);
    }

    @Test
    void testDeletePatientByChatID_PatientServiceFindByChatIDReturnsAbsent() throws Exception {
        // Setup
        when(mockPatientService.findByChatID(0L)).thenReturn(Optional.empty());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(delete("/patients/chat/{chatID}", 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }
}
