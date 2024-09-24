package ar.edu.itba.pod.server.servants;

import ar.edu.itba.pod.grpc.notifications.Notification;
import ar.edu.itba.pod.grpc.notifications.NotificationServiceGrpc;
import ar.edu.itba.pod.grpc.notifications.Registration;
import ar.edu.itba.pod.server.exceptions.DoctorNotFoundException;
import ar.edu.itba.pod.server.models.Doctor;
import ar.edu.itba.pod.server.models.Hospital;
import ar.edu.itba.pod.server.models.NotificationManager;
import io.grpc.stub.StreamObserver;

public class NotificationServant extends NotificationServiceGrpc.NotificationServiceImplBase {
    private final Hospital hospital;
    private final NotificationManager notificationManager;

    public NotificationServant(Hospital hospital, NotificationManager notificationManager) {
        this.hospital = hospital;
        this.notificationManager = notificationManager;
    }

    @Override
    public void registerDoctor(Registration request, StreamObserver<Notification> responseObserver) {
        String doctorName = request.getDoctorName();
        Doctor doctor = hospital.getDoctorByName(doctorName).orElseThrow(() -> new DoctorNotFoundException(doctorName)); //TODO: check if it's correct to check doctor existance here
        notificationManager.registerDoctor(doctorName, responseObserver);
        notificationManager.notifyRegistration(doctor);
    }

    @Override
    public void unregisterDoctor(Registration request, StreamObserver<Notification> responseObserver) {
        String doctorName = request.getDoctorName();
        Doctor doctor = hospital.getDoctorByName(doctorName).get(); //TODO: check if could not be present. Logic says it must be.
        StreamObserver<Notification> notificationStreamObserver = notificationManager.UnregisterDoctor(doctorName);
        //TODO: check if it's okey to return the message to both ends
        String message = String.format("%s (%d) unregistered successfully for page", doctorName, doctor.getMaxLevel());
        notificationStreamObserver.onNext(Notification.newBuilder().setMessage(message).build());
        notificationStreamObserver.onCompleted();
        responseObserver.onNext(Notification.newBuilder().setMessage(message).build());
        responseObserver.onCompleted();
    }
}
