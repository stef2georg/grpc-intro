package com.epam.grpc.intro.messages.examples;

import com.epam.grpc.intro.messages.proto.PriceOverride;
import com.epam.grpc.intro.messages.proto.PriceOverrideSummary;

import java.io.IOException;

public class PriceOverrideDemo {

    public static void main(String[] args) throws IOException {
        PriceOverride banica = PriceOverride.newBuilder()
                .setProduct("Banica")
                .setPrice(2)
                .build();

        PriceOverrideSummary.newBuilder()
                .setTimestamp(System.currentTimeMillis())
                .addPriceOverrides(banica)
                .build();
    }

}
