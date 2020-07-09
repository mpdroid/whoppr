package contracts.billing

import org.springframework.cloud.contract.spec.Contract

Contract.make {
  description("Should compute order total")

  request {
     method "POST"
     url "/compute-total"
     headers {
       header('Authorization', 'Bearer mock-access-token')
       contentType(applicationJson())
     }
     body (
       customerId: "customer-1",
       cartItems: [
         [
           id: "item-1",
           menuItemId: "cheese-pizza",
           quantity: 1
         ]
       ],
       deliveryAddress: [
         addressLine1: "742 Evergreen Terrace",
         city: "Springfield"
       ],
       gratuity: 2.0,
       applyRewards: true,
       earnedRewards: 2.0
     )
  }
  response {
 	  status 200
    headers {
     contentType(applicationJson())
    }
 	  body (
 	    customerId: "customer-1",
 	    orderItems :[
 	      [
 	        id: "item-1",
 	        menuItemId: "cheese-pizza",
 	        quantity:1
 	      ]
 	    ],
      applyRewards :true,
      amount: 9.99,
      discount:2.0,
      tax: 0.8,
      gratuity: 2.0,
      totalAmount: 10.79,
      deliveryAddress: [
        addressLine1: "742 Evergreen Terrace",
        city: "Springfield"
      ]
    )
  }
}
