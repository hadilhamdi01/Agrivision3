package com.example.AUTH_SERVICE.services;

import com.example.AUTH_SERVICE.dto.RegisterRequest;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeycloakUserService {

    private final Keycloak keycloak;

    public KeycloakUserService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public void register(RegisterRequest req) {

        UserRepresentation user = new UserRepresentation();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setEnabled(true);

        CredentialRepresentation password = new CredentialRepresentation();
        password.setType(CredentialRepresentation.PASSWORD);
        password.setValue(req.getPassword());
        password.setTemporary(false);

        user.setCredentials(List.of(password));

  Response response = keycloak
    .realm("agrivision")
    .users()
    .create(user);

if (response.getStatus() == 409) {
    throw new RuntimeException("Username or email already exists");
}

if (response.getStatus() != 201) {
    String error = response.readEntity(String.class);
    throw new RuntimeException("Keycloak error: " + error);
}

    }
}
