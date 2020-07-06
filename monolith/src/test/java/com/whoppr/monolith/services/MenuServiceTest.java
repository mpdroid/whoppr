package com.whoppr.monolith.services;

import com.whoppr.monolith.model.MenuItem;
import com.whoppr.monolith.repos.MenuItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static com.whoppr.monolith.TestUtils.buildTestMenuItems;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

class MenuServiceTest {

  @InjectMocks
  private MenuService menuService;

  @Mock
  MenuItemRepository menuItemRepository;

  @BeforeEach
  void setUp() {
    initMocks(this);
  }

  @Test
  void deleteMenuItems() {
    menuService.deleteMenuItems();
    verify(menuItemRepository, times(1)).deleteAll();
  }

  @Test
  void addMenuItem() {
    List<MenuItem> menuItems = buildTestMenuItems();
    menuService.addMenuItem(menuItems.get(0));
    verify(menuItemRepository, times(1)).save(menuItems.get(0));
  }

  @Test
  void getMenuItems() {
    menuService.getMenuItems();
    verify(menuItemRepository, times(1)).findAll();
  }

}
