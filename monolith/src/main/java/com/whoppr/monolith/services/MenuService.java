package com.whoppr.monolith.services;

import com.whoppr.monolith.exceptions.NotFoundException;
import com.whoppr.monolith.model.MenuItem;
import com.whoppr.monolith.model.Recipe;
import com.whoppr.monolith.repos.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Service
public class MenuService {

  @Autowired
  public MenuItemRepository menuItemRepository;

  @DeleteMapping("/menu-items")
  public void deleteMenuItems() {
    menuItemRepository.deleteAll();
  }

  @PostMapping(value = "/menu-item", consumes = "application/json")
  public void addMenuItem(@RequestBody MenuItem menuItem) {
    menuItemRepository.save(menuItem);
  }

  @GetMapping(value = "/menu-items", produces = "application/json")
  public List<MenuItem> getMenuItems() {
    return menuItemRepository.findAll();
  }

  @GetMapping(value = "/menu-item/{menuItemId}", produces = "application/json")
  public MenuItem getMenuItem(@PathVariable("menuItemId") String menuItemId) {
    return menuItemRepository
        .findById(menuItemId)
        .orElseThrow(NotFoundException::new);
  }

  @GetMapping(value = "/menu-item/{menuItemId}/recipe", produces = "application/json")
  public Recipe getRecipe(@PathVariable("menuItemId") String menuItemId) {
    return menuItemRepository
        .findById(menuItemId)
        .orElseThrow(NotFoundException::new)
        .getRecipe();
  }
}
