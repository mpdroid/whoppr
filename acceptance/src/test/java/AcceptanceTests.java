import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources")
public class AcceptanceTests {
  private static boolean appsRunning = false;
//  static SpringApplicationBuilder customer;
//  static SpringApplicationBuilder order;
//  static SpringApplicationBuilder registry;

  @BeforeClass
  public static void startApps() {
//    customer = new SpringApplicationBuilder(CustomerApplication.class)
//        .properties("server.port=8080",
//            "SOA.ControllerFactory.enforceProxyCreation=true");
//    customer.run();
//    startRegistryApplication();
//    startOrderApplication();
//    startCustomerApplication();
    appsRunning = true;
  }

//  private static void startCustomerApplication() {
//    customer = makeApplication(CustomerApplication.class, 8080);
//    customer.run();
//  }
//
//  private static void startOrderApplication() {
//    order = makeApplication(OrderApplication.class, 8081);
//    order.run();
//  }
//
//  private static void startRegistryApplication() {
//    registry = makeApplication(RegistryApplication.class, 8761);
//    registry.run();
//  }
//
//  private static SpringApplicationBuilder makeApplication(Class clazz, int port) {
//    return new SpringApplicationBuilder(clazz)
//        .properties("server.port=" + port,
//            "SOA.ControllerFactory.enforceProxyCreation=true");
//  }

}
