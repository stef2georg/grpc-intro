package com.epam.grpc.intro.messages.examples;

import com.epam.grpc.intro.messages.proto.PriceOverride;

public class DescriptorMain {

    public static void main(String[] args) {
        PriceOverride.getDescriptor()
                .getFields()
                .stream()
                .map(field -> field.getType() + " " + field.getName() + " " + field.getNumber())
                .forEach(System.out::println);
    }

}
