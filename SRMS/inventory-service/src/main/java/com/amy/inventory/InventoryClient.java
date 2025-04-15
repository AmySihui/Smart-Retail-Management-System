package com.amy.inventory;

import com.amy.common.security.AuthClientInterceptor;
import com.amy.common.security.JWTUtil;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import srms.InventoryServiceGrpc;
import srms.MessageTypes.ProductRequest;
import srms.MessageTypes.StockResponse;

public class InventoryClient {
    public static void main(String[] args) {
        // connect
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",9090)
        .usePlaintext()
                .intercept(new AuthClientInterceptor(JWTUtil.generateToken("user001")))
                .build();
        // creat stub
        InventoryServiceGrpc.InventoryServiceBlockingStub stub = InventoryServiceGrpc.newBlockingStub(channel);

        // build request
        ProductRequest request = ProductRequest.newBuilder()
                .setProductId("P123")
                .build();

        // call
        StockResponse response = stub.getStockInfo(request);
        System.out.println("inventory response: items " + response.getProductId() + " inventory quantity " + response.getStock());

        // close
        channel.shutdown();
    }
}
