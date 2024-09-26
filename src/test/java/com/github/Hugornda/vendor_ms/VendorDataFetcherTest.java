package com.github.Hugornda.vendor_ms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.Hugornda.vendor_ms.datafetcher.VendorDataFetcher;
import com.github.Hugornda.vendor_ms.model.Vendor;
import com.github.Hugornda.vendor_ms.repository.VendorRepository;
import com.github.Hugornda.vendor_ms.service.VendorService;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import graphql.ExecutionResult;
import org.dataloader.impl.Assertions;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import static org.mockito.Mockito.when;

@SpringBootTest(classes = {DgsAutoConfiguration.class, VendorDataFetcher.class})
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class VendorDataFetcherTest {


    @Autowired
    DgsQueryExecutor queryExecutor;

    ObjectMapper objectMapper = new ObjectMapper();


    @MockBean
    VendorRepository vendorRepository;

    @MockBean
    private VendorService vendorService;


    @Test
    void vendors() {
        ExecutionResult executionResult = queryExecutor.execute("subscription { vendors { name, price } }");
        Publisher<ExecutionResult> publisher = executionResult.getData();
        Vendor partialVendor = new Vendor("test vendor", 100, "PT"); // Missing ID and country

        when(vendorRepository.findAll()).thenReturn(Flux.just(partialVendor));
        VirtualTimeScheduler virtualTimeScheduler = VirtualTimeScheduler.create();
        StepVerifier.withVirtualTime(() -> publisher, 3)
                .expectSubscription()
                .thenRequest(1)
                .assertNext(result -> Assertions.nonNull(toVendor(result).getName()))
                .thenCancel()
                .verify();
    }

    private Vendor toVendor(ExecutionResult result) {
        Object data = result.getData();
        return objectMapper.convertValue(data, Vendor.class);
    }




}
