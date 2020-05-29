package com.epam.grpc.intro.server;

import com.epam.grpc.intro.messages.proto.PriceOverride;
import com.epam.grpc.intro.messages.proto.PriceRequest;
import com.epam.grpc.intro.messages.proto.PriceResponse;
import com.epam.grpc.intro.messages.proto.PriceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component
class ProductMarket {

    private final ProductMarketUpdateListener productMarketUpdateListener;

    private final ScheduledExecutorService priceUpdateExecutor;

    private final Map<String, PriceResponse> productToPriceResponse;

    @Autowired
    ProductMarket(final ProductMarketUpdateListener productMarketUpdateListener) {
        this.productMarketUpdateListener = productMarketUpdateListener;
        this.priceUpdateExecutor = Executors.newSingleThreadScheduledExecutor();
        this.productToPriceResponse = new ConcurrentHashMap<>();
    }

    @PostConstruct
    private void start() {
        priceUpdateExecutor.scheduleAtFixedRate(this::updatePrices, 1, 1, TimeUnit.SECONDS);
    }

    @PreDestroy
    private void stop() {
        priceUpdateExecutor.shutdownNow();
    }

    private void updatePrices() {
        final List<PriceResponse> updatedPriceResponses = productToPriceResponse.values()
                .stream()
                .filter(priceResponse -> priceResponse.getPriceType() == PriceType.SOURCE)
                .map(priceResponse -> createPriceResponse(priceResponse.getProduct()))
                .collect(Collectors.toList());

        // Simplified for readability - does not check if a price response was concurrently overridden.
        updatedPriceResponses.forEach(updatedPriceResponse -> productToPriceResponse.put(updatedPriceResponse.getProduct(), updatedPriceResponse));

        productMarketUpdateListener.onUpdatedPrices(updatedPriceResponses);
    }

    PriceResponse requestPrice(final PriceRequest priceRequest) {
        return productToPriceResponse.computeIfAbsent(priceRequest.getProduct(), ProductMarket::createPriceResponse);
    }

    private static PriceResponse createPriceResponse(final String product) {
        return PriceResponse.newBuilder()
                .setProduct(product)
                .setPrice(ThreadLocalRandom.current().nextDouble())
                .setPriceType(PriceType.SOURCE)
                .build();
    }

    void overridePrice(final PriceOverride priceOverride) {
        final String product = priceOverride.getProduct();
        final double price = priceOverride.getPrice();
        productToPriceResponse.put(product, createPriceResponseForOverride(product, price));
    }

    private static PriceResponse createPriceResponseForOverride(final String product, final double price) {
        return PriceResponse.newBuilder()
                .setProduct(product)
                .setPrice(price)
                .setPriceType(PriceType.OVERRIDE)
                .build();
    }

}
