package contracts.billing

import org.springframework.cloud.contract.spec.Contract


Contract.make {
  description("Should process payment hold")

  request {
     method "POST"
     url "/payment-hold/joshua/10.79"
     headers {
       header('Authorization', 'Bearer mock-access-token')
     }
  }
  response {
 	  status 200
    headers {
      contentType(applicationJson())
    }
 	  body value(consumer("a-hold-id"), producer($(anyUuid())))
  }
}
