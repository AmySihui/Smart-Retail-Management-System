package com.amy.inventory;
import io.grpc.stub.StreamObserver;
import srms.InventoryServiceGrpc;
import srms.MessageTypes.ProductRequest;
import srms.MessageTypes.StockResponse;
import srms.MessageTypes.ReturnRequest;
import srms.MessageTypes.StockUpdateResponse;

import java.util.logging.Logger;

public class InventoryServiceImpl implements InventoryServiceGrpc.AsyncService {

    private static final Logger logger = Logger.getLogger(InventoryServiceImpl.class.getName());


    @Override
    public void getStockInfo(ProductRequest request, StreamObserver<StockResponse> responseObserver) {
        logger.info("receive inventory request: item: " + request.getProductId());

        try {
            StockResponse response = StockResponse.newBuilder()
                    .setStock(100)
                    .setProductId(request.getProductId())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            logger.info("return inventory info：100");
        } catch (Exception e) {
            logger.severe("fail select:" + e.getMessage());
            responseObserver.onError(e);
        }
    }

    @Override
    public void streamStockUpdates(ProductRequest request, StreamObserver<StockResponse> responseObserver) {
        logger.info("receive inventory stream request: item" + request.getProductId());

        try {
            for (int i = 0; i < 3; i++) {
                StockResponse update = StockResponse.newBuilder()
                        .setProductId(request.getProductId())
                        .setStock(100 - i)
                        .build();
                responseObserver.onNext(update);
            }
            responseObserver.onCompleted();
            logger.info("inventory renew success");
        } catch (Exception e) {
            logger.severe("inventory renew error" + e.getMessage());
            responseObserver.onError(e);
        }
    }


    @Override
    public void restoreStock(ReturnRequest request, StreamObserver<StockUpdateResponse> responseObserver) {
        logger.info("receive recover request: order" + request.getOrderId() + "item" + request.getProductId());

        try {
            StockUpdateResponse response = StockUpdateResponse.newBuilder()
                    .setSuccess(true)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            logger.info("inventory recover success");
        } catch (Exception e) {
            logger.severe("inventory recover fail" + e.getMessage());
            responseObserver.onError(e);
        }
    }

}
