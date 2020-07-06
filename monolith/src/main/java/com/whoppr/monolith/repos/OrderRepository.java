package com.whoppr.monolith.repos;

import com.whoppr.monolith.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {
}
