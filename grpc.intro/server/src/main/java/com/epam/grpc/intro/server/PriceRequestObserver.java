package com.epam.grpc.intro.server;

import com.epam.grpc.intro.messages.proto.PriceRequest;
import com.epam.grpc.intro.messages.proto.PriceResponse;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class PriceRequestObserver implements StreamObserver<PriceRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriceRequestObserver.class);

    private final ProductMarketUpdateListener productMarketUpdateListener;

    private final StreamObserver<PriceResponse> priceResponseObserver;

    PriceRequestObserver(final ProductMarketUpdateListener productMarketUpdateListener, final StreamObserver<PriceResponse> priceResponseObserver) {
        this.productMarketUpdateListener = productMarketUpdateListener;
        this.priceResponseObserver = priceResponseObserver;
    }

    @Override
    public void onNext(final PriceRequest priceRequest) {
        productMarketUpdateListener.addPriceResponseObserver(priceRequest, priceResponseObserver);
        LOGGER.info("Price request: {}", priceRequest);
    }

    @Override
    public void onError(final Throwable throwable) {
        productMarketUpdateListener.removePriceResponseObserver(priceResponseObserver);
        LOGGER.error("Error in price request stream!", throwable);
    }

    @Override
    public void onCompleted() {
        productMarketUpdateListener.removePriceResponseObserver(priceResponseObserver);
        priceResponseObserver.onCompleted();

        LOGGER.info("Price request stream completed");
    }

}
