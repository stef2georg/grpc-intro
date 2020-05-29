package com.epam.grpc.intro.client;

import com.epam.grpc.intro.messages.proto.PriceRequest;
import com.epam.grpc.intro.messages.proto.PriceResponse;
import com.epam.grpc.intro.messages.proto.PriceServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
final class PriceClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriceClient.class);

    private final ManagedChannel managedChannel;

    private final ScheduledExecutorService scheduledExecutorService;

    @Autowired
    PriceClient(
            @Value("${grpc.server.host}") final String host,
            @Value("${grpc.server.port}") final int port) {
        managedChannel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .keepAliveTime(2, TimeUnit.MINUTES)
                .build();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    @PostConstruct
    private void start() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            final PriceServiceGrpc.PriceServiceBlockingStub blockingStub = PriceServiceGrpc.newBlockingStub(managedChannel);
            final PriceRequest request = PriceRequest.newBuilder()
                    .setProduct("banica")
                    .build();

            try {
                LOGGER.info("Response: {}", blockingStub.requestPrice(request));
            } catch (final StatusRuntimeException exception) {
                LOGGER.error("Unable to request {}", request, exception);
            }
        }, 2, 2, TimeUnit.SECONDS);

        final PriceServiceGrpc.PriceServiceStub asynchronousStub = PriceServiceGrpc.newStub(managedChannel);
        final PriceRequest request = PriceRequest.newBuilder()
                .setProduct("cheese")
                .build();
        asynchronousStub.streamPrices(request, new StreamObserver<>() {
            @Override
            public void onNext(final PriceResponse response) {
                LOGGER.info("Response: {}", response);
            }

            @Override
            public void onError(final Throwable throwable) {
                LOGGER.error("Unable to request {}", request, throwable);
            }

            @Override
            public void onCompleted() {
                LOGGER.info("Price request completed");
            }
        });
    }

    @PreDestroy
    private void stop() {
        managedChannel.shutdownNow();
        scheduledExecutorService.shutdownNow();
    }

}
