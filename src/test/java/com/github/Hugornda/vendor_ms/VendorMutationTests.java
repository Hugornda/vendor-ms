package com.github.Hugornda.vendor_ms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class VendorMutationTests {

    @Autowired
    private WebTestClient webTestClient;


    private final String graphqlQuery = "{ \"query\": " +
            "\"mutation { " +
            "createVendor(name: \\\"Test vendor\\\", numberOfEmployees: 123, country: \\\"Portugal\\\") " +
            "{ name numberOfEmployees country } }\" }";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateVendorWithAdminRole(){
        webTestClient.post()
                .uri("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(graphqlQuery)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.createVendor.name").isEqualTo("Test vendor");
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testCreateResourceMutationUnauthorized() {

        webTestClient.post()
                .uri("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(graphqlQuery)
                .exchange()
                .expectBody()
                .jsonPath("$.errors[0].message").isEqualTo("Forbidden")
                .jsonPath("$.data.createVendor").doesNotExist();
    }
}
