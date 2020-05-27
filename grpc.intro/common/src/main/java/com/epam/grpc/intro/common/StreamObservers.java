package com.epam.grpc.intro.common;

import io.grpc.stub.StreamObserver;

public final class StreamObservers {

    private StreamObservers() {
    }

    public static <V> StreamObserver<V> withErrorHandling(final StreamObserver<V> streamObserver) {
        return new StreamObserverWithErrorHandling<>(streamObserver);
    }

    public static <V> StreamObserver<V> threadSafeWithErrorHandling(final StreamObserver<V> streamObserver) {
        return withErrorHandling(new ThreadSafeStreamObserver<>(streamObserver));
    }

}
