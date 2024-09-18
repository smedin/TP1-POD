package ar.edu.itba.pod.client;

import ar.edu.itba.pod.grpc.admin.AdminServiceGrpc;
import ar.edu.itba.pod.grpc.admin.DoctorAvailability;
import ar.edu.itba.pod.grpc.admin.DoctorData;
import ar.edu.itba.pod.grpc.admin.RoomData;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.BoolValue;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AdminClient {
    private static Logger logger = LoggerFactory.getLogger(Client.class);
    private static CountDownLatch latch;

    public static void main(String[] args) throws InterruptedException {
        logger.info("TP1-POD Client Starting ...");
        logger.info("grpc-com-patterns Client Starting ...");
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        String action = "addRoom";

        AdminServiceGrpc.AdminServiceFutureStub stub = AdminServiceGrpc.newFutureStub(channel);

        switch (action) {
            case "addRoom":
                latch = new CountDownLatch(1);
                ListenableFuture<BoolValue> roomResponse = stub.addRoom(RoomData.newBuilder().build());
                // TODO: do a callback class in another file
                Futures.addCallback(roomResponse, new FutureCallback<BoolValue>() {
                    @Override
                    public void onSuccess(BoolValue result) {
                        logger.info("Room added: " + result.getValue());
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        logger.error("Error adding room: " + t.getMessage());
                        latch.countDown();
                    }
                }, Executors.newCachedThreadPool());
            case "addDoctor":
                latch = new CountDownLatch(1);
                ListenableFuture<BoolValue> doctorResponse = stub.addDoctor(DoctorData.newBuilder().build());
                Futures.addCallback(doctorResponse, new FutureCallback<BoolValue>() {
                    @Override
                    public void onSuccess(BoolValue result) {
                        logger.info("Doctor added: " + result.getValue());
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        logger.error("Error adding doctor: " + t.getMessage());
                        latch.countDown();
                    }
                }, Executors.newCachedThreadPool());
            case "defineAvailability":
                latch = new CountDownLatch(1);
                ListenableFuture<BoolValue> availabilityResponse = stub.defineAvailability(ar.edu.itba.pod.grpc.admin.DoctorAvailability.newBuilder().build());
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
            case "getDoctorAvailability":
                latch = new CountDownLatch(1);
                ListenableFuture<DoctorAvailability> doctorAvailabilityResponse = stub.getDoctorAvailability(ar.edu.itba.pod.grpc.admin.Doctor.newBuilder().build());
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
