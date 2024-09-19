package ar.edu.itba.pod.client;

import ar.edu.itba.pod.grpc.admin.AdminServiceGrpc;
import ar.edu.itba.pod.grpc.admin.Doctor;
import ar.edu.itba.pod.grpc.admin.DoctorAvailability;
import ar.edu.itba.pod.grpc.admin.DoctorData;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AdminClient {
    private static Logger logger = LoggerFactory.getLogger(AdminClient.class);
    private static CountDownLatch latch;

    public static void main(String[] args) throws InterruptedException {
        logger.info("TP1-POD Client Starting ...");
        logger.info("grpc-com-patterns Client Starting ...");
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        String action = System.getProperty("action");

        AdminServiceGrpc.AdminServiceFutureStub stub = AdminServiceGrpc.newFutureStub(channel);

        switch (action) {
            case "addRoom":
                latch = new CountDownLatch(1);
                ListenableFuture<BoolValue> roomResponse = stub.addRoom(Empty.newBuilder().build());
                // TODO: do a callback class in another file
                Futures.addCallback(roomResponse, new FutureCallback<BoolValue>() {
                    @Override
                    public void onSuccess(BoolValue result) {
                        logger.info("Room added: " + result.getValue());
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        logger.error(t.getMessage());
                        latch.countDown();
                    }
                }, Executors.newCachedThreadPool());
                break;
            case "addDoctor":
                latch = new CountDownLatch(1);
                ListenableFuture<BoolValue> doctorResponse = stub.addDoctor(DoctorData
                                .newBuilder()
                                .setDoctorName(Doctor
                                                .newBuilder()
                                                .setName(System.getProperty("doctor"))
                                                .build()
                                )
                                .setLevel(System.getProperty("level"))
                                .build());
                Futures.addCallback(doctorResponse, new FutureCallback<BoolValue>() {
                    @Override
                    public void onSuccess(BoolValue result) {
                        logger.info("Doctor added: " + result.getValue());
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        logger.error(t.getMessage());
                        latch.countDown();
                    }
                }, Executors.newCachedThreadPool());
                break;
            case "defineAvailability":
                latch = new CountDownLatch(1);
                ListenableFuture<BoolValue> availabilityResponse = stub.defineAvailability(DoctorAvailability
                        .newBuilder()
                        .setDoctorName(Doctor.newBuilder().setName(System.getProperty("doctor")).build())
                        .setAvailability(System.getProperty("availability"))
                        .build());
                Futures.addCallback(availabilityResponse, new FutureCallback<BoolValue>() {
                    @Override
                    public void onSuccess(BoolValue result) {
                        logger.info("Availability defined: " + result.getValue());
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        logger.error("Error defining availability: " + t.getMessage());
                        latch.countDown();
                    }
                }, Executors.newCachedThreadPool());
                break;
            case "getDoctorAvailability":
                latch = new CountDownLatch(1);
                ListenableFuture<DoctorAvailability> doctorAvailabilityResponse = stub.getDoctorAvailability(ar.edu.itba.pod.grpc.admin.Doctor
                        .newBuilder()
                        .setName(System.getProperty("doctor"))
                        .build());
                Futures.addCallback(doctorAvailabilityResponse, new FutureCallback<DoctorAvailability>() {
                    @Override
                    public void onSuccess(DoctorAvailability result) {
                        logger.info("Doctor availability: " + result.getAvailability());
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        logger.error("Error getting doctor availability: " + t.getMessage());
                        latch.countDown();
                    }
                }, Executors.newCachedThreadPool());
                break;
            default:
                break;
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Error waiting for latch: " + e.getMessage());
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }
    }
}
