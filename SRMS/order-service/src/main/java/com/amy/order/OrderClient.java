package com.amy.order;

import com.amy.common.security.AuthClientInterceptor;
import com.amy.common.security.JWTUtil;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import srms.MessageTypes.OrderList;
import srms.MessageTypes.ReturnRequest;
import srms.MessageTypes.ReturnResponse;
import srms.MessageTypes.UserRequest;
import srms.OrderServiceGrpc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class OrderClient {
    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9092)
        .usePlaintext()
                .intercept(new AuthClientInterceptor(JWTUtil.generateToken("user001")))
                .build();

        OrderServiceGrpc.OrderServiceBlockingStub blockingStub = OrderServiceGrpc.newBlockingStub(channel);
        UserRequest historyRequest = UserRequest.newBuilder()
                .setUserId("U001")
                .build();

        OrderList orders = blockingStub.getOrderHistory(historyRequest);
        System.out.println("history order：");
        for (String orderId : orders.getOrdersList()) {
            System.out.println(" - " + orderId);
        }

        OrderServiceGrpc.OrderServiceStub asyncStub = OrderServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<ReturnRequest> requestObserver = asyncStub.processReturns(new StreamObserver<ReturnResponse>() {
            @Override
            public void onNext(ReturnResponse response) {
                System.out.println("return response：" + response.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("error:" + t.getMessage());
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("success");
                latch.countDown();
            }
        });


        requestObserver.onNext(ReturnRequest.newBuilder().setOrderId("ORD-001").setProductId("P001").build());
        requestObserver.onNext(ReturnRequest.newBuilder().setOrderId("ORD-002").setProductId("P002").build());
        requestObserver.onNext(ReturnRequest.newBuilder().setOrderId("ORD-003").setProductId("P003").build());


        requestObserver.onCompleted();

        latch.await(3, TimeUnit.SECONDS);

        channel.shutdown();
    }
}
