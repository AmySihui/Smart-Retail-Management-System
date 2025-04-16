package com.amy.web.controller;
import com.amy.common.security.AuthClientInterceptor;
import com.amy.common.security.JWTUtil;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import srms.InventoryServiceGrpc;
import srms.MessageTypes.ProductRequest;
import srms.MessageTypes.StockResponse;

@RestController
public class InventoryController {

    @GetMapping("/inventory")
    public String getStock(@RequestParam("productId") String productId) {

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .intercept(new AuthClientInterceptor(JWTUtil.generateToken("web-user")))
                .build();

        InventoryServiceGrpc.InventoryServiceBlockingStub stub = InventoryServiceGrpc.newBlockingStub(channel);

        ProductRequest request = ProductRequest.newBuilder().setProductId(productId).build();
        StockResponse response = stub.getStockInfo(request);

        channel.shutdown();

        return "item " + productId + " inventory" + response.getStock();
    }
}
