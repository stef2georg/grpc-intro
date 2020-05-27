package com.epam.grpc.intro.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
final class GrpcServerAsynchronousWaiting {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcServerAsynchronousWaiting.class);

    private final GrpcServer server;

    private final ExecutorService serverWaitingExecutor;

    @Autowired
    private GrpcServerAsynchronousWaiting(final GrpcServer server) {
        this.server = server;
        this.serverWaitingExecutor = Executors.newSingleThreadExecutor();
    }

    @PostConstruct
    private void start() {
        serverWaitingExecutor.submit(() -> {
            try {
                server.awaitTermination();
            } catch (final InterruptedException exception) {
                LOGGER.info("Server interrupted!", exception);
            }
        });
    }

    @PreDestroy
    private void destroy() {
        serverWaitingExecutor.shutdownNow();
    }

}
