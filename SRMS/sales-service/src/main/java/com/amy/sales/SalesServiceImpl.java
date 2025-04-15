package com.amy.sales;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import srms.InventoryServiceGrpc;
import srms.MessageTypes;
import srms.SalesServiceGrpc;
import srms.MessageTypes.SaleRequest;
import srms.MessageTypes.SaleResponse;

import java.util.logging.Logger;

public class SalesServiceImpl implements SalesServiceGrpc.AsyncService {
    private static final Logger logger = Logger.getLogger(SalesServiceImpl.class.getName());


    @Override
    public StreamObserver<SaleRequest> streamSalesData(StreamObserver<SaleResponse> responseObserver) {
        return new StreamObserver<SaleRequest>() {
            @Override
            public void onNext(SaleRequest request) {
                SaleResponse response = SaleResponse.newBuilder()
                        .setSuccess(true)
                        .setMessage("received item " + request.getProductId() + " quantity " + request.getQuantity())
                        .build();
                responseObserver.onNext(response);
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("client error:" + t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void processSale(SaleRequest request, StreamObserver<SaleResponse> responseObserver) {
        logger.info("received sales item: " + request.getProductId() + " quantity " + request.getQuantity());

        ManagedChannel inventoryChannel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();

        InventoryServiceGrpc.InventoryServiceBlockingStub inventoryStub =
                InventoryServiceGrpc.newBlockingStub(inventoryChannel);

        MessageTypes.ProductRequest stockRequest = MessageTypes.ProductRequest.newBuilder()
                .setProductId(request.getProductId())
                .build();

        try {
            MessageTypes.StockResponse stockResponse = inventoryStub.getStockInfo(stockRequest);
            int available = stockResponse.getStock();
            logger.info("select result：item " + request.getProductId() + " inventory " + available);


            if (available >= request.getQuantity()) {
                logger.info("can sale");
                SaleResponse response = SaleResponse.newBuilder()
                        .setSuccess(true)
                        .setMessage("success" + (available - request.getQuantity()))
                        .build();
                responseObserver.onNext(response);
            } else {
                logger.warning("fail");
                SaleResponse response = SaleResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("inventory not enough" + available)
                        .build();
                responseObserver.onNext(response);
            }

        } catch (StatusRuntimeException e) {
            logger.severe("fail" + e.getMessage());
            String errMsg;
            if (e.getStatus().getCode() == Status.Code.UNAVAILABLE) {
                errMsg = "error";
            } else if (e.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED) {
                errMsg = "time out";
            } else {
                errMsg = "exception" + e.getMessage();
            }

            SaleResponse response = SaleResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(errMsg)
                    .build();
            responseObserver.onNext(response);
        } finally {
            responseObserver.onCompleted();
            inventoryChannel.shutdown();
        }
    }
}
