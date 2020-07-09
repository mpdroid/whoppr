package com.whoppr.keymaker;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "keycloak.server")
public class KeyCloakServerProperties {
  String contextPath = "/auth";
  String realmImportFile = "whoppr-realm.json";
  AdminUser adminUser = new AdminUser();
}
