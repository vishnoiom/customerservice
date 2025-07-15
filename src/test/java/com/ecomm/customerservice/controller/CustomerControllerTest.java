package com.ecomm.customerservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.ecomm.customerservice.model.Customer;
import com.ecomm.customerservice.service.CustomerService;
import com.ecomm.customerservice.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllCustomers() throws Exception {
        Customer customer = new Customer("123", "Alice", "alice@example.com", "9876543210",true);
        when(customerService.getAllCustomers()).thenReturn(List.of(customer));

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alice"));
    }

    @Test
    void testGetCustomerById() throws Exception {
        Customer customer = new Customer("123", "Alice", "alice@example.com", "9876543210", true);
        when(customerService.getCustomer("123")).thenReturn(customer);

        mockMvc.perform(get("/customers/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void testCreateCustomerValidToken() throws Exception {
        Customer customer = new Customer(null, "Alice", "alice@example.com", "9876543210", true);
        Customer saved = new Customer("9876543210", "Alice", "alice@example.com", "9876543210", true);

        when(tokenService.validateToken(anyString(), anyString())).thenReturn("9876543210");
        when(customerService.saveCustomer(any())).thenReturn(saved);

        mockMvc.perform(post("/customers")
                .header("Authorization", "Bearer xyz")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("9876543210"));
    }

    @Test
    void testCreateCustomerInvalidToken() throws Exception {
        Customer customer = new Customer(null, "Alice", "alice@example.com", "9876543210", true);

        when(tokenService.validateToken(anyString(), anyString()))
                .thenThrow(new org.springframework.web.reactive.function.client.WebClientResponseException(
                        401, "Unauthorized", null, null, null));

        mockMvc.perform(post("/customers")
                .header("Authorization", "Bearer xyz")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isUnauthorized());
    }
}
