package com.amy.web.controller;

import com.amy.common.security.AuthClientInterceptor;
import com.amy.common.security.JWTUtil;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.web.bind.annotation.*;
import srms.MessageTypes.SaleRequest;
import srms.MessageTypes.SaleResponse;
import srms.SalesServiceGrpc;

@RestController
public class SalesController {

    @PostMapping("/sale")
    public String processSale(@RequestParam String productId, @RequestParam int quantity) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9091)
                .usePlaintext()
                .intercept(new AuthClientInterceptor(JWTUtil.generateToken("web-user")))
                .build();

        SalesServiceGrpc.SalesServiceBlockingStub stub = SalesServiceGrpc.newBlockingStub(channel);

        SaleRequest request = SaleRequest.newBuilder()
                .setProductId(productId)
                .setQuantity(quantity)
                .build();

        SaleResponse response = stub.processSale(request);
        channel.shutdown();

        return "sale result：" + response.getMessage();
    }
}
