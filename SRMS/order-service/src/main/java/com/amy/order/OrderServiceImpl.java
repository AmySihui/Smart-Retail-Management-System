package com.amy.order;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import srms.InventoryServiceGrpc;
import srms.MessageTypes;
import srms.OrderServiceGrpc;
import srms.MessageTypes.UserRequest;
import srms.MessageTypes.OrderList;
import srms.MessageTypes.ReturnRequest;
import srms.MessageTypes.ReturnResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class OrderServiceImpl implements OrderServiceGrpc.AsyncService {
    private static final Logger logger = Logger.getLogger(OrderServiceImpl.class.getName());

    @Override
    public StreamObserver<ReturnRequest> processReturns(StreamObserver<ReturnResponse> responseObserver) {

        return new StreamObserver<ReturnRequest>() {
            int successCount = 0;

            @Override
            public void onNext(ReturnRequest request) {
                logger.info("receive return order" + request.getOrderId() + " item " + request.getProductId());

                ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                        .usePlaintext()
                        .build();

                InventoryServiceGrpc.InventoryServiceBlockingStub stub = InventoryServiceGrpc.newBlockingStub(channel);

                try {
                    MessageTypes.StockUpdateResponse restoreResult = stub.restoreStock(request);
                    if (restoreResult.getSuccess()) {
                        logger.info("recover success：" + request.getProductId());
                        successCount++;
                    } else {
                        logger.warning("recover fail" + request.getProductId());
                    }
                } catch (StatusRuntimeException e) {
                    logger.severe("error" + e.getMessage());
                } finally {
                    channel.shutdown();
                }
            }

            @Override
            public void onError(Throwable t) {
                logger.severe("client error" + t.getMessage());
            }

            @Override
            public void onCompleted() {
                ReturnResponse response = ReturnResponse.newBuilder()
                        .setSuccess(true)
                        .setMessage("return count" + successCount)
                        .build();
                logger.info("finish" + successCount);
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }

}
