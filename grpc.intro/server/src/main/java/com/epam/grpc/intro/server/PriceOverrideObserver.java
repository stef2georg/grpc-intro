package com.epam.grpc.intro.server;

import com.epam.grpc.intro.messages.proto.PriceOverride;
import com.epam.grpc.intro.messages.proto.PriceOverrideSummary;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

final class PriceOverrideObserver implements StreamObserver<PriceOverride> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriceOverrideObserver.class);

    private final ProductMarket productMarket;

    private final StreamObserver<PriceOverrideSummary> priceOverrideSummaryObserver;

    private final List<PriceOverride> priceOverrides;

    PriceOverrideObserver(final ProductMarket productMarket, final StreamObserver<PriceOverrideSummary> priceOverrideSummaryObserver) {
        this.productMarket = productMarket;
        this.priceOverrideSummaryObserver = priceOverrideSummaryObserver;
        this.priceOverrides = new CopyOnWriteArrayList<>();
    }

    @Override
    public void onNext(final PriceOverride priceOverride) {
        priceOverrides.add(priceOverride);
        productMarket.overridePrice(priceOverride);

        LOGGER.info("Price override: {}", priceOverride);
    }

    @Override
    public void onError(final Throwable throwable) {
        LOGGER.error("Error in price override stream!", throwable);
    }

    @Override
    public void onCompleted() {
        final PriceOverrideSummary priceOverrideSummary = PriceOverrideSummary.newBuilder()
                .setTimestamp(System.currentTimeMillis())
                .addAllPriceOverrides(priceOverrides)
                .build();

        priceOverrideSummaryObserver.onNext(priceOverrideSummary);
        priceOverrideSummaryObserver.onCompleted();

        LOGGER.info("Price override stream completed");
    }

}
