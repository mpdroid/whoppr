package com.whoppr.common.clients;

import com.whoppr.common.model.MenuItem;
import com.whoppr.common.model.Recipe;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("menu")
public interface MenuClient {
  @DeleteMapping("/menu-items")
  void deleteMenuItems();

  @PostMapping(value = "/menu-item", consumes = "application/json")
  void addMenuItem(@RequestBody MenuItem menuItem);

  @GetMapping(value = "/menu-items", produces = "application/json")
  List<MenuItem> getMenuItems();

  @GetMapping(value = "/menu-item/{menuItemId}", produces = "application/json")
  MenuItem getMenuItem(@PathVariable("menuItemId") String menuItemId);

  @GetMapping(value = "/menu-item/{menuItemId}/recipe", produces = "application/json")
  Recipe getRecipe(@PathVariable("menuItemId") String menuItemId);
}
