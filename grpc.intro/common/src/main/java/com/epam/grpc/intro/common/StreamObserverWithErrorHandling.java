package com.epam.grpc.intro.common;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class StreamObserverWithErrorHandling<V> implements StreamObserver<V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamObserverWithErrorHandling.class);

    private final StreamObserver<V> streamObserver;

    public StreamObserverWithErrorHandling(final StreamObserver<V> streamObserver) {
        this.streamObserver = streamObserver;
    }

    @Override
    public void onNext(final V value) {
        try {
            streamObserver.onNext(value);
        } catch (final StatusRuntimeException exception) {
            onError(exception);
            throw exception;
        }
    }

    @Override
    public void onError(final Throwable throwable) {
        try {
            streamObserver.onError(throwable);
        } catch (final StatusRuntimeException exception) {
            LOGGER.warn("Unable to send error {}!", throwable, exception);
        }
    }

    @Override
    public void onCompleted() {
        try {
            streamObserver.onCompleted();
        } catch (final StatusRuntimeException exception) {
            LOGGER.warn("Unable to send completed!", exception);
        }
    }

}
