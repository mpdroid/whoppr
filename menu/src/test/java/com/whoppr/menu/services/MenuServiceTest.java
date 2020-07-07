package com.whoppr.menu.services;

import com.whoppr.common.model.MenuItem;
import com.whoppr.common.model.Recipe;
import com.whoppr.menu.repos.MenuItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static com.whoppr.testutils.TestDataBuddy.buildTestMenuItems;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class MenuServiceTest {

  @InjectMocks
  private MenuService menuService;

  @Mock
  MenuItemRepository menuItemRepository;

  private List<MenuItem> menuItems = buildTestMenuItems();

  @BeforeEach
  void setUp() {
    initMocks(this);
    String id = menuItems.get(0).getId();
    when(menuItemRepository.findById(id))
        .thenReturn(Optional.of(menuItems.get(0)));

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

  @Test
  void getMenuItem() {
    String id = menuItems.get(0).getId();
    menuService.getMenuItem(id);
    verify(menuItemRepository, times(1)).findById(id);
  }

  @Test
  void getRecipe() {
    String id = menuItems.get(0).getId();
    Recipe recipe = menuService.getRecipe(id);
    verify(menuItemRepository, times(1)).findById(id);
    assertThat(recipe).isEqualTo(menuItems.get(0).getRecipe());
  }
}
