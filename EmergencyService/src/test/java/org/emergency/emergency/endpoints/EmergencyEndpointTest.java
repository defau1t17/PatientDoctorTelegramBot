package org.emergency.emergency.endpoints;

import org.emergency.emergency.dto.DoctorDTO;
import org.emergency.emergency.dto.PatientDTO;
import org.emergency.emergency.dto.UserDTO;
import org.emergency.emergency.service.PatientStateResolverService;
import org.emergency.emergency.service.RequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EmergencyEndpoint.class)
class EmergencyEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService mockRequestService;
    @MockBean

    private PatientStateResolverService mockResolverService;

    private DoctorDTO doctorDTO;

    private PatientDTO patientDTO;

    @BeforeEach
    void setUp() {
        doctorDTO = new DoctorDTO(1, "", "NURSE", new UserDTO(1));
        patientDTO = new PatientDTO(1, 1, "STABLE");
    }


    @Test
    void testGetHelp() throws Exception {
        when(mockRequestService.getPatientByChatID(anyLong())).thenReturn(Optional.of(patientDTO));
        when(mockRequestService.getDoctorsByPatientID(anyInt())).thenReturn(Optional.of(List.of(doctorDTO)));

        final MockHttpServletResponse response = mockMvc.perform(post("/help")
                        .param("chatID", String.valueOf(anyLong()))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        verify(mockResolverService).emergencyCallerByPatientState(List.of(doctorDTO), patientDTO);
    }

    @Test
    void testGetHelp_RequestServiceGetPatientByChatIDReturnsAbsent() throws Exception {
        when(mockRequestService.getPatientByChatID(anyLong())).thenReturn(Optional.empty());
        final MockHttpServletResponse response = mockMvc.perform(post("/help")
                        .param("chatID", String.valueOf(anyLong()))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }
}
