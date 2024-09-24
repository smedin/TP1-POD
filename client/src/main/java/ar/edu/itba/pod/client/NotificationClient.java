package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.callbacks.notification.UnregisterCallback;
import ar.edu.itba.pod.grpc.notifications.Notification;
import ar.edu.itba.pod.grpc.notifications.NotificationServiceGrpc;
import ar.edu.itba.pod.grpc.notifications.Registration;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class NotificationClient {
    private static Logger logger = LoggerFactory.getLogger(NotificationClient.class);
    private static CountDownLatch latch;

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        String action = System.getProperty("action");
        String doctorName = System.getProperty("doctor");

        NotificationServiceGrpc.NotificationServiceFutureStub futureStub = NotificationServiceGrpc.newFutureStub(channel);
        NotificationServiceGrpc.NotificationServiceStub stub = NotificationServiceGrpc.newStub(channel);

        switch (action) {
            case "register":
                latch = new CountDownLatch(1);
                Registration registration = Registration.newBuilder().setDoctorName(doctorName).build();
                StreamObserver<Notification> streamObserver = new StreamObserver<>() {
                    @Override
                    public void onNext(Notification notification) {
                        logger.info(notification.getMessage()); //TODO: check if should be sout
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        latch.countDown();
                        logger.error(throwable.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        latch.countDown();
                    }
                };

                stub.registerDoctor(registration, streamObserver);
                latch.await();
                break;
            case "unregister":
                latch = new CountDownLatch(1);
                Registration unregistration = Registration.newBuilder().setDoctorName(doctorName).build();
                ListenableFuture<Notification> unregisterResponse = futureStub.unregisterDoctor(unregistration);
                Futures.addCallback(unregisterResponse, new UnregisterCallback(logger, latch), Executors.newCachedThreadPool());
                break;
            default:
                System.exit(1);
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
