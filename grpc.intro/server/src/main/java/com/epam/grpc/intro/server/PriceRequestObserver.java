package com.epam.grpc.intro.server;

import com.epam.grpc.intro.messages.proto.PriceRequest;
import com.epam.grpc.intro.messages.proto.PriceResponse;
import io.grpc.stub.StreamObserver;

final class PriceRequestObserver implements StreamObserver<PriceRequest> {

    private final ProductMarketUpdateListener productMarketUpdateListener;

    private final StreamObserver<PriceResponse> priceResponseObserver;

    PriceRequestObserver(final ProductMarketUpdateListener productMarketUpdateListener, final StreamObserver<PriceResponse> priceResponseObserver) {
        this.productMarketUpdateListener = productMarketUpdateListener;
        this.priceResponseObserver = priceResponseObserver;
    }

    @Override
    public void onNext(final PriceRequest priceRequest) {
        productMarketUpdateListener.addPriceResponseObserver(priceRequest, priceResponseObserver);
    }

    @Override
    public void onError(final Throwable throwable) {
        priceResponseObserver.onCompleted();
    }

    @Override
    public void onCompleted() {
        priceResponseObserver.onCompleted();
    }

}
