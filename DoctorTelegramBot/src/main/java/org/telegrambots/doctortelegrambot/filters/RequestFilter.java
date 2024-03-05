package org.telegrambots.doctortelegrambot.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class RequestFilter implements Filter {

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String chatID = request.getHeader("chatID");
//        Map<String, Integer> values = Map.of("chatID", Integer.parseInt(chatID));
//        if (chatID != null) {
//            ResponseEntity<HttpStatus> forEntity = restTemplate.exchange("http://localhost:8080/api/v1/authenticate?chatID={chatID}", HttpMethod.GET, null, HttpStatus.class, values);
//            if (forEntity.getStatusCode().equals(HttpStatus.OK)) {
//
//            }
//        } else {
//            System.out.println("Unauthenticated user");
//        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
