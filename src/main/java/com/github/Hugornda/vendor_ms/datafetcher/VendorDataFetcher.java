package com.github.Hugornda.vendor_ms.datafetcher;

import com.github.Hugornda.vendor_ms.model.Vendor;
import com.github.Hugornda.vendor_ms.service.VendorService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.DgsSubscription;
import com.netflix.graphql.dgs.InputArgument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import static com.github.Hugornda.vendor_ms.utils.FormatterUtils.toJsonString;

@DgsComponent
public class VendorDataFetcher {

    private static final Logger log = LoggerFactory.getLogger(VendorDataFetcher.class);

    private final VendorService vendorService;

    public VendorDataFetcher(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @DgsSubscription(field = "vendors")
    public Flux<Vendor> getVendors() {
        return vendorService.findAll()
                .doOnNext(res->log.info(toJsonString(res)));
    }

    @DgsQuery
    public Flux<Vendor> getAllVendors() {
        return vendorService.getAllVendors();
    }

    @DgsQuery(field = "filteredVendors")
    public Flux<Vendor> getFilteredVendors(@InputArgument Integer maxEmployees){
        return vendorService.getVendorsFilteredByEmployeeNumber(maxEmployees);
    }

}
