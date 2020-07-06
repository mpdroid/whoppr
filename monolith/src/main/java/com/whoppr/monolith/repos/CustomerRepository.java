package com.whoppr.monolith.repos;

import com.whoppr.monolith.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerRepository extends MongoRepository<Customer, String> {
}
