package com.whoppr.monolith.repos;

import com.whoppr.common.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {
}
