package com.github.Hugornda.vendor_ms.datafetcher;

import com.github.Hugornda.vendor_ms.model.Vendor;
import com.github.Hugornda.vendor_ms.repository.VendorRepository;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import reactor.core.publisher.Flux;

@DgsComponent
public class VendorDataFetcher {


    private final VendorRepository vendorRepository;

    public VendorDataFetcher(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @DgsQuery(field = "vendors")
    public Flux<Vendor> getVendors() {
        return vendorRepository.findAll();
    }
}
