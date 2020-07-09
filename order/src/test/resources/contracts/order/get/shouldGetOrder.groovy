package contracts.order.get

import org.springframework.cloud.contract.spec.Contract

Contract.make {
  description("Should get order")

  request {
     method "GET"
     url "/order/order-1"
     headers {
       header('Authorization', 'Bearer mock-access-token')
     }
  }
  response {
 	  status 200
 	  headers {
      contentType(applicationJson())
 	  }
    body (
        id: "order-1",
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
        holdId: "a-hold-id",
        deliveryAddress: [
          addressLine1: "742 Evergreen Terrace",
          city: "Springfield"
        ],
        orderEvents: [ [
            status: "RECEIVED"
          ]
        ]
     )
  }
}
