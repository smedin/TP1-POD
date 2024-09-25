package ar.edu.itba.pod.server.servants;

import ar.edu.itba.pod.grpc.admin.*;
import ar.edu.itba.pod.server.models.*;
import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import io.grpc.stub.StreamObserver;

public class AdminServant extends AdminServiceGrpc.AdminServiceImplBase {
    private final Hospital hospital;
    private final NotificationManager notificationManager;
    private int roomCounter = 1;


    public AdminServant(Hospital hospital, NotificationManager notificationManager) {
        this.hospital = hospital;
        this.notificationManager = notificationManager;
    }

    @Override
    public void addRoom(Empty request, StreamObserver<Int32Value> response) {
        hospital.addRoom(new Room(roomCounter++));

        response.onNext(Int32Value.newBuilder().setValue(roomCounter-1).build());
        response.onCompleted();
    }

    @Override
    public void addDoctor(DoctorData request, StreamObserver<BoolValue> response) {
        String doctorName = request.getDoctorName().getName();
        int maxLevel = request.getLevel();

        Doctor doctor = new Doctor(doctorName, maxLevel);

        boolean added = hospital.addDoctor(doctor);
        response.onNext(BoolValue.newBuilder().setValue(added).build()); // TODO: check
        response.onCompleted();
    }

    @Override
    public void defineAvailability(DoctorAvailability request, StreamObserver<DoctorData> response) {
        String doctorName = request.getDoctorName().getName();
        Availability availability = Availability.valueOf(request.getAvailability().toUpperCase());

        Doctor doctor = hospital.defineDoctorAvailability(doctorName, availability); // TODO: make defineAvailability inside Hospital syncronized (use writeLock)

        response.onNext(DoctorData
                .newBuilder()
                .setDoctorName(DoctorName.newBuilder().setName(doctorName).build())
                .setLevel(doctor.getMaxLevel())
                .build());
        response.onCompleted();

        notificationManager.notifyAvailabilityDefinition(doctor, availability);
    }

    @Override
    public void getDoctorAvailability(DoctorName request, StreamObserver<DoctorAvailabilityResponse> response) {
        String doctorName = request.getName();

        Doctor doctor = hospital.getDoctorByName(doctorName);

        DoctorAvailabilityResponse toReturn = DoctorAvailabilityResponse
                .newBuilder()
                .setDoctor(DoctorData.newBuilder().setDoctorName(DoctorName.newBuilder().setName(doctor.getName())).setLevel(doctor.getMaxLevel()).build())
                .setAvailability(doctor.getAvailability().toString())
                .build();

        response.onNext(toReturn);
        response.onCompleted();
    }
}
