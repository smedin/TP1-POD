package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.callbacks.emergency.CareAllPatientsCallback;
import ar.edu.itba.pod.client.callbacks.emergency.CarePatientCallback;
import ar.edu.itba.pod.client.callbacks.emergency.DischargeEmergencyCallback;
import ar.edu.itba.pod.grpc.emergency.*;
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

public class EmergencyClient {
    private static final Logger logger = LoggerFactory.getLogger(EmergencyClient.class);
    private static CountDownLatch latch;

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(System.getProperty("serverAddress"))
                .usePlaintext()
                .build();

        String action = System.getProperty("action");

        EmergencyServiceGrpc.EmergencyServiceFutureStub stub = EmergencyServiceGrpc.newFutureStub(channel);
        ExecutorService executorService = Executors.newCachedThreadPool();

        switch (action) {
            case "carePatient" -> {
                latch = new CountDownLatch(1);
                RoomNumber roomNumber = RoomNumber
                        .newBuilder()
                        .setRoomNumber(Integer.parseInt(System.getProperty("room")))
                        .build();
                ListenableFuture<EndEmergencyData> emergencyResponse = stub.startEmergencyByRoom(roomNumber);
                Futures.addCallback(emergencyResponse, new CarePatientCallback(logger, latch), executorService);
            }
            case "careAllPatients" ->{
                latch = new CountDownLatch(1);
                ListenableFuture<ListEmergencyData> allEmergenciesResponse = stub.startAllEmergencies(Empty.newBuilder().build());
                Futures.addCallback(allEmergenciesResponse, new CareAllPatientsCallback(logger, latch), executorService);
            }

            case "dischargePatient" -> {
                latch = new CountDownLatch(1);
                RoomNumber roomNumber = RoomNumber
                        .newBuilder()
                        .setRoomNumber(Integer.parseInt(System.getProperty("room")))
                        .build();
                RoomData roomData = RoomData
                        .newBuilder()
                        .setDoctor(PersonData.newBuilder().setName(System.getProperty("doctor")).build()).setPatient(PersonData.newBuilder().setName(System.getProperty("patient")).build()).build();
                EndEmergencyData emergencyData = EndEmergencyData.newBuilder().setRoomNumber(roomNumber).setRoomData(roomData).build();
                ListenableFuture<EndEmergencyData> dischargeResponse = stub.endEmergency(emergencyData);
                Futures.addCallback(dischargeResponse, new DischargeEmergencyCallback(logger, latch), executorService);
            }
            default -> {
                System.exit(1);
            }
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Error waiting for latch", e);
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
            executorService.shutdown();
        }
    }
}
