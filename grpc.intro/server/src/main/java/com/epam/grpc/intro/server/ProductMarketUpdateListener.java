package com.epam.grpc.intro.server;

import com.epam.grpc.intro.messages.proto.PriceRequest;
import com.epam.grpc.intro.messages.proto.PriceResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public final class ProductMarketUpdateListener {

    private final ProductMarket productMarket;

    private final Map<String, Set<StreamObserver<PriceResponse>>> productToPriceResponseObservers;

    @Autowired
    public ProductMarketUpdateListener(@Lazy final ProductMarket productMarket) {
        this.productMarket = productMarket;
        this.productToPriceResponseObservers = new ConcurrentHashMap<>();
    }

    @PreDestroy
    private void stop() {
        productToPriceResponseObservers.values()
                .stream()
                .flatMap(Set::stream)
                .forEach(observer -> observer.onError(Status.UNAVAILABLE.asRuntimeException()));
    }

    void addPriceResponseObserver(final PriceRequest priceRequest, final StreamObserver<PriceResponse> priceResponseObserver) {
        final String product = priceRequest.getProduct();
        productToPriceResponseObservers.computeIfAbsent(product, p -> ConcurrentHashMap.newKeySet()).add(priceResponseObserver);

        productMarket.requestPrice(priceRequest);
    }

    void removePriceResponseObserver(final StreamObserver<PriceResponse> priceResponseObserver) {
        productToPriceResponseObservers.values().forEach(priceResponseObservers -> priceResponseObservers.remove(priceResponseObserver));
    }

    void onUpdatedPrices(final List<PriceResponse> updatedPriceResponses) {
        for (final PriceResponse updatedPriceResponse : updatedPriceResponses) {
            final String product = updatedPriceResponse.getProduct();
            if (!productToPriceResponseObservers.containsKey(product)) {
                continue;
            }

            productToPriceResponseObservers.get(product)
                    .forEach(priceResponseObserver -> priceResponseObserver.onNext(updatedPriceResponse));
        }
    }

}
