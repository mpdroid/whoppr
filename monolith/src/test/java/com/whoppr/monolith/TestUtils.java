package com.whoppr.monolith;

import com.whoppr.monolith.model.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestUtils {

  public static Customer buildTestCustomer() {
    return Customer.builder()
        .id("customer-1")
        .customerToken("joshua")
        .name("Homer Simpson")
        .earnedRewardAmount(2.0)
        .deliveryAddress(
            Address.builder()
                .addressLine1("742 Evergreen Terrace")
                .city("Springfield")
                .build()
        )
        .build();
  }


  public static List<MenuItem> buildTestMenuItems() {
    Recipe cheesePizza = Recipe.builder()
        .ingredients("1⁄4 ounce dry yeast\n" +
            "1 cup water, lukewarm\n" +
            "1 teaspoon sugar\n" +
            "3 cups bread flour\n" +
            "1⁄2 teaspoon salt\n" +
            "1 tablespoon olive oil\n" +
            "1⁄2 cup tomato puree\n" +
            "2 tablespoons tomato paste\n" +
            "1 garlic clove, minced\n" +
            "1 tablespoon fresh basil, chopped\n" +
            "4 ounces fresh mozzarella cheese")
        .protocol("Dough:\n" +
            "In a glass or plastic bowl, combine yeast, water, and sugar (the water can just be water from the tap, make sure it's below 100°F).\n" +
            "Stir to dissolve the yeast and let the yeast \"bloom\" for 15 minutes.\n" +
            "Stir in 1 cup flour, add salt, and then stir in another cup of flour (the remaining cup of flour will be your \"bench\" flour and added flour).\n" +
            "Dump mixture onto kneading board and work in last cup of flour, kneading until dough is soft and elastic, but not sticky. Form dough into a ball.\n" +
            "In another bowl, pour in the 1 tbsp olive oil and spread around.\n" +
            "Coat ball of dough with oil and cover bowl with a damp towel and let dough rise for 40 minutes.\n" +
            "Punch down dough and knead on board about 2 minutes. Dough is now ready to spread in the pan.\n" +
            "To avoid sticking of crust, lightly spray pizza pan with olive oil or vegetable oil spray and then work dough to pan (or use free form pan) - this dough is enough for 1 14-in pizza with a thin bottom crust and enough dough around the edge to munch.\n" +
            "Sauce:\n" +
            "Combine pureed tomatoes, tomato paste, minced garlic, and basil.\n" +
            "Spread onto prepared pizza dough.\n" +
            "Top with sliced mozzarella cheese and bake at 500°F for 11-13 minutes.")
        .build();

    Recipe breadSticks = Recipe.builder()
        .ingredients("2 tablespoons dry yeast\n" +
            "3 cups warm water\n" +
            "4 tablespoons sugar\n" +
            "6 cups flour\n" +
            " garlic powder\n" +
            " parmesan cheese\n" +
            "2 tablespoons butter or 2 tablespoons margarine")
        .protocol("Soften yeast in 1/2 cup of the water.\n" +
            "In a mixing bowl or bread mixer, add rest of water, salt, sugar, and flour.\n" +
            "Mix to a workable state. Knead to a nice consistency.\n" +
            "Melt butter and cover bottom of cookie sheet generously with butter (no substitutes). 5.Sprinkle with garlic powder.\n" +
            "Roll out dough in baking sheet.\n" +
            "Rub with butter and sprinkle parmesan cheese ( I like to add more garlic powder and Italian seasonings at this point).\n" +
            "With a pizza cutter, cut into 1 inch strips longways, then cut them in half the opposite direction.\n" +
            "Let rise for 20 minutes.\n" +
            "Bake at 375 for 15-20 minutes.\n" +
            "May be served warm or cold.")
        .build();
    return Arrays.asList(
        MenuItem.builder()
            .id("cheese-pizza")
            .name("Cheese Pizza")
            .unitPrice(9.99)
            .recipe(cheesePizza)
            .build(),
        MenuItem.builder()
            .id("bread-sticks")
            .name("Bread Sticks")
            .recipe(breadSticks)
            .unitPrice(5.99)
            .build()
    );
  }

  public static ShoppingCart buildTestShoppingCart(
      List<OrderItem> orderItems) {
    Customer customer = buildTestCustomer();
    return ShoppingCart.builder()
        .customerId("customer-1")
        .deliveryAddress(customer.getDeliveryAddress())
        .gratuity(2.0)
        .cartItems(orderItems)
        .applyRewards(true)
        .earnedRewards(2.0)
        .build();
  }

  public static List<OrderItem> buildTestOrderItems(List<MenuItem> menuItems) {
    return Arrays.asList(OrderItem.builder()
        .id("item-1")
        .menuItemId(menuItems.get(0).getId())
        .quantity(1)
        .build());

  }

  public static Order buildTestOrder(Customer customer) {
    List<MenuItem> menuItems = buildTestMenuItems();

    return Order.builder()
        .id("order-1")
        .customerId(customer.getId())
        .orderItems(buildTestOrderItems(Arrays.asList(
            menuItems.get(0))))
        .orderEvents(new ArrayList<>())
        .deliveryAddress(customer.getDeliveryAddress())
        .applyRewards(true)
        .amount(9.99)
        .tax(0.8)
        .discount(2.0)
        .gratuity(2.0)
        .totalAmount(10.79)
        .build();
  }

  public static File buildTestReceipt() throws IOException, URISyntaxException {
    return new File(TestUtils.class.getResource("/receipt.png").toURI());
  }

}
