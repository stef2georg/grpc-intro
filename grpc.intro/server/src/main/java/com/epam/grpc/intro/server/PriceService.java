package com.epam.grpc.intro.server;

import com.epam.grpc.intro.common.ThreadSafeStreamObserver;
import com.epam.grpc.intro.messages.proto.PriceRequest;
import com.epam.grpc.intro.messages.proto.PriceResponse;
import com.epam.grpc.intro.messages.proto.PriceServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
final class PriceService extends PriceServiceGrpc.PriceServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriceService.class);

    private final ProductMarket productMarket;

    private final ProductMarketUpdateListener productMarketUpdateListener;

    @Autowired
    private PriceService(final ProductMarket productMarket, final ProductMarketUpdateListener productMarketUpdateListener) {
        this.productMarket = productMarket;
        this.productMarketUpdateListener = productMarketUpdateListener;
    }

    @Override
    public void requestPrice(final PriceRequest priceRequest, final StreamObserver<PriceResponse> priceResponseObserver) {
        priceResponseObserver.onNext(productMarket.requestPrice(priceRequest));
        priceResponseObserver.onCompleted();

        LOGGER.info("Unary price request: {}", priceRequest);
    }

    @Override
    public void streamPrices(final PriceRequest priceRequest, final StreamObserver<PriceResponse> priceResponseObserver) {
        productMarketUpdateListener.addPriceResponseObserver(priceRequest, new ThreadSafeStreamObserver<>(priceResponseObserver));
        LOGGER.info("Server streaming price request: {}", priceRequest);
    }

    @Override
    public StreamObserver<PriceRequest> streamMultiplePrices(final StreamObserver<PriceResponse> priceResponseObserver) {
        return new PriceRequestObserver(productMarketUpdateListener, new ThreadSafeStreamObserver<>(priceResponseObserver));
    }

}
