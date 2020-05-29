package com.epam.grpc.intro.common;

import io.grpc.stub.StreamObserver;

public final class ThreadSafeStreamObserver<V> implements StreamObserver<V> {

    private final StreamObserver<V> streamObserver;

    private final Object lock;

    public ThreadSafeStreamObserver(final StreamObserver<V> streamObserver) {
        this.streamObserver = streamObserver;
        this.lock = new Object();
    }

    @Override
    public void onNext(final V value) {
        synchronized (lock) {
            streamObserver.onNext(value);
        }
    }

    @Override
    public void onError(final Throwable throwable) {
        synchronized (lock) {
            streamObserver.onError(throwable);
        }
    }

    @Override
    public void onCompleted() {
        synchronized (lock) {
            streamObserver.onCompleted();
        }
    }

}
