package com.ecomm.customerservice.service;

import com.ecomm.customerservice.model.Customer;
import com.ecomm.customerservice.repository.CustomerRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository repository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private Customer customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customer = new Customer("123", "John", "john@example.com", "123 Main Street", true);
    }

    @Test
    void testGetAllCustomers() {
        when(repository.findAll()).thenReturn(List.of(customer));

        List<Customer> customers = customerService.getAllCustomers();

        assertEquals(1, customers.size());
        assertEquals("John", customers.get(0).getName());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testGetCustomerById() {
        when(repository.findById("123")).thenReturn(Optional.of(customer));

        Customer found = customerService.getCustomer("123");

        assertNotNull(found);
        assertEquals("John", found.getName());
    }

    @Test
    void testSaveCustomer() throws JsonProcessingException {
        when(repository.save(customer)).thenReturn(customer);
        when(objectMapper.writeValueAsString(customer)).thenReturn("{\"id\":\"123\"}");

        Customer saved = customerService.saveCustomer(customer);

        assertEquals("123", saved.getId());
        verify(kafkaTemplate, times(1)).send(eq("customer-events"), anyString());
    }

    @Test
    void testDefaultCustomers() {
        List<Customer> fallback = customerService.defaultCustomers();
        assertTrue(fallback.isEmpty());
    }
}