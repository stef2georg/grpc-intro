package com.epam.grpc.intro.server;

import com.epam.grpc.intro.messages.proto.PriceRequest;
import com.epam.grpc.intro.messages.proto.PriceResponse;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public final class ProductMarketUpdateListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductMarketUpdateListener.class);

    private final ProductMarket productMarket;

    private final Map<String, Set<StreamObserver<PriceResponse>>> productToPriceResponseObservers;

    @Autowired
    public ProductMarketUpdateListener(@Lazy final ProductMarket productMarket) {
        this.productMarket = productMarket;
        this.productToPriceResponseObservers = new ConcurrentHashMap<>();
    }

    @PreDestroy
    private void stop() {
        productToPriceResponseObservers.values().stream().flatMap(Set::stream).forEach(StreamObserver::onCompleted);
    }

    void addPriceResponseObserver(final PriceRequest priceRequest, final StreamObserver<PriceResponse> priceResponseObserver) {
        final String product = priceRequest.getProduct();
        productToPriceResponseObservers.computeIfAbsent(product, p -> ConcurrentHashMap.newKeySet()).add(priceResponseObserver);

        productMarket.requestPrice(priceRequest);
    }

    void onUpdatedPrices(final List<PriceResponse> updatedPriceResponses) {
        for (final PriceResponse updatedPriceResponse : updatedPriceResponses) {
            final String product = updatedPriceResponse.getProduct();
            if (!productToPriceResponseObservers.containsKey(product)) {
                continue;
            }

            final Set<StreamObserver<PriceResponse>> errorPriceResponseObservers = new HashSet<>();
            final Set<StreamObserver<PriceResponse>> priceResponseObservers = productToPriceResponseObservers.get(product);
            for (final StreamObserver<PriceResponse> priceResponseObserver : priceResponseObservers) {
                try {
                    priceResponseObserver.onNext(updatedPriceResponse);
                } catch (final StatusRuntimeException exception) {
                    LOGGER.error("Removing observer - unable to send price response {}", updatedPriceResponse, exception);
                    errorPriceResponseObservers.add(priceResponseObserver);
                }
            }

            priceResponseObservers.removeAll(errorPriceResponseObservers);
        }
    }

}
