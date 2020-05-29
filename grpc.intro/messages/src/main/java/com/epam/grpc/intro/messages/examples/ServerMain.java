package com.epam.grpc.intro.messages.examples;

import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) throws IOException {
        Server server = NettyServerBuilder.forPort(8080)
                .addService(new DemoService())
                .build();
        server.start();
        server.shutdownNow();
    }
}
