package com.epam.grpc.intro.messages.examples;

import com.epam.grpc.intro.messages.example.proto.DemoServiceGrpc;
import com.epam.grpc.intro.messages.example.proto.Request;
import com.epam.grpc.intro.messages.example.proto.Response;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;

public class ClientMain {
    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        Request request = Request.newBuilder().build();

        DemoServiceGrpc.DemoServiceBlockingStub blockingStub = DemoServiceGrpc.newBlockingStub(managedChannel);
        try {
            Response response = blockingStub.sendRequest(request);
        } catch (StatusRuntimeException exception) {

        }

        try {
            Iterator<Response> responses = blockingStub.streamResponses(request);
        } catch (StatusRuntimeException exception) {

        }

        DemoServiceGrpc.DemoServiceFutureStub futureStub = DemoServiceGrpc.newFutureStub(managedChannel);
        ListenableFuture<Response> responseFuture = futureStub.sendRequest(request);
        try {
            Response response = responseFuture.get();
        } catch (ExecutionException | InterruptedException exception) {

        }

        DemoServiceGrpc.DemoServiceStub asynchronousStub = DemoServiceGrpc.newStub(managedChannel);
        asynchronousStub.sendRequest(request, new StreamObserver<>() {
            @Override
            public void onNext(Response response) {

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        });

        asynchronousStub.streamResponses(request, new StreamObserver<>() {
            @Override
            public void onNext(Response response) {

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        });

        Request request1 = Request.newBuilder().build();
        Request request2 = Request.newBuilder().build();

        StreamObserver<Request> requestObserver = asynchronousStub.streamRequests(new StreamObserver<>() {
            @Override
            public void onNext(Response response) {

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        });

        requestObserver.onNext(request1);
        requestObserver.onNext(request2);
        requestObserver.onCompleted();

        StreamObserver<Request> requestObserver2 = asynchronousStub.streamRequestsAndResponses(new StreamObserver<>() {
            @Override
            public void onNext(Response response) {

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        });

        requestObserver2.onNext(request1);
        requestObserver2.onNext(request2);
        requestObserver2.onCompleted();

        managedChannel.shutdownNow();
    }
}
