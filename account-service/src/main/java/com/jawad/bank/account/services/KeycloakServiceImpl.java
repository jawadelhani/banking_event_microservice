package com.jawad.bank.account.services;

import com.jawad.bank.account.dtos.RegisterRequest;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeycloakServiceImpl implements KeycloakService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    @Override
    public String createClient(RegisterRequest request) {

        UserRepresentation user = new UserRepresentation();

        user.setEnabled(true);
        user.setRequiredActions(List.of()); // no required actions
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName()); // make sure RegisterRequest actually has this field
        user.setEmail(request.getEmail());

        user.setFirstName(request.getFirstName());

        CredentialRepresentation credential = new CredentialRepresentation();

        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        credential.setTemporary(false);

        user.setCredentials(List.of(credential));

        Response response =
                keycloak.realm(realm)
                        .users()
                        .create(user);

        if (response.getStatus() != 201) {
            throw new RuntimeException("Cannot create Keycloak user. HTTP " + response.getStatus());
        }

        return CreatedResponseUtil.getCreatedId(response);
    }

}