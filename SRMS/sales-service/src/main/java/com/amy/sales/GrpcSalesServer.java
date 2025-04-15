package com.amy.sales;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import srms.SalesServiceGrpc;

public class GrpcSalesServer {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(9091)
                .addService(SalesServiceGrpc.bindService(new SalesServiceImpl()))
                .build();
        System.out.println("Sales gRPC server started on port 9091");
        server.start();
        server.awaitTermination();
    }
}
