package com.epam.grpc.intro.server;

import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
final class GrpcServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcServer.class);

    private final ExecutorService applicationExecutor;

    private final Server server;

    @Autowired
    private GrpcServer(
            @Value("${application.executor.pool.size}") final int applicationExecutorPoolSize,
            @Value("${grpc.server.port}") final int port,
            final PriceService priceService,
            final PriceOverrideService priceOverrideService) {
        applicationExecutor = Executors.newFixedThreadPool(applicationExecutorPoolSize);
        server = NettyServerBuilder.forPort(port)
                .executor(applicationExecutor)
                .keepAliveTime(1, TimeUnit.MINUTES)
                .permitKeepAliveTime(1, TimeUnit.MINUTES)
                .addService(priceService)
                .addService(priceOverrideService)
                .build();
    }

    @PostConstruct
    private void start() throws IOException {
        server.start();
        LOGGER.info("Server started on port {}...", server.getPort());
    }

    @PreDestroy
    private void destroy() {
        applicationExecutor.shutdownNow();
        server.shutdownNow();
        LOGGER.info("Server stopped!");
    }

    void awaitTermination() throws InterruptedException {
        server.awaitTermination();
    }

}
