package com.amy.sales;

import io.grpc.stub.StreamObserver;
import srms.SalesServiceGrpc;
import srms.MessageTypes.SaleRequest;
import srms.MessageTypes.SaleResponse;
import srms.MessageTypes.UserRequest;
import srms.MessageTypes.OrderList;

public class SalesServiceImpl implements SalesServiceGrpc.AsyncService {

    @Override
    public void processSale(SaleRequest request, StreamObserver<SaleResponse> responseObserver) {
        SaleResponse response = SaleResponse.newBuilder()
                .setSuccess(true)
                .setMessage("销售成功")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void streamSalesData(UserRequest request, StreamObserver<OrderList> responseObserver) {
        OrderList orders = OrderList.newBuilder()
                .addOrders("订单001")
                .addOrders("订单002")
                .build();
        responseObserver.onNext(orders);
        responseObserver.onCompleted();
    }
}
