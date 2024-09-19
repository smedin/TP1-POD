package ar.edu.itba.pod.server.servants;

import ar.edu.itba.pod.grpc.admin.*;
import ar.edu.itba.pod.server.exceptions.DoctorAlreadyExistsException;
import ar.edu.itba.pod.server.models.Hospital;
import ar.edu.itba.pod.server.models.Room;
import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;

import java.util.Optional;

public class AdminServant extends AdminServiceGrpc.AdminServiceImplBase {
    private final Hospital hospital;
    private int roomCounter = 1;


    public AdminServant(Hospital hospital) {
        this.hospital = hospital;
    }

    @Override
    public void addRoom(Empty request, StreamObserver<BoolValue> response) {
        hospital.addRoom(new Room(roomCounter++));

        response.onNext(BoolValue.newBuilder().setValue(true).build());
        response.onCompleted();
    }

    @Override
    public void addDoctor(DoctorData request, StreamObserver<BoolValue> response) throws DoctorAlreadyExistsException{
        String doctorName = request.getDoctorName().getName();
        int maxLevel = Integer.parseInt(request.getLevel());

        if (maxLevel < 1 || maxLevel > 5) { // TODO: move inside Doctor (model) class
            response.onNext(BoolValue.newBuilder().setValue(false).build());  // Or throw an exception
            response.onCompleted();
            return;
        }

        // TODO: Change the doctor message in the .proto to avoid this import
        ar.edu.itba.pod.server.models.Doctor doctor = new ar.edu.itba.pod.server.models.Doctor(doctorName, maxLevel);

        boolean added = hospital.addDoctor(doctor);
        if (!added) {
            throw new DoctorAlreadyExistsException(doctorName);
        }

        response.onNext(BoolValue.newBuilder().setValue(true).build()); // TODO: check
        response.onCompleted();
    }

    @Override
    public void defineAvailability(DoctorAvailability request, StreamObserver<BoolValue> response) {
        String doctorName = request.getDoctorName().getName();
        String availability = request.getAvailability(); // TODO: change to enum

        boolean defined = hospital.defineDoctorAvailability(doctorName, availability); // TODO: make defineAvailability inside Hospital syncronized (use writeLock)

        response.onNext(BoolValue.newBuilder().setValue(defined).build());
        response.onCompleted();
    }

    @Override
    public void getDoctorAvailability(Doctor request, StreamObserver<DoctorAvailability> response) {
        String doctorName = request.getName();

        String availability = hospital.getDoctorAvailability(doctorName); // TODO: check

        DoctorAvailability toReturn = DoctorAvailability.newBuilder().setDoctorName(request).setAvailability(availability).build();

        response.onNext(toReturn);
        response.onCompleted();
    }
}
