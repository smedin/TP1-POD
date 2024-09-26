package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.callbacks.query.EmergencyListCallback;
import ar.edu.itba.pod.client.callbacks.query.PatientListCallback;
import ar.edu.itba.pod.client.callbacks.query.RoomListCallback;
import ar.edu.itba.pod.grpc.emergency.EmergencyServiceGrpc;
import ar.edu.itba.pod.grpc.query.*;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class QueryClient {
    private static final Logger logger = LoggerFactory.getLogger(QueryClient.class);
    private static CountDownLatch latch;

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(System.getProperty("serverAddress"))
                .usePlaintext()
                .build();

        String action = System.getProperty("action");

        QueryServiceGrpc.QueryServiceFutureStub stub = QueryServiceGrpc.newFutureStub(channel);
        ExecutorService executorService = Executors.newCachedThreadPool();

        String filePath = System.getProperty("outPath");

        switch (action) {
            case "queryRooms" -> {
                latch = new CountDownLatch(1);
                ListenableFuture<RoomList> roomList = stub.getRooms(Empty.newBuilder().build());
                Futures.addCallback(roomList, new RoomListCallback(logger, latch, filePath), executorService);
            }
            case "queryWaitingRoom" -> {
                latch = new CountDownLatch(1);
                ListenableFuture<PatientList> patientList = stub.getWaitingRoom(Empty.newBuilder().build());
                Futures.addCallback(patientList, new PatientListCallback(logger, latch, filePath), executorService);
            }
            case "queryCares" -> {
                latch = new CountDownLatch(1);
                String roomNumber = System.getProperty("room");
                RoomNumber roomNumberReq = RoomNumber.newBuilder().setRoomNumber(roomNumber!=null?Integer.parseInt(roomNumber):-1).build();
                ListenableFuture<EmergencyList> emergencyList = stub.getFinalizedEmergencies(roomNumberReq);
                Futures.addCallback(emergencyList, new EmergencyListCallback(logger, latch, filePath), executorService);
            }
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Error waiting for the response", e);
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
            executorService.shutdown();
        }
    }
}
