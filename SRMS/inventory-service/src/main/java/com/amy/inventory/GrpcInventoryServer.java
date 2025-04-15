package com.amy.inventory;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import srms.InventoryServiceGrpc;

public class GrpcInventoryServer {

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(9090)
                .addService(InventoryServiceGrpc.bindService(new InventoryServiceImpl()))
                .build();

        System.out.println("Inventory gRPC server started on port 9090");
        server.start();
        server.awaitTermination();
    }
}
