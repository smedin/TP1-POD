package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.callbacks.query.RoomListCallback;
import ar.edu.itba.pod.grpc.query.QueryServiceGrpc;
import ar.edu.itba.pod.grpc.query.RoomList;
import ar.edu.itba.pod.grpc.query.RoomNumber;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class QueryClient {
    private static Logger logger = LoggerFactory.getLogger(QueryClient.class);
    private static CountDownLatch latch;

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        String action = System.getProperty("action");

        QueryServiceGrpc.QueryServiceFutureStub stub = QueryServiceGrpc.newFutureStub(channel);

        switch (action) {
            case "queryRooms" -> {
                latch = new CountDownLatch(1);
                ListenableFuture<RoomList> roomList = stub.getRooms(Empty.newBuilder().build());
                String filePath = System.getProperty("outPath");
                Futures.addCallback(roomList, new RoomListCallback(logger, latch, filePath), Executors.newCachedThreadPool());
            }
            case "queryWaitingRoom" -> {
                latch = new CountDownLatch(1);
                stub.getWaitingRoom(Empty.newBuilder().build());
            }
            case "queryCares" -> {
                latch = new CountDownLatch(1);
                stub.getFinalizedEmergencies(RoomNumber.newBuilder().build());
            }
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Error waiting for the response", e);
        } finally {
            channel.shutdown();
        }
    }
}
