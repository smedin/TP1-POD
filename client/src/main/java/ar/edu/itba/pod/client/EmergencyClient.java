package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.callbacks.emergency.CareAllPatientsCallback;
import ar.edu.itba.pod.client.callbacks.emergency.CarePatientCallback;
import ar.edu.itba.pod.grpc.emergency.EmergencyServiceGrpc;
import ar.edu.itba.pod.grpc.emergency.EndEmergencyData;
import ar.edu.itba.pod.grpc.emergency.ListEmergencyData;
import ar.edu.itba.pod.grpc.emergency.RoomNumber;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EmergencyClient {
    private static Logger logger = LoggerFactory.getLogger(EmergencyClient.class);
    private static CountDownLatch latch;

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        String action = System.getProperty("action");

        EmergencyServiceGrpc.EmergencyServiceFutureStub stub = EmergencyServiceGrpc.newFutureStub(channel);

        switch (action) {
            case "carePatient":
                latch = new CountDownLatch(1);
                RoomNumber roomNumber = RoomNumber
                        .newBuilder()
                        .setRoomNumber(Integer.parseInt(System.getProperty("room")))
                        .build();
                ListenableFuture<EndEmergencyData> emergencyResponse = stub.startEmergencyByRoom(roomNumber);
                Futures.addCallback(emergencyResponse, new CarePatientCallback(logger, latch), Executors.newCachedThreadPool());
                break;
            case "careAllPatients":
                latch = new CountDownLatch(1);
                ListenableFuture<ListEmergencyData> allEmergenciesResponse = stub.startAllEmergencies(Empty.newBuilder().build());
                Futures.addCallback(allEmergenciesResponse, new CareAllPatientsCallback(logger, latch), Executors.newCachedThreadPool());
                break;
            case "dischargePatient":
                latch = new CountDownLatch(1);
                break;
            default:
                break;
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Error waiting for latch", e);
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }
    }
}
