package com.whoppr.monolith.repos;

import com.whoppr.common.model.MenuItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MenuItemRepository extends MongoRepository<MenuItem, String> {
}
