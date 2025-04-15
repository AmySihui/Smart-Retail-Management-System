package com.amy.sales;

import com.amy.common.security.AuthClientInterceptor;
import com.amy.common.security.JWTUtil;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import srms.MessageTypes.SaleRequest;
import srms.MessageTypes.SaleResponse;
import srms.SalesServiceGrpc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SalesClient {
    public static void main(String[] args) throws Exception {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9091)
                .usePlaintext()
                .intercept(new AuthClientInterceptor(JWTUtil.generateToken("user001")))
                .build();

        SalesServiceGrpc.SalesServiceBlockingStub stub =
                SalesServiceGrpc.newBlockingStub(channel);
        SalesServiceGrpc.SalesServiceStub asyncStub = SalesServiceGrpc.newStub(channel);

        //ProcessSale
        SaleRequest request = SaleRequest.newBuilder()
                .setProductId("P001")
                .setQuantity(2)
                .build();

        SaleResponse response = stub.processSale(request);
        System.out.println("result：" + response.getMessage());

        //StreamSalesDate
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<SaleRequest> requestObserver = asyncStub.streamSalesData(new StreamObserver<SaleResponse>() {
            @Override
            public void onNext(SaleResponse value) {
                System.out.println("stream response：" + value.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("error: " + t.getMessage());
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("success");
                latch.countDown();
            }
        });

        requestObserver.onNext(SaleRequest.newBuilder().setProductId("P101").setQuantity(1).build());
        requestObserver.onNext(SaleRequest.newBuilder().setProductId("P102").setQuantity(5).build());
        requestObserver.onNext(SaleRequest.newBuilder().setProductId("P103").setQuantity(3).build());

        requestObserver.onCompleted();

        latch.await(3, TimeUnit.SECONDS);

        channel.shutdown();
    }
}
