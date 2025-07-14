package com.ecomm.customerservice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.ecomm.customerservice.model.Customer;
import com.ecomm.customerservice.service.CustomerService;
import com.ecomm.customerservice.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/customers")
public class CustomerController {
	@Autowired
    private CustomerService customerService;
	
	@Autowired
    TokenService tokenService;

	final String userType="CUSTOMER";
	private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
	
    @GetMapping
    public List<Customer> getAll() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getById(@PathVariable String id) {
    	Customer customer = customerService.getCustomer(id);
        if (customer == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(customer);
    }

    @PostMapping
    public ResponseEntity<?>  create(@RequestBody	 Customer customer, @RequestHeader("Authorization") String tokenValue) throws JsonProcessingException {
    	String phone = null;
    	try
        {
            phone =  tokenService.validateToken(tokenValue, userType);
        }
        catch (WebClientResponseException e)
        {
            logger.info("Token validation failed: " + e.getMessage());
            return ResponseEntity.status(401).body(e.getResponseBodyAsString());
        }
    	customer.setId(phone);
        Customer savedCustomer= customerService.saveCustomer(customer);
        return ResponseEntity.status(201).body(savedCustomer);
    }


}
