package com.github.Hugornda.vendor_ms;

import com.github.Hugornda.vendor_ms.model.Vendor;
import com.github.Hugornda.vendor_ms.repository.VendorRepository;
import com.github.Hugornda.vendor_ms.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.io.IOException;

import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class VendorDataFetcherTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private VendorRepository vendorRepository;


    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllVendors() throws IOException {
        String graphqlQuery = TestUtils.loadGraphQLQuery("graphql/getAllVendors.graphql");
        when(vendorRepository.findAll()).thenReturn(Flux.just(
                new Vendor("Vendor", 100, "USA"),
                new Vendor("Vendor1", 200, "Canada")
        ));
        String responseBody = webTestClient.post()
                .uri("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(graphqlQuery)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        System.out.println("Response Body: " + responseBody);
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


    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllVendorsBadRequest() throws IOException {
        String invalidGraphqlQuery = TestUtils.loadGraphQLQuery("graphql/invalidgetall.graphql"); // Invalid field in query
        webTestClient.post()
                .uri("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidGraphqlQuery)
                .exchange()
                .expectStatus().isOk() // GraphQL often returns a 200 status even for errors
                .expectBody()
                .jsonPath("$.errors").exists(); // Ensure errors are present in the response
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllVendorsDataIntegrity() throws IOException {
        Vendor partialVendor = new Vendor("test vendor", 0, null); // Missing ID and country
        when(vendorRepository.findAll()).thenReturn(Flux.just(partialVendor));

        String graphqlQuery = TestUtils.loadGraphQLQuery("graphql/getAllVendors.graphql");
        webTestClient.post()
                .uri("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(graphqlQuery)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.vendors[0].name").isEqualTo("test vendor")
                .jsonPath("$.data.vendors[0].country").doesNotExist();
    }
}
