package com.ecomm.customerservice.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ecomm.customerservice.model.Customer;
import com.ecomm.customerservice.repository.CustomerRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CustomerService {
	@Autowired
    private CustomerRepository repository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;

    private static final String TOPIC = "customer-events";

   // @HystrixCommand(fallbackMethod = "defaultCustomers")
    public List<Customer> getAllCustomers() {
        return repository.findAll();
    }

    public Customer getCustomer(String id) {
        return repository.findById(id).orElse(null);
    }

    public Customer saveCustomer(Customer customer) throws JsonProcessingException {
    	Customer saved = repository.save(customer);
        String json = objectMapper.writeValueAsString(saved);
        kafkaTemplate.send(TOPIC, json);
        return saved;
    }

    public List<Customer> defaultCustomers() {
        return Collections.emptyList();
    }



}
