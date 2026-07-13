package com.jawad.bank.account.services;

import com.jawad.bank.account.dtos.ClientDto;
import com.jawad.bank.account.dtos.RegisterRequest;
import com.jawad.bank.account.entities.Account;
import com.jawad.bank.account.entities.AccountStatus;
import com.jawad.bank.account.entities.AccountType;
import com.jawad.bank.account.entities.Client;
import com.jawad.bank.account.mappers.ClientMapper;
import com.jawad.bank.account.repositories.AccountRepository;
import com.jawad.bank.account.repositories.ClientRepository;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RegistrationService {

    private final Keycloak keycloak;

    private final ClientRepository clientRepository;

    private final AccountRepository accountRepository;

    private final ClientMapper clientMapper;

    public ClientDto register(RegisterRequest request) {

        //----------------------------------------------------
        // Create Keycloak user
        //----------------------------------------------------

        UserRepresentation user = new UserRepresentation();

        user.setEnabled(true);
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());

        CredentialRepresentation credential = new CredentialRepresentation();

        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setTemporary(false);
        credential.setValue(request.getPassword());

        user.setCredentials(Collections.singletonList(credential));

        Response response = keycloak.realm("banking")
                .users()
                .create(user);

        if (response.getStatus() != 201) {
            throw new RuntimeException("Cannot create Keycloak user");
        }

        //----------------------------------------------------
        // Get created user id
        //----------------------------------------------------

        String keycloakId = response
                .getLocation()
                .getPath()
                .replaceAll(".*/([^/]+)$", "$1");

        //----------------------------------------------------
        // Assign CLIENT role
        //----------------------------------------------------

        UserResource userResource =
                keycloak.realm("banking")
                        .users()
                        .get(keycloakId);

        RoleRepresentation clientRole =
                keycloak.realm("banking")
                        .roles()
                        .get("CLIENT")
                        .toRepresentation();

        userResource.roles()
                .realmLevel()
                .add(Collections.singletonList(clientRole));

        //----------------------------------------------------
        // Save client in database
        //----------------------------------------------------

        Client client = new Client();

        client.setFullName(
                request.getFirstName() + " " + request.getLastName()
        );
        client.setEmail(request.getEmail());
        client.setCin(request.getCin());

        client.setKeycloakUserId(keycloakId);

        Client saved = clientRepository.save(client);

        Account account = new Account();

        account.setClientId(saved.getId());

        account.setBalance(request.getBalance() != null ? request.getBalance() : BigDecimal.ZERO);

        account.setAccountNumber(generateAccountNumber());

        account.setAccountType(AccountType.COURANT);   // or SAVINGS

        account.setStatus(AccountStatus.ACTIVE);

        accountRepository.save(account);

        return clientMapper.toDto(saved);
    }

    private String generateAccountNumber() {
        return "ACC-" + UUID.randomUUID()
                .toString()
                .substring(0,8)
                .toUpperCase();
    }
}