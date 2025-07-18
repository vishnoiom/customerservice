package com.ecomm.customerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class CustomerServiceApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }

}
