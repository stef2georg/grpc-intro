package com.epam.grpc.intro.messages.examples;

import com.epam.grpc.intro.messages.example.proto.DemoServiceGrpc;
import com.epam.grpc.intro.messages.example.proto.Request;
import com.epam.grpc.intro.messages.example.proto.Response;
import io.grpc.stub.StreamObserver;

public class DemoService extends DemoServiceGrpc.DemoServiceImplBase {
    @Override
    public void sendRequest(Request request, StreamObserver<Response> responseObserver) {
        Response response = Response.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void streamResponses(Request request, StreamObserver<Response> responseObserver) {
        Response response1 = Response.newBuilder().build();
        Response response2 = Response.newBuilder().build();
        responseObserver.onNext(response1);
        responseObserver.onNext(response2);
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<Request> streamRequests(StreamObserver<Response> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(Request request) {

            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onCompleted() {
                Response response = Response.newBuilder().build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<Request> streamRequestsAndResponses(StreamObserver<Response> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(Request request) {
                Response response = Response.newBuilder().build();
                responseObserver.onNext(response);
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
