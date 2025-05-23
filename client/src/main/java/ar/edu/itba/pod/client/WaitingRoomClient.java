package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.callbacks.waitingRoom.AddPatientCallback;
import ar.edu.itba.pod.client.callbacks.waitingRoom.CheckPatientCallback;
import ar.edu.itba.pod.client.callbacks.waitingRoom.UpdateLevelCallback;
import ar.edu.itba.pod.grpc.waitingRoom.PatientName;
import ar.edu.itba.pod.grpc.waitingRoom.PatientData;
import ar.edu.itba.pod.grpc.waitingRoom.WaitingRoomServiceGrpc;
import ar.edu.itba.pod.grpc.waitingRoom.TimeData;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WaitingRoomClient {
    private static final Logger logger = LoggerFactory.getLogger(WaitingRoomClient.class);
    private static CountDownLatch latch;

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(System.getProperty("serverAddress"))
                .usePlaintext()
                .build();

        String action = System.getProperty("action");

        WaitingRoomServiceGrpc.WaitingRoomServiceFutureStub stub = WaitingRoomServiceGrpc.newFutureStub(channel);
        ExecutorService executorService = Executors.newCachedThreadPool();

        switch (action) {
            case "addPatient":
                latch = new CountDownLatch(1);
                PatientData patientData = PatientData
                        .newBuilder()
                        .setPatientName(PatientName
                                .newBuilder()
                                .setName(System.getProperty("patient"))
                                .build())
                        .setLevel(Integer.parseInt(System.getProperty("level")))
                        .build();
                ListenableFuture<Empty> patientResponse = stub.registerPatient(patientData);
                Futures.addCallback(patientResponse, new AddPatientCallback(logger, latch, patientData), executorService);
                break;
            case "updateLevel":
                latch = new CountDownLatch(1);
                PatientData updatedPatientData = PatientData
                        .newBuilder()
                        .setPatientName(PatientName
                                .newBuilder()
                                .setName(System.getProperty("patient"))
                                .build())
                        .setLevel(Integer.parseInt(System.getProperty("level")))
                        .build();
                ListenableFuture<Empty> levelResponse = stub.updateEmergencyLevel(updatedPatientData);
                Futures.addCallback(levelResponse, new UpdateLevelCallback(logger, latch, updatedPatientData), executorService);
                break;
            case "checkPatient":
                latch = new CountDownLatch(1);
                PatientName patient = PatientName
                        .newBuilder()
                        .setName(System.getProperty("patient"))
                        .build();
                ListenableFuture<TimeData> checkResponse = stub.waitingTime(patient);
                Futures.addCallback(checkResponse, new CheckPatientCallback(logger, latch), executorService);
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
            executorService.shutdown();
        }
    }

}
