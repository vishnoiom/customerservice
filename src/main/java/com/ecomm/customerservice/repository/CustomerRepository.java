package com.ecomm.customerservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ecomm.customerservice.model.Customer;

public interface CustomerRepository extends MongoRepository<Customer, String> {

}
