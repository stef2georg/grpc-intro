package com.epam.grpc.intro.server;

import com.epam.grpc.intro.common.StreamObservers;
import com.epam.grpc.intro.messages.proto.PriceOverride;
import com.epam.grpc.intro.messages.proto.PriceOverrideServiceGrpc;
import com.epam.grpc.intro.messages.proto.PriceOverrideSummary;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;

@Component
final class PriceOverrideService extends PriceOverrideServiceGrpc.PriceOverrideServiceImplBase {

    private final ProductMarket productMarket;

    private PriceOverrideService(final ProductMarket productMarket) {
        this.productMarket = productMarket;
    }

    @Override
    public StreamObserver<PriceOverride> streamPriceOverrides(final StreamObserver<PriceOverrideSummary> priceOverrideSummaryObserver) {
        final StreamObserver<PriceOverrideSummary> wrappedPriceResponseObserver = StreamObservers.threadSafeWithErrorHandling(priceOverrideSummaryObserver);
        return new PriceOverrideObserver(productMarket, priceOverrideSummaryObserver);
    }

}
