package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.callbacks.admin.*;
import ar.edu.itba.pod.grpc.admin.*;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class AdminClient {
    private static Logger logger = LoggerFactory.getLogger(AdminClient.class);
    private static CountDownLatch latch;

    public static void main(String[] args) throws InterruptedException {
        logger.info("TP1-POD Client Starting ...");
        logger.info("grpc-com-patterns Client Starting ...");
        ManagedChannel channel = ManagedChannelBuilder.forTarget(System.getProperty("serverAddress"))
                .usePlaintext()
                .build();

        String action = System.getProperty("action");

        AdminServiceGrpc.AdminServiceFutureStub stub = AdminServiceGrpc.newFutureStub(channel);

        switch (action) {
            case "addRoom":
                latch = new CountDownLatch(1);
                ListenableFuture<Int32Value> roomResponse = stub.addRoom(Empty.newBuilder().build());
                Futures.addCallback(roomResponse, new AddRoomCallback(logger, latch), Executors.newCachedThreadPool());
                break;
            case "addDoctor":
                latch = new CountDownLatch(1);
                DoctorData doctorData = DoctorData
                        .newBuilder()
                        .setDoctorName(DoctorName
                                .newBuilder()
                                .setName(System.getProperty("doctor"))
                                .build()
                        )
                        .setLevel(Integer.parseInt(System.getProperty("level")))
                        .build();
                ListenableFuture<BoolValue> doctorResponse = stub.addDoctor(doctorData);
                Futures.addCallback(doctorResponse, new AddDoctorCallback(logger, latch, doctorData), Executors.newCachedThreadPool());
                break;
            case "setDoctor":
                latch = new CountDownLatch(1);
                DoctorAvailability doctorAvailability = DoctorAvailability
                        .newBuilder()
                        .setDoctorName(DoctorName.newBuilder().setName(System.getProperty("doctor")).build())
                        .setAvailability(System.getProperty("availability"))
                        .build();
                ListenableFuture<DoctorData> availabilityResponse = stub.defineAvailability(doctorAvailability);
                Futures.addCallback(availabilityResponse, new DefineAvailabilityCallback(logger, latch, doctorAvailability), Executors.newCachedThreadPool());
                break;
            case "checkDoctor":
                latch = new CountDownLatch(1);
                ListenableFuture<DoctorAvailabilityResponse> doctorAvailabilityResponse = stub.getDoctorAvailability(DoctorName
                        .newBuilder()
                        .setName(System.getProperty("doctor"))
                        .build());
                Futures.addCallback(doctorAvailabilityResponse, new GetDoctorAvailabilityCallback(logger, latch), Executors.newCachedThreadPool());
                break;
            default:
                System.exit(1);
        }

        try {
            logger.info("Waiting for response");
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Error waiting for latch: " + e.getMessage());
        } finally {
            channel.shutdownNow();
        }
    }
}
