package com.amy.inventory;
import io.grpc.stub.StreamObserver;
import srms.InventoryServiceGrpc;
import srms.MessageTypes.ProductRequest;
import srms.MessageTypes.StockResponse;
import srms.MessageTypes.ReturnRequest;
import srms.MessageTypes.StockUpdateResponse;

public class InventoryServiceImpl implements InventoryServiceGrpc.AsyncService {

    @Override
    public void getStockInfo(ProductRequest request, StreamObserver<StockResponse> responseObserver) {
        StockResponse response = StockResponse.newBuilder()
                .setStock(100)
                .setProductId(request.getProductId())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void streamStockUpdates(ProductRequest request, StreamObserver<StockResponse> responseObserver) {
        for (int i = 0; i < 3; i++) {
            responseObserver.onNext(StockResponse.newBuilder()
                    .setStock(100 - i)
                    .setProductId(request.getProductId())
                    .build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void restoreStock(ReturnRequest request, StreamObserver<StockUpdateResponse> responseObserver) {
        StockUpdateResponse response = StockUpdateResponse.newBuilder()
                .setSuccess(true)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
