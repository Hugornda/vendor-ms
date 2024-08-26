package com.github.Hugornda.vendor_ms;

import com.github.Hugornda.vendor_ms.repository.VendorRepository;
import com.github.Hugornda.vendor_ms.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class VendorDataFetcherTest {

    @Autowired
    private WebTestClient webTestClient;

    @Mock
    private VendorRepository vendorRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllVendors() throws IOException {
        String graphqlQuery = TestUtils.loadGraphQLQuery("graphql/getAllVendors.graphql");
        webTestClient.post()
                .uri("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(graphqlQuery)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.vendors").exists()
                .jsonPath("$.data.vendors").isArray()
                .jsonPath("$.data.vendors[0].numberOfEmployees").isEqualTo(100)
                .jsonPath("$.data.vendors[0].name").isEqualTo("Vendor")
                .jsonPath("$.data.vendors[0].country").isEqualTo("USA")
                .jsonPath("$.data.vendors[1].numberOfEmployees").isEqualTo(200)
                .jsonPath("$.data.vendors[1].name").isEqualTo("Vendor1")
                .jsonPath("$.data.vendors[1].country").isEqualTo("Canada");
    }


    @Test
    public void getAllVendorsUnauthorized() throws IOException {
        String graphqlQuery = TestUtils.loadGraphQLQuery("graphql/getAllVendors.graphql");
        webTestClient.post()
                .uri("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(graphqlQuery)
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
