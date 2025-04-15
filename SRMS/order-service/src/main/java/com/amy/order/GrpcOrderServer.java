package com.amy.order;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import srms.OrderServiceGrpc;

public class GrpcOrderServer {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(9092)
                .addService(OrderServiceGrpc.bindService(new OrderServiceImpl()))
                .build();
        System.out.println("Order gRPC server started on port 9092");
        server.start();
        server.awaitTermination();
    }
}
