package com.epam.grpc.intro.server;

import com.epam.grpc.intro.common.StreamObservers;
import com.epam.grpc.intro.messages.proto.PriceRequest;
import com.epam.grpc.intro.messages.proto.PriceResponse;
import com.epam.grpc.intro.messages.proto.PriceServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
final class PriceService extends PriceServiceGrpc.PriceServiceImplBase {

    private final ProductMarket productMarket;

    private final ProductMarketUpdateListener productMarketUpdateListener;

    @Autowired
    private PriceService(final ProductMarket productMarket, final ProductMarketUpdateListener productMarketUpdateListener) {
        this.productMarket = productMarket;
        this.productMarketUpdateListener = productMarketUpdateListener;
    }

    @Override
    public void requestPrice(final PriceRequest priceRequest, final StreamObserver<PriceResponse> priceResponseObserver) {
        final StreamObserver<PriceResponse> wrappedPriceResponseObserver = StreamObservers.withErrorHandling(priceResponseObserver);
        final PriceResponse priceResponse = productMarket.requestPrice(priceRequest);

        wrappedPriceResponseObserver.onNext(priceResponse);
        wrappedPriceResponseObserver.onCompleted();
    }

    @Override
    public void streamPrices(final PriceRequest priceRequest, final StreamObserver<PriceResponse> priceResponseObserver) {
        final StreamObserver<PriceResponse> wrappedPriceResponseObserver = StreamObservers.threadSafeWithErrorHandling(priceResponseObserver);
        productMarketUpdateListener.addPriceResponseObserver(priceRequest, wrappedPriceResponseObserver);
    }

    @Override
    public StreamObserver<PriceRequest> streamMultiplePrices(final StreamObserver<PriceResponse> priceResponseObserver) {
        final StreamObserver<PriceResponse> wrappedPriceResponseObserver = StreamObservers.threadSafeWithErrorHandling(priceResponseObserver);
        return new PriceRequestObserver(productMarketUpdateListener, wrappedPriceResponseObserver);
    }

}
