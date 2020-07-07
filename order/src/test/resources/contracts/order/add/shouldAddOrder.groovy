package contracts.order.add

import org.springframework.cloud.contract.spec.Contract

Contract.make {
  description("Should add order")

  request {
     method "POST"
     url "/order"
     headers {
       header('Authorization', 'Basic am9zaHVhOmpvc2h1YQ==')
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
  response {
 	  status 200
  }
}
