package contracts.menu

import org.springframework.cloud.contract.spec.Contract

Contract.make {
  description("Should get menu item")

  request {
     method "GET"
     url "/menu-item/cheese-pizza"
     headers {
       header('Authorization', 'Basic am9zaHVhOmpvc2h1YQ==')
     }
  }
  response {
 	  status 200
    headers {
     contentType(applicationJson())
    }
 	  body (
 	      id: "cheese-pizza",
 	      name: "Cheese Pizza",
 	      unitPrice: 9.99,
 	      recipe: [
 	        ingredients: "1⁄4 ounce dry yeast\n" +
                                   "1 cup water, lukewarm\n" +
                                   "1 teaspoon sugar\n" +
                                   "3 cups bread flour\n" +
                                   "1⁄2 teaspoon salt\n" +
                                   "1 tablespoon olive oil\n" +
                                   "1⁄2 cup tomato puree\n" +
                                   "2 tablespoons tomato paste\n" +
                                   "1 garlic clove, minced\n" +
                                   "1 tablespoon fresh basil, chopped\n" +
                                   "4 ounces fresh mozzarella cheese",
           protocol: "Dough:\n" +
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
                                 "Top with sliced mozzarella cheese and bake at 500°F for 11-13 minutes."
        ]
    )
  }
}
