package com.amy.web.controller;

import com.amy.common.security.AuthClientInterceptor;
import com.amy.common.security.JWTUtil;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.web.bind.annotation.*;
import srms.MessageTypes.UserRequest;
import srms.MessageTypes.OrderList;
import srms.OrderServiceGrpc;

@RestController
public class ReturnController {

    @GetMapping("/return-orders")
    public String getReturnOrders(@RequestParam("userId") String userId) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9092)
                .usePlaintext()
                .intercept(new AuthClientInterceptor(JWTUtil.generateToken("web-user")))
                .build();

        OrderServiceGrpc.OrderServiceBlockingStub stub = OrderServiceGrpc.newBlockingStub(channel);

        UserRequest request = UserRequest.newBuilder()
                .setUserId(userId)
                .build();

        OrderList orderList = stub.getOrderHistory(request);
        channel.shutdown();

        return "user" + userId + "return record" + String.join("，", orderList.getOrdersList());
    }
}
