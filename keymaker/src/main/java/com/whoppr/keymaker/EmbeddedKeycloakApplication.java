package com.whoppr.keymaker;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.services.managers.ApplianceBootstrap;
import org.keycloak.services.managers.RealmManager;
import org.keycloak.services.resources.KeycloakApplication;
import org.keycloak.services.util.JsonConfigProviderFactory;
import org.keycloak.util.JsonSerialization;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.NoSuchElementException;

@Slf4j
public class EmbeddedKeycloakApplication extends KeycloakApplication {
  static KeyCloakServerProperties keycloakServerProperties;

  protected void loadConfig() {
    JsonConfigProviderFactory factory = new RegularJsonConfigProviderFactory();
    Config.init(factory.create()
        .orElseThrow(() -> new NoSuchElementException("No value present")));
  }

  public EmbeddedKeycloakApplication() {
    createMasterRealmAdminUser();
    createWhopprRealm();
  }

  private void createMasterRealmAdminUser() {
    KeycloakSession session = getSessionFactory().create();
    ApplianceBootstrap applianceBootstrap = new ApplianceBootstrap(session);
    AdminUser admin = keycloakServerProperties.getAdminUser();
    try {
      session.getTransactionManager().begin();
      applianceBootstrap.createMasterRealmUser(admin.getUsername(), admin.getPassword());
      session.getTransactionManager().commit();
    } catch (Exception ex) {
      session.getTransactionManager().rollback();
    }
    session.close();
  }

  private void createWhopprRealm() {
    log.info("************* CREATING WHOPPR REALM**************");
    KeycloakSession session = getSessionFactory().create();
    try {
      session.getTransactionManager().begin();
      RealmManager manager = new RealmManager(session);
      Resource lessonRealmImportFile = new ClassPathResource(
          keycloakServerProperties.getRealmImportFile());
      manager.importRealm(JsonSerialization.readValue(lessonRealmImportFile.getInputStream(),
          RealmRepresentation.class));
      session.getTransactionManager().commit();
    } catch (Exception ex) {
      log.error("************* ERROR CREATING WHOPPR REALM**************", ex);
      session.getTransactionManager().rollback();
    }
    session.close();
  }
}
